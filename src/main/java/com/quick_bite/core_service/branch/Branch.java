package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "branches")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    @Convert(converter = CountryCodeConverter.class)
    private CountryCode countryCode;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String addressText;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal lat;

    @Column( nullable = false, precision = 10, scale = 6)
    private BigDecimal lng;

    @Column( nullable = false)
    private Short deliveryRadius;

    @Column(insertable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    private Point location;

    @Column( nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private LocalTime opensAt;

    @Column(nullable = false)
    private LocalTime closesAt;

    @Column(nullable = false)
    private boolean acceptOrders;

    @Column(nullable = false)
    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    @Column(nullable = false)
    private Short commission;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public long getRestaurantOwnerId() {
        return this.restaurant.getOwnerId();
    }
}
