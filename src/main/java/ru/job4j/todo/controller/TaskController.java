package ru.job4j.todo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.TaskService;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    @GetMapping("/completed")
    public String getCompleted(Model model) {
        model.addAttribute("tasks", taskService.findCompleted());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String getNew(Model model) {
        model.addAttribute("tasks", taskService.findNew());
        return "tasks/list";
    }

    @GetMapping("/outdated")
    public String getOutdated(Model model) {
        model.addAttribute("tasks", taskService.findOutdated());
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        task.setAuthor(user);
        taskService.save(task);
        return "redirect:/tasks";
    }

    private String findById(Model model, int id, String redirect) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задача с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
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
