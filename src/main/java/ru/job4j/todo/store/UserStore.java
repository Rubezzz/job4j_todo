package ru.job4j.todo.store;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserStore implements UserRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<User> save(User user) {
        crudRepository.run(
                session -> {
                    session.persist(user);
                    return 0;
                }
        );
        return Optional.of(user);
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
