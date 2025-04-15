package com.projects.server.controllers.user;

import com.projects.server.dto.response.MatchResponse;
import com.projects.server.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * Récupère tous les matchs à venir
     * @return Liste des matchs à venir
     */
    @GetMapping
    public ResponseEntity<List<MatchResponse>> getUpcomingMatches() {
        return ResponseEntity.ok(matchService.getAllUpcomingMatches());
    }

    /**
     * Récupère un match par son ID
     * @param id ID du match
     * @return Détails du match
     */
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    /**
     * Récupère les matchs par phase de compétition
     * @param phase Phase de compétition (ex : GROUP_STAGE, ROUND_OF_16, etc.)
     * @return Liste des matchs de la phase spécifiée
     */
    @GetMapping("/phase/{phase}")
    public ResponseEntity<List<MatchResponse>> getMatchesByPhase(@PathVariable String phase) {
        return ResponseEntity.ok(matchService.getMatchesByPhase(phase));
    }

    /**
     * Récupère les matchs par groupe
     * @param group Groupe (ex: GROUP_A, GROUP_B, etc.)
     * @return Liste des matchs du groupe spécifié
     */
    @GetMapping("/group/{group}")
    public ResponseEntity<List<MatchResponse>> getMatchesByGroup(@PathVariable String group) {
        return ResponseEntity.ok(matchService.getMatchesByGroup(group));
    }

    @GetMapping("/date")
    public ResponseEntity<List<MatchResponse>> getMatchesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(matchService.getMatchesByDate(date));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByTeam(@PathVariable String teamId) {
        return ResponseEntity.ok(matchService.getMatchesByTeam(teamId));
    }

    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByStadium(@PathVariable String stadiumId) {
        return ResponseEntity.ok(matchService.getMatchesByStadium(stadiumId));
    }
}