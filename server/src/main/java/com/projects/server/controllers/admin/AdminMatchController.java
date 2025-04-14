package com.projects.server.controllers.admin;

import com.projects.server.dto.request.MatchCreateRequest;
import com.projects.server.dto.response.MatchResponse;
import com.projects.server.services.interfaces.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/matches")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@Valid @RequestBody MatchCreateRequest request) {
        return ResponseEntity.ok(matchService.createMatch(request));
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllUpcomingMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/phase/{phase}")
    public ResponseEntity<List<MatchResponse>> getMatchesByPhase(@PathVariable String phase) {
        return ResponseEntity.ok(matchService.getMatchesByPhase(phase));
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<List<MatchResponse>> getMatchesByGroup(@PathVariable String group) {
        return ResponseEntity.ok(matchService.getMatchesByGroup(group));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<Void> updateMatchScore(
            @PathVariable Long id,
            @RequestParam Integer matchScore) {
        matchService.updateMatchScore(id, matchScore);
        return ResponseEntity.ok().build();
    }
}