package dev.matheuscruz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "order")
@Table(name = "orders")
public class Order {

    @Id
    String id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "created_at")
    Instant createdAt;

    public Order() {
        this.id = UUID.randomUUID().toString();
        this.status = OrderStatus.CREATED;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
