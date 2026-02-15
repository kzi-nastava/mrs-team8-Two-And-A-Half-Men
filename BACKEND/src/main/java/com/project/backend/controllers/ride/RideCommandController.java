package com.project.backend.controllers.ride;

import com.project.backend.DTO.Ride.CostTimeDTO;
import com.project.backend.DTO.Ride.FinishRideDTO;
import com.project.backend.DTO.Ride.RideCancelationDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.service.IRideService;
import com.project.backend.service.impl.CancellationService;
import com.project.backend.service.impl.PanicService;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideCommandController {
    private final IRideService rideService;
    private final AuthUtils authUtils;
    private final CancellationService cancellationService;
    private final PanicService panicService;

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> startRide(
            @PathVariable String id
    ) {
        var user = authUtils.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(rideService.startARide(id,user.getId()));
    }


    @PatchMapping("/{id}/end")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> endRide(
            @PathVariable Long id
    ) {
        Driver driver = authUtils.getCurrentDriver();
        if (driver == null) {
            return ResponseEntity.status(401).body(Map.of());
        }
        CostTimeDTO costTimeDTO = rideService.endRideById(id, driver);
        System.out.println(costTimeDTO.getCost() + " " + costTimeDTO.getTime());
        return ResponseEntity.ok(costTimeDTO);
    }


    @PatchMapping("/{id}/finish")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> finishRide(
            @PathVariable Long id,
            @Valid @RequestBody FinishRideDTO finishRideDTO
    ) {
        RideResponseDTO next = rideService.finishRide(id, finishRideDTO);
        return ResponseEntity.status(HttpStatus.OK).body(next);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(
            @PathVariable Long id,
            @RequestBody RideCancelationDTO reason
    ) {
        AppUser user = authUtils.getCurrentUser();
        if(user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
        }
        cancellationService.cancelRide(id, reason, user);
        return ResponseEntity.ok(Map.of("message", "Ride cancelled successfully"));
    }
    @PostMapping("/{id}/panic/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolvePanic(
            @PathVariable Long id
    ) {
        panicService.resolvePanicAlert(id);
        return ResponseEntity.ok(Map.of("message", "Panic alert resolved successfully"));
    }

}
