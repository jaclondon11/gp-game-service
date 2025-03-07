package io.gaming.platform.gameservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemAcquiredRequest(
    @NotNull(message = "Player ID is required")
    Long playerId,
    
    @NotBlank(message = "Item name is required")
    String itemName,
    
    @NotBlank(message = "Rarity is required")
    String rarity
) {} 