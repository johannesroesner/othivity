package de.oth.othivity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.WebDataBinder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.oth.othivity.dto.LoginDto;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.validator.LoginDtoValidator;
import de.oth.othivity.validator.RegisterDtoValidator;
import de.oth.othivity.service.IUserService;

import java.util.Locale;
import java.util.Map;

@AllArgsConstructor
@Controller
public class UserController {

    private final IUserService userService;
    private final LoginDtoValidator loginDtoValidator;
    private final RegisterDtoValidator registerDtoValidator;

    @InitBinder("loginDto")
    public void initLoginBinder(WebDataBinder binder) {
        binder.addValidators(loginDtoValidator);
    }

    @InitBinder("registerDto")
    public void initRegisterBinder(WebDataBinder binder) {
        binder.addValidators(registerDtoValidator);
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/process-register")
    public String registerUserAccount(@ModelAttribute("registerDto") @Valid RegisterDto registerDto, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerNewUserAccount(registerDto, request.getLocale(), false, true);
        return "redirect:/login?registered";
    }
}
