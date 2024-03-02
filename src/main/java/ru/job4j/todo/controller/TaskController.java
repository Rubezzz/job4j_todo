package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.CategoryService;
import ru.job4j.todo.service.PriorityService;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

@Controller
@AllArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    private Collection<Task> refactorDate(Collection<Task> tasks, HttpServletRequest request) {
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

    @GetMapping
    public String getAll(Model model, HttpServletRequest request) {
        model.addAttribute("tasks", refactorDate(taskService.findAll(), request));
        return "tasks/list";
    }

    @GetMapping("/completed")
    public String getCompleted(Model model, HttpServletRequest request) {
        model.addAttribute("tasks", refactorDate(taskService.findCompleted(), request));
        return "tasks/list";
    }

    @GetMapping("/new")
    public String getNew(Model model, HttpServletRequest request) {
        model.addAttribute("tasks", refactorDate(taskService.findNew(), request));
        return "tasks/list";
    }

    @GetMapping("/outdated")
    public String getOutdated(Model model, HttpServletRequest request) {
        model.addAttribute("tasks", refactorDate(taskService.findOutdated(), request));
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        var priorities = priorityService.findAll();
        var categories = categoryService.findAll();
        model.addAttribute("priorities", priorities);
        model.addAttribute("categories", categories);
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task,
                         @RequestParam(name = "category") int[] categoryId,
                         Model model,
                         HttpSession session) {
        var user = (User) session.getAttribute("user");
        task.setAuthor(user);
        var categories = new ArrayList<Category>();
        for (int id : categoryId) {
            categories.add(new Category(id));
        }
        task.setCategories(categories);
        taskService.save(task);
        return "redirect:/tasks";
    }

    private String findById(Model model, int id, String redirect) {
        var taskOptional = taskService.findById(id);
        var priorities = priorityService.findAll();
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        model.addAttribute("priorities", priorities);
        return redirect;
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        return findById(model, id, "tasks/one");
    }

    @GetMapping("/update/{id}")
    public String getUpdate(Model model, @PathVariable int id) {
        return findById(model, id, "tasks/update");
    }

    @GetMapping("/complete/{id}")
    public String complete(Model model, @PathVariable int id) {
        boolean isComplete = taskService.complete(id);
        if (!isComplete) {
            model.addAttribute("message", "Не удалось выполнить задачу");
            return "errors/404";
        }
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task, Model model) {
        boolean isUpdated = taskService.update(task);
        if (!isUpdated) {
            model.addAttribute("message", "Не удалось изменить задачу");
            return "errors/404";
        }
        return "redirect:/tasks/" + task.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = taskService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Не удалось удалить задачу");
            return "errors/404";
        }
        return "redirect:/tasks";
    }
}
