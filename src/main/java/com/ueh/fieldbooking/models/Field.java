package com.ueh.fieldbooking.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @NotNull
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FieldType type;

    @NotNull
    @Min(0)
    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum FieldType {
        FOOTBALL, BADMINTON, TENNIS, BASKETBALL
    }
}
