package ahmad.dbcommuteralert.telegram;

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

@Component
public class TrainAlertBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;

    private final UserRepository userRepository;

    public TrainAlertBot(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken, UserRepository userRepository) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userRepository = userRepository;
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

            String stringChatId = String.valueOf(chatId);
            User user = userRepository.findUserByTelegramChatId(stringChatId).orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(username != null ? username : "unknown");
                newUser.setTelegramChatId(stringChatId);
                return userRepository.save(newUser);
            });

            System.out.println("👤 User processed in DB: " + user.getTelegramChatId());

            if (messageText.equalsIgnoreCase("/start")) {
                try {
                    sendResponse(chatId, "Willkommen beim DB Commuter Alert System! Deine Chat-ID wurde erfolgreich gespeichert. Du wirst hier über Zugverspätungen benachrichtigt.");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    sendResponse(chatId, "Nachricht erhalten: " + messageText);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
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
