package ahmad.dbcommuteralert.subscription;

import ahmad.dbcommuteralert.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription addSubscription(User user , String trainNumber, String departureStation, String arrivalStation){
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

    public List<Subscription> getAllSubscriptions(){
        return subscriptionRepository.findAll();
    }
}
