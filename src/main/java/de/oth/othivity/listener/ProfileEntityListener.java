package de.oth.othivity.listener;

import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.SmsService;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProfileEntityListener {
    private static ActivityService activityService;
    private static ClubService clubService;
    private static SmsService smsService;
    private static MessageSource messageSource;


    @Autowired
    public void init(ActivityService activityService, ClubService clubService, SmsService smsService, MessageSource messageSource) {
        ProfileEntityListener.activityService = activityService;
        ProfileEntityListener.clubService = clubService;
        ProfileEntityListener.smsService = smsService;
        ProfileEntityListener.messageSource = messageSource;
    }

    @PreRemove
    public void preRemoveProfile(Profile profile) {
        if(profile.getPhone()!=null && profile.getPhone().getNumber()!=null) {
            String message = messageSource.getMessage("profile.deleteNotification", null, LocaleContextHolder.getLocale());
            smsService.sendSms(profile.getPhone().getNumber(), message);
        }

        for(Club club : profile.getClubs()) {
            club.getMembers().remove(profile);
            if(club.getAdmins().contains(profile)){
                if(club.getAdmins().size() == 1) clubService.deleteClub(club,profile);
                else {
                    club.getAdmins().remove(profile);
                }
            }
        }

        activityService.removeProfileFromActivities(profile);
    }
}