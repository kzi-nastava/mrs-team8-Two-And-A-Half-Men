package com.project.backend.service.rating;

public sealed interface RatingActor
permits JwtRatingActor, AccessTokenRatingActor {
}
