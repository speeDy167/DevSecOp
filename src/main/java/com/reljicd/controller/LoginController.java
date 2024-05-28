package com.reljicd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        return "/login";
    }

    @GetMapping("/user")
    public String getUser(@RequestParam("username") String username, Map<String, Object> model) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        Map<String, Object> user = jdbcTemplate.queryForMap(query);
        model.put("user", user);
        return "user";
    }

}
