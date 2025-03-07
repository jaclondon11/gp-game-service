package io.gaming.platform.gameservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LevelUpRequest(
    @NotNull(message = "Player ID is required")
    Long playerId,
    
    @NotNull(message = "New level is required")
    @Min(value = 1, message = "Level must be greater than 0")
    Integer newLevel
) {} 