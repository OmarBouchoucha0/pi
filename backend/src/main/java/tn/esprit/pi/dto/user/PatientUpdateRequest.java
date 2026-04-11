package tn.esprit.pi.dto.user;

import java.time.LocalDate;

import lombok.Data;
import tn.esprit.pi.enums.user.Gender;
import tn.esprit.pi.enums.user.UserStatus;

@Data
public class PatientUpdateRequest {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private LocalDate dateOfBirth;

    private Gender gender;

    private UserStatus status;

    private String medicalRecordNumber;

    private String bloodType;

    private String allergies;

    private String chronicConditions;

    private String emergencyContactName;

    private String emergencyContactPhone;
}
