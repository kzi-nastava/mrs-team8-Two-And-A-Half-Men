package com.project.backend.models.actor;

import com.project.backend.models.Customer;

public record JwtPassengerActor(Customer customer) implements PassengerActor {
}
