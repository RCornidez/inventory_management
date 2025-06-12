package com.cornidezstudios.inventory.models;

import com.cornidezstudios.inventory.enums.Status;
import jakarta.persistence.*;
import java.util.UUID;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "item")
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uniqueidentifier DEFAULT NEWSEQUENTIALID()")
    private UUID uuid;

    private String manufacturer;
    private String model;

    @Column(nullable = false) BigDecimal length;
    @Column(nullable = false) BigDecimal width;
    @Column(nullable = false) BigDecimal height;
    @Column(nullable = false) BigDecimal volume;
    @Column(nullable = false) BigDecimal weight;

    @Column(nullable = false) String shopifyTitle  = "";
    @Column(nullable = false) String shopifyDescription = "";

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @Column(nullable = false) BigDecimal price  = BigDecimal.ONE;
    @Column(nullable = false) Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Lob
    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    void initDefaults() {
        if (shopifyTitle == null)       shopifyTitle = "";
        if (shopifyDescription == null) shopifyDescription = "";
        if (price == null)              price = BigDecimal.ONE;
        if (quantity == null)           quantity = 1;
    }
}

