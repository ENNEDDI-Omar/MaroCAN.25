package com.projects.server.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializationConfig {

    private final GroupStageMatchInitializer groupStageMatchInitializer;

    @EventListener(ContextRefreshedEvent.class)
    public void initializeDatabase() {
        log.info("Début de l'initialisation de la base de données pour la CAN 2025");

        // Initialiser les matchs de la phase de groupes
        groupStageMatchInitializer.initializeGroupStageMatches();

        log.info("Initialisation de la base de données terminée");
    }
}
