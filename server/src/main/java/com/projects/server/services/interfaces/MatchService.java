package com.projects.server.services.interfaces;

import com.projects.server.dto.request.MatchCreateRequest;
import com.projects.server.dto.response.MatchResponse;

import java.util.List;

public interface MatchService {

    MatchResponse createMatch(MatchCreateRequest request);

    MatchResponse getMatchById(Long id);

    List<MatchResponse> getAllUpcomingMatches();

    List<MatchResponse> getMatchesByPhase(String phase);

    List<MatchResponse> getMatchesByGroup(String group);

    void updateMatchScore(Long id, Integer matchScore);
}