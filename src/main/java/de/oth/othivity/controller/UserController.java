package de.oth.othivity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.oth.othivity.dto.LoginDto;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.validator.LoginRequestValidator;
import de.oth.othivity.validator.RegisterRequestValidator;
import de.oth.othivity.exception.UserAlreadyExistException;
import de.oth.othivity.service.IUserService;

@AllArgsConstructor
@Controller
public class UserController {

    private final IUserService userService;

    @InitBinder("loginRequest")
    public void initLoginBinder(WebDataBinder binder) {
        binder.addValidators(new LoginRequestValidator());
    }

    @InitBinder("registerRequest")
    public void initRegisterBinder(WebDataBinder binder) {
        binder.addValidators(new RegisterRequestValidator());
    }

    @GetMapping("/login")
    public String index(Model model) {
        model.addAttribute("loginRequest", new LoginDto());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterDto());
        return "register";
    }

    @PostMapping("/process-register")
    public ModelAndView registerUserAccount(
            @ModelAttribute("registerRequest") @Valid RegisterDto registerRequest,
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
            errors.reject("register.error.userAlreadyExists");
            return mav;
        }
    }
}
