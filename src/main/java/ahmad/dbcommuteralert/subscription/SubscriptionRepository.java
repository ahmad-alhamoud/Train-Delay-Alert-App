package ahmad.dbcommuteralert.subscription;

import ahmad.dbcommuteralert.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    List<Subscription> findByUser(User user);


    List<Subscription> findByIsActiveTrue();
}
