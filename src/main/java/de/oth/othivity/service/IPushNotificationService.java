package de.oth.othivity.service;

import de.oth.othivity.model.main.Profile;

public interface IPushNotificationService {
    
    void sendPushToProfile(Profile profile, String title, String message);
} 
    

