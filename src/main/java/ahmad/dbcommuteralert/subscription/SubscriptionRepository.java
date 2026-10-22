package ahmad.dbcommuteralert.subscription;

import ahmad.dbcommuteralert.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser(User user);


    List<Subscription> findByIsActiveTrue();

    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.trainNumber = :trainNumber AND s.isActive = true")
    List<Subscription> findByTrainNumberAndIsActiveTrue(String trainNumber);


    List<Subscription> findByUserAndIsActiveTrue(User user);


    Optional<Subscription> findByUserAndTrainNumberAndIsActiveTrue(User user, String trainNumber);
}
