package io.gaming.platform.gameservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaming.platform.gameservice.model.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class GameEventProducer {
    private static final Logger log = LoggerFactory.getLogger(GameEventProducer.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public GameEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    /**
     * Sends a level up event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendLevelUpEvent(Long playerId, int newLevel) {
        return sendGameEvent(GameEvent.levelUp(playerId, newLevel));
    }

    /**
     * Sends an item acquisition event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendItemAcquiredEvent(
            Long playerId, String itemName, String rarity) {
        return sendGameEvent(GameEvent.itemAcquired(playerId, itemName, rarity));
    }

    /**
     * Sends a challenge completion event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendChallengeCompletedEvent(
            Long playerId, String challengeName) {
        return sendGameEvent(GameEvent.challengeCompleted(playerId, challengeName));
    }

    /**
     * Sends a PvP attack event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendPvpAttackEvent(
    		Long defenderId, Long attackerId , int damageDealt) {
        return sendGameEvent(GameEvent.pvpAttack(defenderId, attackerId, damageDealt));
    }

    /**
     * Sends a PvP defeat event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendPvpDefeatEvent(
            Long defeatedPlayerId, Long victorPlayerId, String battleLocation) {
        return sendGameEvent(GameEvent.pvpDefeat(defeatedPlayerId, victorPlayerId, battleLocation));
    }

    /**
     * Generic method to send any game event to Kafka.
     */
    private CompletableFuture<SendResult<String, String>> sendGameEvent(GameEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            String key = event.playerId().toString();

            return kafkaTemplate.send(topicName, key, message)
                .thenApply(result -> {
                    log.info("Successfully sent game event {}: {} to topic {} partition {} offset {}",
                        event.eventType(), message, result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    return result;
                })
                .exceptionally(ex -> {
                    log.error("Failed to send game event {}: {}", event.eventType(), ex.getMessage(), ex);
                    throw new RuntimeException("Failed to send game event", ex);
                });
        } catch (Exception e) {
            log.error("Error preparing game event {}: {}", event.eventType(), e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
} 