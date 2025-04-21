package com.projects.server.services.interfaces;

import com.projects.server.dto.request.MatchCreateRequest;
import com.projects.server.dto.response.MatchResponse;

import java.time.LocalDate;
import java.util.List;

public interface MatchService {

    MatchResponse createMatch(MatchCreateRequest request);

    MatchResponse getMatchById(Long id);

    List<MatchResponse> getAllUpcomingMatches();

    List<MatchResponse> getMatchesByPhase(String phase);

    List<MatchResponse> getMatchesByGroup(String group);

    List<MatchResponse> getMatchesByDate(LocalDate date);

    List<MatchResponse> getMatchesByTeam(String teamId);

    List<MatchResponse> getMatchesByStadium(String stadiumId);

    void updateMatchScore(Long id, Integer matchScore);




}