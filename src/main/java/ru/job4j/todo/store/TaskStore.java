package ru.job4j.todo.store;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@AllArgsConstructor
public class TaskStore implements Store {

    private final CrudRepository crudRepository;

    @Override
    public Task save(Task task) {
        crudRepository.run(
                session -> {
                    session.persist(task);
                    return 0;
                }
        );
        return task;
    }

    @Override
    public boolean complete(int id) {
        return crudRepository.run(
                "UPDATE Task SET done = :fDone  WHERE id = :fId",
                Map.of("fDone", true, "fId", id)
        ) != 0;
    }

    @Override
    public boolean deleteById(int id) {
        return crudRepository.run(
                "DELETE Task WHERE id = :fId",
                Map.of("fId", id)
        ) != 0;
    }

    @Override
    public boolean update(Task task) {
        return crudRepository.run(
                "UPDATE Task SET name = :fName, description = :fDescription, done = :fDone, priority_id = :fPriority  WHERE id = :fId",
                Map.of("fName", task.getName(),
                        "fDescription", task.getDescription(),
                        "fDone", task.isDone(),
                        "fPriority", task.getPriority().getId(),
                        "fId", task.getId())
        ) != 0;
    }

    @Override
    public Optional<Task> findById(int id) {
        return crudRepository.optional(
                "FROM Task t JOIN FETCH t.priority WHERE t.id = :fId",
                Task.class,
                Map.of("fId", id)
        );
    }

    @Override
    public Collection<Task> findAll() {
        return crudRepository.query("FROM Task", Task.class);
    }

    @Override
    public Collection<Task> findNew() {
        return crudRepository.query(
                "FROM Task WHERE created > :fDate",
                Task.class,
                Map.of("fDate", LocalDateTime.now().minusDays(1))
        );
    }

    @Override
    public Collection<Task> findCompleted() {
        return crudRepository.query(
                "FROM Task WHERE done = :fDone",
                Task.class,
                Map.of("fDone", true)
        );
    }

    @Override
    public Collection<Task> findOutdated() {
        return crudRepository.query(
                "FROM Task WHERE done <> :fDone AND created < :fDate",
                Task.class,
                Map.of("fDone", true, "fDate", LocalDateTime.now().minusDays(7))
        );
    }
}