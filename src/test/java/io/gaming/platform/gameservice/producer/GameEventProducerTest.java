package io.gaming.platform.gameservice.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class GameEventProducerTest {
    private static final String TOPIC = "game-events";
    private static final Long PLAYER_ID = 123L;
    private static final Long OTHER_PLAYER_ID = 456L;
    private static final String ERROR_MESSAGE = "Error serializing event";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private GameEventProducer gameEventProducer;

    @BeforeEach
    void setUp() {
        gameEventProducer = new GameEventProducer(kafkaTemplate, objectMapper, TOPIC);
    }

    @Test
    void sendLevelUpEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        int newLevel = 5;
        String eventJson = """
            {"eventType":"LEVEL_UP","playerId":123,"data":{"newLevel":5}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendLevelUpEvent(PLAYER_ID, newLevel);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @Test
    void sendItemAcquiredEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        String itemName = "Legendary Sword";
        String rarity = "LEGENDARY";
        String eventJson = """
            {"eventType":"ITEM_ACQUIRED","playerId":123,"data":{"itemName":"Legendary Sword","rarity":"LEGENDARY"}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendItemAcquiredEvent(PLAYER_ID, itemName, rarity);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @Test
    void sendChallengeCompletedEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        String challengeName = "Dragon Slayer";
        String eventJson = """
            {"eventType":"CHALLENGE_COMPLETED","playerId":123,"data":{"challengeName":"Dragon Slayer"}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendChallengeCompletedEvent(PLAYER_ID, challengeName);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @Test
    void sendPvpAttackEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        int damageDealt = 50;
        String eventJson = """
            {"eventType":"PVP_ATTACK","playerId":123,"data":{"attackerId":456,"damageDealt":50}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendPvpAttackEvent(PLAYER_ID, OTHER_PLAYER_ID, damageDealt);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @Test
    void sendPvpDefeatEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        String battleLocation = "Ancient Arena";
        String eventJson = """
            {"eventType":"PVP_DEFEAT","playerId":123,"data":{"victorPlayerId":456,"battleLocation":"Ancient Arena"}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendPvpDefeatEvent(PLAYER_ID, OTHER_PLAYER_ID, battleLocation);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @SuppressWarnings("serial")
    @Test
    void sendGameEvent_ShouldHandleSerializationError() throws JsonProcessingException {
        // Arrange
        int newLevel = 5;
        
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException(ERROR_MESSAGE) {});

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendLevelUpEvent(PLAYER_ID, newLevel);

        // Assert
        assertThat(result).isCompletedExceptionally();
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void sendGameEvent_ShouldHandleKafkaError() throws JsonProcessingException {
        // Arrange
        int newLevel = 5;
        String eventJson = """
            {"eventType":"LEVEL_UP","playerId":123,"data":{"newLevel":5}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        // Act
        CompletableFuture<SendResult<String, String>> result = gameEventProducer.sendLevelUpEvent(PLAYER_ID, newLevel);

        // Assert
        assertThat(result).isCompletedExceptionally();
    }

    private SendResult<String, String> createSendResult() {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, PLAYER_ID.toString(), "test-value");
        TopicPartition topicPartition = new TopicPartition(TOPIC, 0);
        RecordMetadata metadata = new RecordMetadata(topicPartition, 0, 0, 0, 0, 0);
        return new SendResult<>(record, metadata);
    }
}