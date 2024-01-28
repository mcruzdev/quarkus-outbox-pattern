package dev.matheuscruz;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/outboxs")
public class OutboxResource {

    OutboxRepository outboxRepository;

    @Inject
    public OutboxResource(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @GET
    public List<Outbox> all() {
        return this.outboxRepository.listAll();
    }
}
