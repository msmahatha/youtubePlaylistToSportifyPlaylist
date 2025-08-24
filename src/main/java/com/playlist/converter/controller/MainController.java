package com.playlist.converter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle main page routing
 */
@Controller
public class MainController {
    
    /**
     * Serve the main index page
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}
