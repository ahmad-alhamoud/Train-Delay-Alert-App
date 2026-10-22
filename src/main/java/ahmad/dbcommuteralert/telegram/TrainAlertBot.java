package ahmad.dbcommuteralert.telegram;

import ahmad.dbcommuteralert.subscription.Subscription;
import ahmad.dbcommuteralert.subscription.SubscriptionService;
import ahmad.dbcommuteralert.user.User;
import ahmad.dbcommuteralert.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TrainAlertBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;

    private final UserRepository userRepository;
    private SubscriptionService subscriptionService;

    public TrainAlertBot(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken, UserRepository userRepository, SubscriptionService subscriptionService) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("🤖 [Telegram] Bot registered successfully and is listening!");
        } catch (TelegramApiException e) {
            System.err.println("❌ [Telegram] Failed to register: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();


            if (messageText.equals("/start")) {
                String chatIdStr = String.valueOf(chatId);
                userRepository.findUserByTelegramChatId(chatIdStr).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramChatId(chatIdStr);
                    newUser.setUsername(username);
                    newUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

                String welcomeMsg = "Willkommen beim DB Commuter Alert System! Deine Chat-ID wurde erfolgreich gespeichert. Sende mir einfach deine Zugnummer (z.B. ICE 123), um ihn zu abonnieren!";
                try {
                    sendResponse(chatId, welcomeMsg);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (messageText.equals("/mysubs")) {
                String chatIdStr = String.valueOf(chatId);
                Optional<User> optionalUser = userRepository.findUserByTelegramChatId(chatIdStr);
                try {
                    if (optionalUser.isPresent()) {
                        List<Subscription> subs = subscriptionService.getActiveUserSubscriptions(optionalUser.get());
                        if (subs.isEmpty()) {
                            sendResponse(chatId, "Du hast aktuell keine aktiven Zug-Abonnements.");
                        } else {
                            StringBuilder sb = new StringBuilder("🚆 Deine aktiven Abonnements:\n");
                            for (Subscription sub : subs) {
                                sb.append("- ").append(sub.getTrainNumber()).append("\n");
                            }
                            sendResponse(chatId, sb.toString());
                        }
                    } else {
                        sendResponse(chatId, "Bitte starte zuerst den Bot mit /start");
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (messageText.startsWith("/unsubscribe")) {
                String chatIdStr = String.valueOf(chatId);
                Optional<User> optionalUser = userRepository.findUserByTelegramChatId(chatIdStr);
                try {
                    if (optionalUser.isPresent()) {
                        // استخراج كل ما بعد أمر /unsubscribe ليأخذ اسم القطار كاملاً (مثل ICE 123)
                        String trainNumber = messageText.replace("/unsubscribe", "").trim().toUpperCase();

                        if (trainNumber.isEmpty()) {
                            sendResponse(chatId, "Bitte gib die Zugnummer an. Beispiel: /unsubscribe ICE 123");
                            return;
                        }

                        boolean success = subscriptionService.unsubscribeTrain(optionalUser.get(), trainNumber);
                        if (success) {
                            sendResponse(chatId, "❌ Abonnement für Zug " + trainNumber + " wurde erfolgreich gekündigt.");
                        } else {
                            sendResponse(chatId, "⚠️ Du hast kein aktives Abonnement für den Zug " + trainNumber);
                        }
                    } else {
                        sendResponse(chatId, "Bitte starte zuerst den Bot mit /start");
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String chatIdStr = String.valueOf(chatId);
                Optional<User> optionalUser = userRepository.findUserByTelegramChatId(chatIdStr);

                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    String trainNumber = messageText.trim().toUpperCase();

                    // استخدام الـ Service التي أنشأناها مسبقاً لحفظ الاشتراك
                    subscriptionService.addSubscription(user, trainNumber, "Berlin Hbf", "Halle(Saale)Hbf");

                    String confirmMsg = "✅ Zug " + trainNumber + " wurde erfolgreich abonniert! Du erhältst eine Benachrichtigung, sobald es eine Verspätung gibt.";
                    try {
                        sendResponse(chatId, confirmMsg);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        sendResponse(chatId, "Bitte starte zuerst den Bot mit /start");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendResponse(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ [Telegram] Error sending message: " + e.getMessage());
        }
    }

}
