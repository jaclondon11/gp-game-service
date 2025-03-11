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
    String errorMessage
) {
    public static GameEventResponse success(
    		String eventId,
    		EventCategory category,
    		GameEventType eventType, 
            Instant timestamp,
            Long playerId) {
        return new GameEventResponse(eventId, category, eventType, timestamp, playerId, null);
    }

    public static GameEventResponse error(
    		GameEventType eventType,
    		Long playerId,
    		String errorMessage) {
		return new GameEventResponse(
			null,
			EventCategory.GAME,
			eventType,
			Instant.now(),
			playerId,
			errorMessage
		);
    }
    
    public boolean isSuccess() {
        return errorMessage == null;
    }
} 