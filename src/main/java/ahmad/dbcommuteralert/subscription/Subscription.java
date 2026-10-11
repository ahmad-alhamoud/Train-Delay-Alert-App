package ahmad.dbcommuteralert.subscription;

import ahmad.dbcommuteralert.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @Column(name = "train_number", nullable = false)
    private String trainNumber;

    @Column(name = "departure_station", nullable = false)
    private String departureStation;

    @Column(name = "arrival_station", nullable = false)
    private String arrivalStation;

    @Column(name = "is_active")
    private Boolean isActive = true;

}
