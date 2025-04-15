package com.projects.server.services.impl;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.enums.CompetitionPhaseType;
import com.projects.server.domain.enums.GroupType;
import com.projects.server.domain.enums.StadiumType;
import com.projects.server.domain.enums.TeamType;
import com.projects.server.dto.request.MatchCreateRequest;
import com.projects.server.dto.response.MatchResponse;
import com.projects.server.exceptions.ResourceNotFoundException;
import com.projects.server.mapper.MatchMapper;
import com.projects.server.repositories.MatchRepository;
import com.projects.server.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Override
    @Transactional
    public MatchResponse createMatch(MatchCreateRequest request) {
        Match match = matchMapper.toEntity(request);
        match = matchRepository.save(match);
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id) {
        Match match = findMatchById(id);
        return matchMapper.toResponse(match);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getAllUpcomingMatches() {
        List<Match> matches = matchRepository.findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now());
        return matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByPhase(String phase) {
        CompetitionPhaseType phaseType = CompetitionPhaseType.valueOf(phase);
        List<Match> matches = matchRepository.findByPhaseOrderByDateTimeAsc(phaseType);
        return matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByGroup(String group) {
        GroupType groupType = GroupType.valueOf(group);
        List<Match> matches = matchRepository.findByMatchGroupOrderByDateTimeAsc(groupType);  // Nom de méthode mis à jour
        return matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public void updateMatchScore(Long id, Integer matchScore) {
        Match match = findMatchById(id);
        match.setMatchScore(matchScore);
        matchRepository.save(match);
    }


    private Match findMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByDate(LocalDate date) {
        // Convertir LocalDate en LocalDateTime pour le début et la fin de la journée
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Requête pour trouver tous les matchs de cette journée
        List<Match> matches = matchRepository.findByDateTimeBetweenOrderByDateTimeAsc(startOfDay, endOfDay);

        return matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByTeam(String teamId) {
        // Convertir l'ID d'équipe en enum TeamType
        TeamType team = TeamType.valueOf(teamId);

        // Requête pour trouver tous les matchs où cette équipe joue (domicile ou extérieur)
        List<Match> matches = matchRepository.findByHomeTeamOrAwayTeamOrderByDateTimeAsc(team, team);

        return matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByStadium(String stadiumId) {
        // Convertir l'ID de stade en enum StadiumType
        StadiumType stadium = StadiumType.valueOf(stadiumId);

        // Requête pour trouver tous les matchs dans ce stade
        List<Match> matches = matchRepository.findByStadiumOrderByDateTimeAsc(stadium);

        return matches.stream()
                .map(matchMapper::toResponse)
                .collect(Collectors.toList());
    }
}