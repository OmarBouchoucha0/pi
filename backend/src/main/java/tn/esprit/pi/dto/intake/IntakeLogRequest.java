package tn.esprit.pi.dto.intake;

import java.time.LocalDateTime;

import tn.esprit.pi.enums.intake.IntakeStatus;

public class IntakeLogRequest {

    private Long prescriptionId;
    private LocalDateTime scheduledTime;
    private LocalDateTime takenAt;
    private IntakeStatus status;
    private Integer delayMinutes;
    private Double doseTaken;

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }

    public IntakeStatus getStatus() {
        return status;
    }

    public void setStatus(IntakeStatus status) {
        this.status = status;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public Double getDoseTaken() {
        return doseTaken;
    }

    public void setDoseTaken(Double doseTaken) {
        this.doseTaken = doseTaken;
    }
}
