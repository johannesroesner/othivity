package de.oth.othivity.controller;

import de.oth.othivity.dto.PhoneVerificationDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ISessionService;
import de.oth.othivity.service.ISmsService;
import de.oth.othivity.service.IVerificationService;
import de.oth.othivity.validator.PhoneVerificationDtoValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
@Controller
public class VerificationController {

    private ISessionService ISessionService;
    private IVerificationService IVerificationService;
    private ISmsService ISmsService;

    private PhoneVerificationDtoValidator phoneVerificationDtoValidator;

    @InitBinder("phoneVerificationDto")
    protected void initBinderVerification(WebDataBinder binder) {
        binder.addValidators(phoneVerificationDtoValidator);
    }

    @GetMapping("/profile/phone/verify")
    public String startPhoneVerify(Model model, HttpSession session) {
        Profile profile = ISessionService.getProfileFromSession(session);
        if (profile == null || profile.getPhone() == null || profile.getPhone().getNumber() == null || profile.getPhone().getVerified() == true) return "redirect:/dashboard";
        ISmsService.startVerification(profile.getPhone().getNumber());
        model.addAttribute("phoneVerificationDto", IVerificationService.buildPhoneVerificationDto(profile));
        return "verify-code";
    }

    @PostMapping("/profile/phone/verify")
    public String phoneVerify(@Valid @ModelAttribute("phoneVerificationDto") PhoneVerificationDto phoneVerificationDto, BindingResult bindingResult, Model model, HttpSession session) {
        Profile profile = ISessionService.getProfileFromSession(session);
        if (profile == null) return  "redirect:/dashboard";
        if (bindingResult.hasErrors()) return "verify-code";
        IVerificationService.setVerificationForPhone(profile);
        return "redirect:/settings";
    }
}
