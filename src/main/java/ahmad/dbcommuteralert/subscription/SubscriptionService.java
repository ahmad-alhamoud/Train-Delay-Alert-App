package ahmad.dbcommuteralert.subscription;

import ahmad.dbcommuteralert.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription addSubscription(User user, String trainNumber, String departureStation, String arrivalStation) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTrainNumber(trainNumber);
        subscription.setArrivalStation(arrivalStation);
        subscription.setDepartureStation(departureStation);
        subscription.setIsActive(true);

        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(User user) {
        return subscriptionRepository.findByUser(user);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> getActiveSubscriptionsForTrain(String trainNumber) {
        return subscriptionRepository.findByTrainNumberAndIsActiveTrue(trainNumber);
    }

    public List<Subscription> getActiveUserSubscriptions(User user) {
        return subscriptionRepository.findByUserAndIsActiveTrue(user);
    }

    public boolean unsubscribeTrain(User user, String trainNumber) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserAndTrainNumberAndIsActiveTrue(user, trainNumber);

        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
            return true;
        }

        return false;
    }
}
