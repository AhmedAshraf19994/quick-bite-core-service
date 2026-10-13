package com.quick_bite.core_service.customeraddress;

import com.quick_bite.core_service.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name= "customer_addresses"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String building;

    @Column(name = "apartment_number", nullable = false)
    private String apartmentNumber;

    @Column(name = "type", length = 30)
    @Convert(converter = AddressTypeConverter.class)
    private AddressType type;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


}
