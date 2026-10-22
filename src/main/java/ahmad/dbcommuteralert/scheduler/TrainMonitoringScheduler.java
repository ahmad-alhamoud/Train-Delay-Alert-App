package ahmad.dbcommuteralert.scheduler;

import ahmad.dbcommuteralert.subscription.Subscription;
import ahmad.dbcommuteralert.subscription.SubscriptionService;
import ahmad.dbcommuteralert.telegram.TrainAlertBot;
import ahmad.dbcommuteralert.train.TrainService;
import ahmad.dbcommuteralert.train.TrainStatusInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TrainMonitoringScheduler {

    private final SubscriptionService subscriptionService;
    private TrainService trainService;
    private TrainAlertBot trainAlertBot;


    public TrainMonitoringScheduler(SubscriptionService subscriptionService, TrainService trainService, TrainAlertBot trainAlertBot) {
        this.subscriptionService = subscriptionService;
        this.trainService = trainService;
        this.trainAlertBot = trainAlertBot;
    }

    @Scheduled(fixedRate = 60000)
    public void monitorTrains() {
        System.out.println("🔄 [Scheduler] Running periodic train delay check at: " + LocalDateTime.now());
        String targetTrain = "ICE 123";
        TrainStatusInfo status = trainService.checkTrainStatus(targetTrain);

        if (status.isDelayed()) {
            System.out.println("⚠️ Train " + status.getTrainNumber() + " is delayed by " + status.getDelayMinutes() + " minutes!");

            List<Subscription> subscriptions = subscriptionService.getActiveSubscriptionsForTrain(targetTrain);


            if (subscriptions.isEmpty()) {
                return;
            }

            String alertMessage = "🚨 Achtung! Dein abonnierter Zug " + status.getTrainNumber() + " hat aktuell " + status.getDelayMinutes() + " Minuten Verspätung.";

            for (Subscription sub : subscriptions) {
                try {
                    long chatId = Long.parseLong(sub.getUser().getTelegramChatId());
                    trainAlertBot.sendResponse(chatId, alertMessage);
                } catch (Exception e) {
                    System.out.println("Error sending  alert : " + e.getMessage());
                }

            }
        }
    }
}
