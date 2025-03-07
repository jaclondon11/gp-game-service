package io.gaming.platform.gameservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PvpAttackRequest(
    @NotNull(message = "Attacker ID is required")
    Long attackerId,
    
    @NotNull(message = "Defender ID is required")
    Long defenderId,
    
    @NotNull(message = "Damage dealt is required")
    @Min(value = 0, message = "Damage dealt cannot be negative")
    Integer damageDealt
) {} 