package io.gaming.platform.gameservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record GameEvent(
    EventCategory category,
    GameEventType eventType,
    String eventId,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant timestamp,
    Long playerId,
    Map<String, Object> eventData
) {
    public static GameEvent levelUp(Long playerId, int newLevel) {
        return new GameEvent(
            EventCategory.GAME,
            GameEventType.LEVEL_UP,
            UUID.randomUUID().toString(),
            Instant.now(),
            playerId,
            Map.of("newLevel", newLevel)
        );
    }

    public static GameEvent itemAcquired(Long playerId, String itemName, String rarity) {
        return new GameEvent(
            EventCategory.GAME,
            GameEventType.ITEM_ACQUIRED,
            UUID.randomUUID().toString(),
            Instant.now(),
            playerId,
            Map.of(
                "itemName", itemName,
                "rarity", rarity
            )
        );
    }

    public static GameEvent challengeCompleted(Long playerId, String challengeName) {
        return new GameEvent(
            EventCategory.GAME,
            GameEventType.CHALLENGE_COMPLETED,
            UUID.randomUUID().toString(),
            Instant.now(),
            playerId,
            Map.of(
                "challengeName", challengeName
            )
        );
    }

    public static GameEvent pvpAttack(Long attackerId, Long defenderId, int damageDealt) {
        return new GameEvent(
            EventCategory.GAME,
            GameEventType.PVP_ATTACK,
            UUID.randomUUID().toString(),
            Instant.now(),
            defenderId,
            Map.of(
                "attackerId", attackerId,
                "damageDealt", damageDealt
            )
        );
    }

    public static GameEvent pvpDefeat(Long defeatedPlayerId, Long victorPlayerId, String battleLocation) {
        return new GameEvent(
            EventCategory.GAME,
            GameEventType.PVP_DEFEAT,
            UUID.randomUUID().toString(),
            Instant.now(),
            defeatedPlayerId,
            Map.of(
                "victorPlayerId", victorPlayerId,
                "battleLocation", battleLocation,
                "defeatTime", Instant.now().toString()
            )
        );
    }
} 