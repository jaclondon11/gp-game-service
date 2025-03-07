package io.gaming.platform.gameservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChallengeCompletedRequest(
    @NotNull(message = "Player ID is required")
    Long playerId,
    
    @NotBlank(message = "Challenge name is required")
    String challengeName
) {} 