package io.gaming.platform.gameservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PvpDefeatRequest(
    @NotNull(message = "Defeated player ID is required")
    Long defeatedPlayerId,
    
    @NotNull(message = "Victor player ID is required")
    Long victorPlayerId,
    
    @NotBlank(message = "Battle location is required")
    String battleLocation
) {} 