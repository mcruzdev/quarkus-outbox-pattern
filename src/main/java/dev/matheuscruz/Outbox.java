package dev.matheuscruz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity(name = "outbox")
@Table(name = "outboxs")
public class Outbox {

    @Id
    String id;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    protected Outbox() {
    }

    public Outbox(String message) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
