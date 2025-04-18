package com.projects.server.mapper;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.dto.request.MatchCreateRequest;
import com.projects.server.dto.response.MatchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matchScore", ignore = true)
    @Mapping(target = "availableTickets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Match toEntity(MatchCreateRequest request);

    @Mapping(target = "matchTitle", expression = "java(match.getMatchTitle())")
    @Mapping(target = "sectionPrices", source = ".", qualifiedByName = "calculateSectionPrices")
    @Mapping(target = "group", source = "matchGroup")
    MatchResponse toResponse(Match match);

    @Named("calculateSectionPrices")
    default Map<SectionType, Double> calculateSectionPrices(Match match) {
        Map<SectionType, Double> prices = new HashMap<>();
        for (SectionType sectionType : SectionType.values()) {
            prices.put(sectionType, match.getPriceForSection(sectionType));
        }
        return prices;
    }
}