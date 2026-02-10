package com.project.backend.DTO.filters;

import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class RideSpecification {

    public static Specification<Ride> withFilter(RideFilter filter) {
        return Specification
                .where(hasDriverId(filter.getDriverId()))
                .and(hasCustomerId(filter.getCustomerId()))
                .and(createdAfter(filter.getStartDate()))
                .and(createdBefore(filter.getEndDate()))
                .and(hasStatus(filter.getStatus()))
                .and(hasVehicleType(filter.getVehicleTypeId()))
                .and(priceGreaterThan(filter.getMinPrice()))
                .and(priceLessThan(filter.getMaxPrice()))
                .and(distanceGreaterThan(filter.getMinDistance()))
                .and(distanceLessThan(filter.getMaxDistance()))
                .and(isScheduled(filter.getIsScheduled()))
                .and(isCancelled(filter.getIsCancelled()));
    }

    private static Specification<Ride> hasCustomerId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;

            Predicate asRideOwner = cb.equal(root.get("rideOwner").get("id"), userId);

            Predicate asDriver = cb.equal(root.get("driver").get("id"), userId);

            Subquery<Long> passengerSubquery = query.subquery(Long.class);
            Root<Passenger> passenger = passengerSubquery.from(Passenger.class);
            passengerSubquery.select(passenger.get("ride").get("id"))
                    .where(cb.equal(passenger.get("user").get("id"), userId));

            Predicate asPassenger = cb.in(root.get("id")).value(passengerSubquery);

            return cb.or(asRideOwner, asDriver, asPassenger);
        };
    }

    private static Specification<Ride> hasDriverId(Long driverId) {
        return (root, query, cb) ->
                driverId == null ? null : cb.equal(root.get("driver").get("id"), driverId);
    }

    private static Specification<Ride> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    private static Specification<Ride> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }

    private static Specification<Ride> hasStatus(RideStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Ride> hasVehicleType(Long vehicleTypeId) {
        return (root, query, cb) ->
                vehicleTypeId == null ? null : cb.equal(root.get("vehicleType").get("id"), vehicleTypeId);
    }

    private static Specification<Ride> priceGreaterThan(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<Ride> priceLessThan(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<Ride> distanceGreaterThan(Double minDistance) {
        return (root, query, cb) ->
                minDistance == null ? null : cb.greaterThanOrEqualTo(root.get("distanceKm"), minDistance);
    }

    private static Specification<Ride> distanceLessThan(Double maxDistance) {
        return (root, query, cb) ->
                maxDistance == null ? null : cb.lessThanOrEqualTo(root.get("distanceKm"), maxDistance);
    }

    private static Specification<Ride> isScheduled(Boolean isScheduled) {
        return (root, query, cb) -> {
            if (isScheduled == null) return null;
            if (isScheduled) {
                return cb.isNotNull(root.get("scheduledTime"));
            } else {
                return cb.isNull(root.get("scheduledTime"));
            }
        };
    }

    private static Specification<Ride> isCancelled(Boolean isCancelled) {
        return (root, query, cb) -> {
            if (isCancelled == null) return null;
            if (isCancelled) {
                return cb.isNotNull(root.get("cancellationReason"));
            } else {
                return cb.isNull(root.get("cancellationReason"));
            }
        };
    }
}