package ru.job4j.todo.store;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserStore implements UserRepository {

    @NonNull
    private final CrudRepository crudRepository;
    private final Logger logger = LoggerFactory.getLogger(TaskStore.class);

    @Override
    public Optional<User> save(User user) {
        try {
            crudRepository.run(
                    session -> {
                        session.persist(user);
                        return 0;
                    }
            );
            return Optional.of(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                "FROM User WHERE login = :fLogin AND  password = :fPassword",
                User.class,
                Map.of("fLogin", login, "fPassword", password)
        );
    }
}
