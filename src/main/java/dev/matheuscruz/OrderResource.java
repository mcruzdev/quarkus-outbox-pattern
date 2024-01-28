package dev.matheuscruz;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.vertx.core.impl.NoStackTraceException;

@Path("/orders")
public class OrderResource {

    static final Logger LOGGER = LoggerFactory.getLogger(OrderResource.class);
    OrderRepository orderRepository;
    OutboxRepository outboxRepository;
    ObjectMapper objectMapper;
    MaybeSuccessPublisher publisher;

    @Inject
    public OrderResource(OrderRepository repository, ObjectMapper objectMapper,
            OutboxRepository outboxRepository) {
        this.orderRepository = repository;
        this.objectMapper = objectMapper;
        this.outboxRepository = outboxRepository;
        this.publisher = new MaybeSuccessPublisher();
    }

    @GET
    public List<Order> all() {
        return this.orderRepository.listAll();
    }

    @POST
    public Response create() throws JsonProcessingException {
        Order order = new Order();

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getStatus().name(),
                order.getCreatedAt());

        Outbox outbox = new Outbox(eventToMessage(event));

        QuarkusTransaction.requiringNew().run(() -> {
            this.orderRepository.persist(order);
            this.outboxRepository.persist(outbox);
        });

        this.publisher.send(outbox.getMessage());

        QuarkusTransaction.requiringNew().run(() -> {
            this.outboxRepository.deleteById(outbox.getId());
        });

        return Response.created(URI.create("/orders/" + order.getId())).build();
    }

    public String eventToMessage(OrderCreatedEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }

    public record OrderCreatedEvent(String orderId, String status, Instant createdAt) {

    }

    public record MaybeSuccessPublisher() {
        void send(String message) {
            String queue = "great-queue";
            if (new Random().nextBoolean()) {
                LOGGER.error("Error while sending message[{}] to Queue[{}]", message, queue);
                throw new NoStackTraceException(new RuntimeException("Publisher error"));
            }
            LOGGER.info("Message [{}] sent to Queue[{}] with success!", message, queue);
        }
    }
}
