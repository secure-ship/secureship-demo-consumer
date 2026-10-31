package com.secureship.notes_api;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotesController {

    // VULN 1: hardcoded secret (fake key, but real-looking format)
    private static final String API_KEY = "AKIAZ5Q7X2M4K5R3T6WB";

    private final JdbcTemplate jdbc;

    public NotesController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("notes",
            jdbc.queryForList("SELECT id, owner, title, content FROM notes ORDER BY id DESC"));
        return "index";
    }

    @PostMapping("/notes")
    public String add(@RequestParam String owner, @RequestParam String title,
                      @RequestParam String content) {
        jdbc.update("INSERT INTO notes(owner, title, content) VALUES (?, ?, ?)", owner, title, content);
        return "redirect:/";
    }

    @GetMapping("/api/notes/search")
    @ResponseBody
    public List<Map<String, Object>> search(@RequestParam String q) {
        // VULN 2: SQL injection (user input concatenated into the query)
        String sql = "SELECT id, owner, title, content FROM notes WHERE title LIKE '%" + q + "%'";
        return jdbc.queryForList(sql);
    }
}