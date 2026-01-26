package com.project.backend.service.rating;

import com.project.backend.models.Customer;

public record JwtRatingActor(Customer customer) implements RatingActor {
}
