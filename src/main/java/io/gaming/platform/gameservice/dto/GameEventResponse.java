package io.gaming.platform.gameservice.dto;

import io.gaming.platform.gameservice.model.EventCategory;
import io.gaming.platform.gameservice.model.GameEventType;

import java.time.Instant;

public record GameEventResponse(
    String eventId,
    EventCategory category,
    GameEventType eventType,
    Instant timestamp,
    Long playerId,
    String status,
    String message
) {
    public static GameEventResponse success(String eventId, EventCategory category, GameEventType eventType, 
            Instant timestamp, Long playerId) {
        return new GameEventResponse(eventId, category, eventType, timestamp, playerId, "ACCEPTED", 
            "Event processed successfully");
    }

    public static GameEventResponse error(GameEventType eventType, Long playerId, String errorMessage) {
        return new GameEventResponse(null, EventCategory.GAME, eventType, Instant.now(), playerId, 
            "ERROR", errorMessage);
    }
} 