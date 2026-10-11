package ahmad.dbcommuteralert.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUserIfNotExists(String telegramChatId , String username){
        Optional<User> existingUser = userRepository.findUserByTelegramChatId(telegramChatId);

        if (existingUser.isPresent()) return existingUser.get();

        User newUser = new User();
        newUser.setTelegramChatId(telegramChatId);
        newUser.setUsername(username);

        return userRepository.save(newUser);
    }
}
