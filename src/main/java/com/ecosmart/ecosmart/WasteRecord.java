
package com.ecosmart.ecosmart;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_records")
public class WasteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private BigDecimal quantity;
    private String location;

    @Column(name = "registered_at", insertable = false, updatable = false)
    private LocalDateTime registeredAt;

    public WasteRecord() {
    }

    public WasteRecord(String category, BigDecimal quantity, String location) {
        this.category = category;
        this.quantity = quantity;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}
