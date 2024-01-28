package dev.matheuscruz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OutboxRetry {

    static final Logger LOGGER = LoggerFactory.getLogger(OutboxRetry.class);

    ObjectMapper objectMapper;
    OutboxRepository outboxRepository;
    SuccessPublisher publisher;

    @Inject
    public OutboxRetry(ObjectMapper objectMapper, OutboxRepository outboxRepository) {
        this.objectMapper = objectMapper;
        this.outboxRepository = outboxRepository;
        this.publisher = new SuccessPublisher();
    }

    @Scheduled(every = "5s")
    void retry() {
        List<Outbox> outboxs = this.outboxRepository.listAll();
        List<String> ids = new ArrayList<>();

        for (Outbox outbox : outboxs) {
            this.publisher.send(outbox.getMessage());
            ids.add(outbox.getId());
        }

        QuarkusTransaction.requiringNew().run(() -> {
            this.outboxRepository.delete("id in (?1)", ids);
        });
    }

    public record SuccessPublisher() {
        void send(String event) {
            String queue = "great-queue";
            LOGGER.info("Message [{}] sent to Queue[{}] with success!", event, queue);
        }
    }
}
