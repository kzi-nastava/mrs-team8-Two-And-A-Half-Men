package com.project.backend.DTO.Profile;


import com.project.backend.models.UpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUpdateRequestListItemDTO {
    private Long id;

    private Long driverId;
    private String driverFirstName;
    private String driverLastName;
    private String driverEmail;

    boolean hasPersonalInfoChanges;
    boolean hasVehicleInfoChanges;

    public AdminUpdateRequestListItemDTO(UpdateRequest request) {
        this.id = request.getId();
        this.driverId = request.getDriver().getId();
        this.driverFirstName = request.getDriver().getFirstName();
        this.driverLastName = request.getDriver().getLastName();
        this.driverEmail = request.getDriver().getEmail();
        this.hasPersonalInfoChanges = checkPersonalInfoChanges(request);
        this.hasVehicleInfoChanges = checkVehicleInfoChanges(request);
    }

    private boolean checkPersonalInfoChanges(UpdateRequest request) {
        return request.getFirstName() != null ||
               request.getLastName() != null ||
               request.getEmail() != null ||
               request.getAddress() != null ||
               request.getPhoneNumber() != null;
    }
    private boolean checkVehicleInfoChanges(UpdateRequest request) {
        return request.getModel() != null ||
               request.getLicensePlate() != null ||
               (request.getNumberOfSeats() != null && request.getNumberOfSeats() > 0) ||
               request.getVehicleType() != null ||
               (request.getAdditionalServices() != null && !request.getAdditionalServices().isEmpty());
    }
}
