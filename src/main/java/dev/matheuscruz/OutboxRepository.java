package dev.matheuscruz;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OutboxRepository implements PanacheRepositoryBase<Outbox, String> {

}
