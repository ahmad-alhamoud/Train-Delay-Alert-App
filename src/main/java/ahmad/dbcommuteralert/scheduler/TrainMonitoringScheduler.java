package ahmad.dbcommuteralert.scheduler;

import ahmad.dbcommuteralert.subscription.Subscription;
import ahmad.dbcommuteralert.subscription.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TrainMonitoringScheduler {

    private final SubscriptionService subscriptionService;

    public TrainMonitoringScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(fixedRate = 6000)
    public void checkTrainDelays() {
        System.out.println("\uD83D\uDD04 [Scheduler] Running periodic train delay check at:" + LocalDateTime.now());

        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();

        subscriptions.forEach(
                sub -> System.out.println("Checking train:" + sub.getTrainNumber() +
                                          "from " + sub.getDepartureStation() +
                                          "to " + sub.getArrivalStation())
        );
    }
}
