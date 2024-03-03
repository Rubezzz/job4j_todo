package ru.job4j.todo.util;

import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.Collection;
import java.util.TimeZone;

public final class DateMapper {

    private DateMapper() {
    }

    public static Collection<Task> mapWithTimezone(Collection<Task> tasks, HttpServletRequest request) {
        var user = (User) request.getSession().getAttribute("user");
        var zone = "".equals(user.getTimezone()) || user.getTimezone() == null
                ? TimeZone.getDefault().getID() : user.getTimezone();
        for (Task task : tasks) {
            task.setCreated(task.getCreated()
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of(zone))
                    .toLocalDateTime());
        }
        return tasks;
    }
}
