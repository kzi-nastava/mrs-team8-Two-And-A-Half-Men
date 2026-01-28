package com.project.backend.models.actor;

public sealed interface PassengerActor
permits JwtPassengerActor, AccessTokenPassengerActor {
}
