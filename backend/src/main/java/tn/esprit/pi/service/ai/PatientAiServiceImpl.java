package tn.esprit.pi.service.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.response.AiExplanationResponse;
import tn.esprit.pi.dto.response.AiPredictionResponse;
import tn.esprit.pi.entity.MedicalNote;
import tn.esprit.pi.entity.PatientState;
import tn.esprit.pi.entity.VitalParameter;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.VitalType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.MedicalNoteRepository;
import tn.esprit.pi.repository.PatientStateRepository;
import tn.esprit.pi.repository.VitalParameterRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PatientAiServiceImpl implements IPatientAiService {

    private final UserRepository           userRepo;
    private final VitalParameterRepository vitalRepo;
    private final PatientStateRepository   stateRepo;
    private final MedicalNoteRepository    noteRepo;
    private final ObjectMapper             objectMapper;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${ml.predict.url}")
    private String mlPredictUrl;

    private static final String GROQ_URL   = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODEL = "llama-3.3-70b-versatile";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ═══════════════════════════════════════════════
    // EXPLAIN  →  Groq / Llama 3.3 70B
    // ═══════════════════════════════════════════════

    @Override
    public AiExplanationResponse explainRisk(Long patientId, Long tenantId) {
        User patient       = findPatient(patientId);
        PatientState state = getLatestState(patientId, tenantId);

        List<VitalParameter> vitals = vitalRepo
                .findByPatientIdAndTenantIdAndRecordedAtAfter(patientId, tenantId,
                        LocalDateTime.now().minusHours(24));
        List<MedicalNote> notes = noteRepo.findRecentByPatient(patientId, tenantId)
                .stream().limit(5).collect(Collectors.toList());

        String aiText = callGroq(buildExplainPrompt(patient, state, vitals, notes));

        return AiExplanationResponse.builder()
                .patientId(patientId)
                .currentState(state.getState())
                .currentScore(state.getScore())
                .explanation(aiText)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ═══════════════════════════════════════════════
    // PREDICT  →  Python ML microservice
    // ═══════════════════════════════════════════════

    @Override
    public AiPredictionResponse predictEvolution(Long patientId, Long tenantId) {
        User patient        = findPatient(patientId);
        PatientState latest = getLatestState(patientId, tenantId);

        List<PatientState> history = stateRepo
                .findAllByPatientAndTenantDesc(patientId, tenantId)
                .stream().limit(10).collect(Collectors.toList());

        List<VitalParameter> recentVitals = vitalRepo
                .findByPatientIdAndTenantIdAndRecordedAtAfter(patientId, tenantId,
                        LocalDateTime.now().minusHours(12));

        Map<String, Object> features = buildFeatureVector(patient, latest, history, recentVitals);
        Map<String, Object> result   = callMlService(features);

        return AiPredictionResponse.builder()
                .patientId(patientId)
                .prediction(String.valueOf(result.getOrDefault("prediction", "STABLE")))
                .confidence(String.valueOf(result.getOrDefault("confidence", "LOW")))
                .reasoning(String.valueOf(result.getOrDefault("reasoning", "")))
                .recommendedActions(String.valueOf(result.getOrDefault("recommended_actions", "Monitor closely.")))
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ═══════════════════════════════════════════════
    // GROQ HTTP CALL
    // ═══════════════════════════════════════════════

    private String callGroq(String userMessage) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", GROQ_MODEL);
            body.put("max_tokens", 1024);
            body.put("temperature", 0.3);
            body.put("messages", List.of(
                    Map.of("role", "system",
                           "content", "You are a clinical decision support assistant. "
                                    + "Provide clear, factual, medical-grade explanations."),
                    Map.of("role", "user", "content", userMessage)
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("Groq API error " + response.statusCode() + ": " + response.body());

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Groq API: " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════
    // ML SERVICE HTTP CALL
    // ═══════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private Map<String, Object> callMlService(Map<String, Object> features) {
        try {
            String jsonBody = objectMapper.writeValueAsString(Map.of("features", features));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mlPredictUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("ML service error " + response.statusCode() + ": " + response.body());

            return objectMapper.readValue(response.body(), Map.class);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call ML prediction service: " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════
    // FEATURE VECTOR  (must match train.py exactly)
    // ═══════════════════════════════════════════════

    private Map<String, Object> buildFeatureVector(User patient, PatientState latest,
                                                    List<PatientState> history,
                                                    List<VitalParameter> vitals) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("current_score", latest.getScore());
        f.put("current_state", latest.getState().name());
        f.put("current_trend", latest.getTrend().name());

        double scoreDelta = 0.0;
        if (history.size() >= 2)
            scoreDelta = history.get(0).getScore() - history.get(1).getScore();
        f.put("score_delta", scoreDelta);

        long streak = 0;
        for (PatientState ps : history) {
            if (ps.getTrend() != null && "WORSENING".equals(ps.getTrend().name())) streak++;
            else break;
        }
        f.put("worsening_streak", streak);

        Map<VitalType, VitalParameter> latestPerType = new LinkedHashMap<>();
        for (VitalParameter vp : vitals) latestPerType.putIfAbsent(vp.getType(), vp);

        for (VitalType type : VitalType.values()) {
            VitalParameter vp = latestPerType.get(type);
            String key = type.name().toLowerCase();
            f.put(key + "_value",       vp != null ? vp.getValue() : null);
            f.put(key + "_normalized",  vp != null ? vp.getNormalizedValue() : null);
            f.put(key + "_status_code", vp != null ? encodeStatus(vp.getStatus().name()) : -1);
        }

        long criticalCount = vitals.stream().filter(v -> "CRITICAL".equals(v.getStatus().name())).count();
        long warningCount  = vitals.stream().filter(v -> "WARNING".equals(v.getStatus().name())).count();
        f.put("critical_vital_count",    criticalCount);
        f.put("warning_vital_count",     warningCount);
        f.put("has_chronic_conditions",  patient.getChronicConditions() != null ? 1 : 0);
        f.put("has_allergies",           patient.getAllergies() != null ? 1 : 0);
        return f;
    }

    private int encodeStatus(String status) {
        return switch (status) {
            case "NORMAL"   -> 0;
            case "WARNING"  -> 1;
            case "CRITICAL" -> 2;
            default         -> -1;
        };
    }

    // ═══════════════════════════════════════════════
    // PROMPT BUILDER
    // ═══════════════════════════════════════════════

    private String buildExplainPrompt(User patient, PatientState state,
                                       List<VitalParameter> vitals, List<MedicalNote> notes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Explain this patient's current health risk in clear, concise language ")
          .append("suitable for a doctor or nurse. Be factual and medical. Maximum 4 sentences.\n\n");

        sb.append("=== PATIENT ===\nName: ")
          .append(patient.getFirstName()).append(" ").append(patient.getLastName()).append("\n");
        if (patient.getDateOfBirth()       != null) sb.append("DOB: ").append(patient.getDateOfBirth()).append("\n");
        if (patient.getBloodType()         != null) sb.append("Blood type: ").append(patient.getBloodType()).append("\n");
        if (patient.getAllergies()          != null) sb.append("Allergies: ").append(patient.getAllergies()).append("\n");
        if (patient.getChronicConditions() != null) sb.append("Chronic conditions: ").append(patient.getChronicConditions()).append("\n");

        sb.append("\n=== CURRENT STATE ===\nState: ").append(state.getState())
          .append(" | Score: ").append(String.format("%.2f", state.getScore()))
          .append("/10 | Trend: ").append(state.getTrend()).append("\n")
          .append("Reason: ").append(state.getReason()).append("\n");

        if (!vitals.isEmpty()) {
            sb.append("\n=== LAST 24H VITALS ===\n");
            vitals.forEach(v -> sb.append("- ").append(v.getType().name())
                    .append(": ").append(v.getValue())
                    .append(v.getUnit() != null ? " " + v.getUnit() : "")
                    .append(" [").append(v.getStatus()).append("] at ")
                    .append(v.getRecordedAt().format(FMT)).append("\n"));
        }
        if (!notes.isEmpty()) {
            sb.append("\n=== MEDICAL NOTES ===\n");
            notes.forEach(n -> sb.append("- [").append(n.getType()).append("] ")
                    .append(n.getCreatedAt().format(FMT)).append(": ")
                    .append(n.getDiagnosisLabel() != null ? n.getDiagnosisLabel() + " — " : "")
                    .append(n.getContent()).append("\n"));
        }
        sb.append("\nExplain the patient's risk situation clearly.");
        return sb.toString();
    }

    // ═══════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════

    private User findPatient(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    private PatientState getLatestState(Long patientId, Long tenantId) {
        return stateRepo.findTopByPatientIdAndTenantIdOrderByCalculatedAtDesc(patientId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No state found for patient " + patientId + ". Run /recalculate first."));
    }
}
