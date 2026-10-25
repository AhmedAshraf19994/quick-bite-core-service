package com.quick_bite.core_service.restaurant;

import com.quick_bite.core_service.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String logoUrl;

    @Column(nullable = false)
    private String primaryCountry;

    @Column(nullable = false)
    @Convert(converter = RestaurantStatusConverter.class)
    private RestaurantStatus status;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant statusUpdatedAt;

    public long getOwnerId() {
        return this.owner.getId();
    }
}
