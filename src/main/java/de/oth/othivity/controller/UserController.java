package de.oth.othivity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.oth.othivity.dto.LoginRequest;
import de.oth.othivity.dto.RegisterRequest;
import de.oth.othivity.exception.UserAlreadyExistException;
import de.oth.othivity.service.IUserService;

@AllArgsConstructor
@Controller
public class UserController {

    private final IUserService userService;

    @GetMapping("/login")
    public String index(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/process-register")
    public ModelAndView registerUserAccount(
            @ModelAttribute("registerRequest") @Valid RegisterRequest registerRequest,
            BindingResult errors,
            HttpServletRequest request) {
        
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("registerRequest", registerRequest);  // Add this line to pass the object back
        
        if (errors.hasErrors()) {
            return mav;
        }
        
        try {
            userService.registerNewUserAccount(registerRequest);
            return new ModelAndView("redirect:/login?registered");
        } catch (UserAlreadyExistException uaeEx) {
            mav.addObject("message", "An account for that username/email already exists.");
            return mav;
        }
    }
}
