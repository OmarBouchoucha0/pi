package tn.esprit.pi.dto.user;

import java.time.LocalDate;

import lombok.Data;
import tn.esprit.pi.enums.user.AdminPrivilege;
import tn.esprit.pi.enums.user.Gender;
import tn.esprit.pi.enums.user.UserStatus;

@Data
public class AdminUpdateRequest {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private AdminPrivilege privilegeLevel;

    private LocalDate dateOfBirth;

    private Gender gender;

    private UserStatus status;
}
