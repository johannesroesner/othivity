package de.oth.othivity.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@Hidden
@RestController
@RequestMapping("/api")
public class TestApiController {

    @GetMapping("/test")
    public String test(Principal principal) {
        return "Zugriff erfolgreich für: " + principal.getName();
    }
}