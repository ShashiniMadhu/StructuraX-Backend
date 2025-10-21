package com.structurax.root.structurax.root.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Configuration  // <-- ADD THIS
public class ProfileConfig {
    @Autowired
    private Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void displayActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";

        System.out.println("\n========================================");
        System.out.println("ACTIVE PROFILE: " + activeProfile.toUpperCase());
        System.out.println("========================================\n");
    }
}