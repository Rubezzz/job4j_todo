package ru.job4j.todo.store;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskStore implements Store {

    @NonNull
    private final SessionFactory sf;
    private final Logger logger = LoggerFactory.getLogger(TaskStore.class);

    @Override
    public Task save(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int userId = (Integer) session.save(task);
            task.setId(userId);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return task;
    }

    @Override
    public boolean complete(int id) {
        Session session = sf.openSession();
        int rsl = 0;
        try {
            session.beginTransaction();
            rsl = session.createQuery("UPDATE Task SET done = :fDone  WHERE id = :fId")
                    .setParameter("fDone", true)
                    .setParameter("fId", id)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return rsl != 0;
    }

    @Override
    public boolean deleteById(int id) {
        Session session = sf.openSession();
        int rsl = 0;
        try {
            session.beginTransaction();
            rsl = session.createQuery("DELETE Task WHERE id = :fId")
                    .setParameter("fId", id)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return rsl != 0;
    }

    @Override
    public boolean update(Task task) {
        Session session = sf.openSession();
        int rsl = 0;
        try {
            session.beginTransaction();
            rsl = session.createQuery("UPDATE Task SET name = :fName, description = :fDescription, done = :fDone  WHERE id = :fId")
                    .setParameter("fName", task.getName())
                    .setParameter("fDescription", task.getDescription())
                    .setParameter("fDone", task.isDone())
                    .setParameter("fId", task.getId())
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return rsl != 0;
    }

    @Override
    public Optional<Task> findById(int id) {
        Session session = sf.openSession();
        Optional<Task> rsl = Optional.empty();
        try {
            session.beginTransaction();
            Query<Task> query = session.createQuery("FROM Task WHERE id = :fId", Task.class)
                    .setParameter("fId", id);
            rsl = query.uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return rsl;
    }

    @Override
    public Collection<Task> findAll() {
        Session session = sf.openSession();
        List<Task> rsl = new ArrayList<>();
        try {
            session.beginTransaction();
            Query<Task> query = session.createQuery("FROM Task", Task.class);
            rsl = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return rsl;
    }

    @Override
    public Collection<Task> findNew() {
        Session session = sf.openSession();
        List<Task> rsl = new ArrayList<>();
        try {
            session.beginTransaction();
            Query<Task> query = session.createQuery("FROM Task WHERE created > :fDate", Task.class)
                    .setParameter("fDate", LocalDateTime.now().minusDays(1));
            rsl = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return rsl;
    }

    @Override
    public Collection<Task> findCompleted() {
        Session session = sf.openSession();
        List<Task> rsl = new ArrayList<>();
        try {
            session.beginTransaction();
            Query<Task> query = session.createQuery("FROM Task WHERE done = :fDone", Task.class)
                    .setParameter("fDone", true);
            rsl = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return rsl;
    }

    @Override
    public Collection<Task> findOutdated() {
        Session session = sf.openSession();
        List<Task> rsl = new ArrayList<>();
        try {
            session.beginTransaction();
            Query<Task> query = session.createQuery("FROM Task WHERE done <> :fDone AND created < :fDate", Task.class)
                    .setParameter("fDone", true)
                    .setParameter("fDate", LocalDateTime.now().minusDays(7));
            rsl = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return rsl;
    }
}