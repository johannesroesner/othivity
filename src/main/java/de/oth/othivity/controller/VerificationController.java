package de.oth.othivity.controller;

import de.oth.othivity.dto.PhoneVerificationDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.service.SmsService;
import de.oth.othivity.service.VerificationService;
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

    private SessionService sessionService;
    private VerificationService verificationService;
    private SmsService smsService;

    private PhoneVerificationDtoValidator phoneVerificationDtoValidator;

    @InitBinder("phoneVerificationDto")
    protected void initBinderVerification(WebDataBinder binder) {
        binder.addValidators(phoneVerificationDtoValidator);
    }

    @GetMapping("/profile/phone/verify")
    public String startPhoneVerify(Model model, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null || profile.getPhone() == null || profile.getPhone().getNumber() == null || profile.getPhone().getVerified() == true) return "redirect:/dashboard";
        smsService.startVerification(profile.getPhone().getNumber());
        model.addAttribute("phoneVerificationDto", verificationService.buildPhoneVerificationDto(profile));
        return "verify-code";
    }

    @PostMapping("/profile/phone/verify")
    public String phoneVerify(@Valid @ModelAttribute("phoneVerificationDto") PhoneVerificationDto phoneVerificationDto, BindingResult bindingResult, Model model, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return  "redirect:/dashboard";
        if (bindingResult.hasErrors()) return "verify-code";
        verificationService.setVerificationForPhone(profile);
        return "redirect:/settings";
    }
}
