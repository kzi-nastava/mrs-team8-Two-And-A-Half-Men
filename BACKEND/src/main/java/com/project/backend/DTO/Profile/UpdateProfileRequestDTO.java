package com.project.backend.DTO.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private String imgSrc;
    private String model;
    private String licensePlate;
    private Integer numberOfSeats;
    private Long vehicleTypeId;
    private List<Long> additionalServiceIds;

    public String validate() {
        if (firstName != null && firstName.trim().isEmpty()) {
            return "First name cannot be empty.";
        }
        if (lastName != null && lastName.trim().isEmpty()) {
            return "Last name cannot be empty.";
        }
        if (email != null && email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        var emailValidator = new EmailValidator();
        if (email != null && !emailValidator.isValid(email, null)) {
            return "Email format is invalid.";
        }
        if (address != null && address.trim().isEmpty()) {
            return "Address cannot be empty.";
        }
        if (phoneNumber != null && phoneNumber.trim().isEmpty()) {
            return "Phone number cannot be empty.";
        }
        if (imgSrc != null && imgSrc.trim().isEmpty()) {
            return "Image source cannot be empty.";
        }
        if (model != null && model.trim().isEmpty()) {
            return "Model cannot be empty.";
        }
        if (licensePlate != null && licensePlate.trim().isEmpty()) {
            return "License plate cannot be empty.";
        }
        if (numberOfSeats != null && numberOfSeats <= 0) {
            return "Number of seats must be greater than zero.";
        }
        return null;
    }
}
