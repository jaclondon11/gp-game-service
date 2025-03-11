package io.gaming.platform.gameservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaming.platform.gameservice.dto.*;
import io.gaming.platform.gameservice.model.GameEvent;
import io.gaming.platform.gameservice.model.GameEventType;
import io.gaming.platform.gameservice.producer.GameEventProducer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/v1/game-events")
public class GameEventController {
    private static final Logger log = LoggerFactory.getLogger(GameEventController.class);
    
    private final GameEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    public GameEventController(GameEventProducer eventProducer, ObjectMapper objectMapper) {
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/level-up")
    public ResponseEntity<GameEventResponse> levelUp(@Valid @RequestBody LevelUpRequest request) {
        log.debug("Processing level-up event for player: {}", request.playerId());
        return handleGameEvent(
            () -> eventProducer.sendLevelUpEvent(
            		request.playerId(),
            		request.newLevel()),
            GameEventType.LEVEL_UP,
            request.playerId()
        );
    }

    @PostMapping("/items/acquired")
    public ResponseEntity<GameEventResponse> acquireItem(@Valid @RequestBody ItemAcquiredRequest request) {
        log.debug("Processing item acquisition event for player: {}", request.playerId());
        return handleGameEvent(
            () -> eventProducer.sendItemAcquiredEvent(
                request.playerId(),
                request.itemName(),
                request.rarity()),
            GameEventType.ITEM_ACQUIRED,
            request.playerId()
        );
    }

    @PostMapping("/challenges/completed")
    public ResponseEntity<GameEventResponse> completeChallenge(@Valid @RequestBody ChallengeCompletedRequest request) {
        log.debug("Processing challenge completion event for player: {}", request.playerId());
        return handleGameEvent(
            () -> eventProducer.sendChallengeCompletedEvent(
                request.playerId(),
                request.challengeName()),
            GameEventType.CHALLENGE_COMPLETED,
            request.playerId()
        );
    }

    @PostMapping("/pvp/attack")
    public ResponseEntity<GameEventResponse> pvpAttack(@Valid @RequestBody PvpAttackRequest request) {
        log.debug("Processing PvP attack event from player: {} to player: {}", 
            request.attackerId(), request.defenderId());
        return handleGameEvent(
            () -> eventProducer.sendPvpAttackEvent(
        		request.defenderId(),
                request.attackerId(),
                request.damageDealt()),
            GameEventType.PVP_ATTACK,
            request.attackerId()
        );
    }

    @PostMapping("/pvp/defeat")
    public ResponseEntity<GameEventResponse> pvpDefeat(@Valid @RequestBody PvpDefeatRequest request) {
        log.debug("Processing PvP defeat event for player: {} defeated by: {}", 
            request.defeatedPlayerId(), request.victorPlayerId());
        return handleGameEvent(
            () -> eventProducer.sendPvpDefeatEvent(
                request.defeatedPlayerId(),
                request.victorPlayerId(),
                request.battleLocation()),
            GameEventType.PVP_DEFEAT,
            request.defeatedPlayerId()
        );
    }

    private ResponseEntity<GameEventResponse> handleGameEvent(
            Supplier<CompletableFuture<SendResult<String, String>>> eventSupplier,
            GameEventType eventType,
            Long playerId) {
        try {
            SendResult<String, String> result = eventSupplier.get().join();
            log.debug("Successfully processed {} event for player: {}", eventType, playerId);
            return buildSuccessResponse(result);
        } catch (Exception ex) {
            log.error("Failed to process {} event for player: {}", eventType, playerId, ex);
            return buildErrorResponse(eventType, playerId, ex.getMessage());
        }
    }

    private ResponseEntity<GameEventResponse> buildSuccessResponse(SendResult<String, String> result) {
        try {
            GameEvent event = objectMapper.readValue(result.getProducerRecord().value(), GameEvent.class);
            return ResponseEntity.accepted().body(
                GameEventResponse.success(
                    event.eventId(),
                    event.category(),
                    event.eventType(),
                    event.timestamp(),
                    event.playerId()
                )
            );
        } catch (Exception e) {
            log.error("Failed to deserialize event response", e);
            throw new RuntimeException("Failed to process response: " + e.getMessage(), e);
        }
    }

    private ResponseEntity<GameEventResponse> buildErrorResponse(
            GameEventType eventType, Long playerId, String errorMessage) {
        return ResponseEntity.internalServerError()
            .body(GameEventResponse.error(eventType, playerId, errorMessage));
    }
} 