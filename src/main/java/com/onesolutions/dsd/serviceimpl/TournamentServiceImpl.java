package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.dto.*;
import com.onesolutions.dsd.entity.Team;
import com.onesolutions.dsd.entity.TeamMember;
import com.onesolutions.dsd.entity.Tournament;
import com.onesolutions.dsd.entity.UserEntity;
import com.onesolutions.dsd.exception.ResourceNotFoundException;
import com.onesolutions.dsd.exception.TournamentExpiredException;
import com.onesolutions.dsd.exception.UserAlreadyJoinedException;
import com.onesolutions.dsd.repository.TeamMemberRepository;
import com.onesolutions.dsd.repository.TeamRepository;
import com.onesolutions.dsd.repository.TournamentRepository;
import com.onesolutions.dsd.repository.profileRepo;
import com.onesolutions.dsd.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private   final profileRepo profileRepo;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public TournamentResponseDTO createTournament(TournamentRequestDTO request) {

        // Get current authenticated user (tournament creator)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        UserEntity creator = profileRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and save tournament
        Tournament tournament = toEntity(request);
        tournament = tournamentRepository.save(tournament);

        // Add HP to the tournament creator using admin-configured reward (default: 10 if not specified)
        int hpReward = request.getHpReward() != null && request.getHpReward() > 0 
            ? request.getHpReward() 
            : 10;
        creator.setHp(creator.getHp() + hpReward);
        profileRepo.save(creator);

        return toDto(tournament);
    }


    @Override
    public List<TeamResponseDTO> getTeamsByTournament(Long tournamentId) {

        List<Team> teams = teamRepository.findByTournamentId(tournamentId);

        return teams.stream().map(team -> {

            List<TeamMember> members = teamMemberRepository.findByTeamTeamId(team.getTeamId());

            List<String> players = members.stream()
                    .map(member -> {
                        return profileRepo.findById(member.getUserId())
                                .map(user -> user.getGamerName())
                                .orElse("Unknown");
                    })
                    .toList();

            return TeamResponseDTO.builder()
                    .teamName(team.getTeamName())
                    .teamLeader(team.getTeamLeaderGamerName())
                    .players(players)
                    .build();

        }).toList();
    }

    @Override
    public void createTeam(CreateTeamRequestDTO request) {

        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (tournament.getExpired()) {
            throw new RuntimeException("Tournament already expired");
        }

        if (request.getGamerNames() == null || request.getGamerNames().isEmpty()) {
            throw new RuntimeException("At least one gamer is required");
        }

        LinkedHashSet<String> uniqueGamerNames = new LinkedHashSet<>(request.getGamerNames());
        if (request.getTeamLeaderGamerName() != null && !request.getTeamLeaderGamerName().isBlank()) {
            uniqueGamerNames.add(request.getTeamLeaderGamerName());
        }

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .tournamentId(request.getTournamentId())
                .teamLeaderGamerName(request.getTeamLeaderGamerName())
                .build();

        teamRepository.save(team);

        for (String gamerName : uniqueGamerNames) {

            UserEntity user = profileRepo.findByGamerName(gamerName)
                    .orElseThrow(() -> new RuntimeException("User not found: " + gamerName));

            TeamMember member = TeamMember.builder()
                    .userId(user.getId())
                    .team(team)
                    .build();

            teamMemberRepository.save(member);

            boolean alreadyJoined = tournament.getUsersJoined().stream()
                    .anyMatch(joinedUser -> joinedUser.getId().equals(user.getId()));

            if (!alreadyJoined) {
                tournament.getUsersJoined().add(user);
                tournament.setTotalJoined(tournament.getTotalJoined() + 1);
            }
        }

        tournamentRepository.save(tournament);
    }


    @Override
    public void joinTournament(Long tournamentId, Long userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with ID: " + tournamentId));

        UserEntity user = profileRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if tournament expired
        if (tournament.getExpired()) {
            throw new TournamentExpiredException("This tournament has already expired and is no longer accepting new members");
        }

        // Check duplicate join by id to avoid equals/hashCode dependency.
        boolean alreadyJoined = tournament.getUsersJoined().stream()
                .anyMatch(joinedUser -> joinedUser.getId().equals(user.getId()));
        if (alreadyJoined) {
            throw new UserAlreadyJoinedException("User with email '" + user.getEmail() + "' has already joined this tournament");
        }

        // Add user to tournament (owning side)
        tournament.getUsersJoined().add(user);
        
        // Update count
        tournament.setTotalJoined(tournament.getTotalJoined() + 1);

        // Save tournament (this updates the join table)
        tournamentRepository.save(tournament);
        
        // Refresh user to sync the bidirectional relationship (mapped side)
        profileRepo.save(user);
    }

    @Override
    public JoinTournamentResponseDTO joinTournamentWithResponse(Long tournamentId, Long userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with ID: " + tournamentId));

        UserEntity user = profileRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if tournament expired
        if (tournament.getExpired()) {
            throw new TournamentExpiredException("This tournament has already expired and is no longer accepting new members");
        }

        // Check duplicate join by id to avoid equals/hashCode dependency.
        boolean alreadyJoined = tournament.getUsersJoined().stream()
                .anyMatch(joinedUser -> joinedUser.getId().equals(user.getId()));
        if (alreadyJoined) {
            throw new UserAlreadyJoinedException("User with email '" + user.getEmail() + "' has already joined this tournament");
        }

        // Add user to tournament (owning side)
        tournament.getUsersJoined().add(user);
        
        // Update count
        tournament.setTotalJoined(tournament.getTotalJoined() + 1);

        // Save tournament (this updates the join table)
        tournamentRepository.save(tournament);
        
        // Refresh user to sync the bidirectional relationship (mapped side)
        profileRepo.save(user);

        // Return detailed response
        return JoinTournamentResponseDTO.builder()
                .tournamentId(tournament.getTournamentId())
                .tournamentName(tournament.getTournamentName())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .gamerName(user.getGamerName())
                .message("User joined tournament successfully")
                .joinedAt(java.time.LocalDateTime.now())
                .totalJoinedNow(tournament.getTotalJoined())
                .build();
    }

    @Override
    public List<UserJoinedResponseDTO> getJoinedUsers(Long tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        return tournament.getUsersJoined()
                .stream()
                .map(user -> UserJoinedResponseDTO.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .gamerName(user.getGamerName())
                        .hp(user.getHp())
                        .build())
                .toList();
    }

    @Override
    public List<TournamentResponseDTO> getAllTournaments() {

        return tournamentRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedTournamentResponseDTO getAllTournamentsPaginated(Pageable pageable) {

        // Create pageable with descending sort by tournamentCreated (latest first)
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "tournamentCreated")
        );

        Page<Tournament> tournaments = tournamentRepository.findAll(sortedPageable);

        List<TournamentResponseDTO> content = tournaments.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return PaginatedTournamentResponseDTO.builder()
                .content(content)
                .pageNumber(tournaments.getNumber())
                .pageSize(tournaments.getSize())
                .totalElements(tournaments.getTotalElements())
                .totalPages(tournaments.getTotalPages())
                .isFirst(tournaments.isFirst())
                .isLast(tournaments.isLast())
                .build();
    }

    @Override
    public TournamentResponseDTO getTournamentById(Long id) {

        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        return toDto(tournament);
    }

    @Override
    public void deleteTournament(Long id) {

        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        tournamentRepository.delete(tournament);
    }

    @Override
    @Transactional
    public List<UserTournamentDTO> getUserTournaments(Long userId) {

        UserEntity user = profileRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getTournamentsJoined()
                .stream()
                .map(tournament -> UserTournamentDTO.builder()
                        .tournamentId(tournament.getTournamentId())
                        .tournamentName(tournament.getTournamentName())
                        .tournamentCategory(tournament.getTournamentCategory())
                        .tournamentCreated(tournament.getTournamentCreated())
                        .tournamentExpiry(tournament.getTournamentExpiry())
                        .tournamentPrize(tournament.getTournamentPrize())
                        .totalJoined(tournament.getTotalJoined())
                        .organizerName(tournament.getOrganizerName())
                        .gameName(tournament.getGameName())
                        .description(tournament.getDescription())
                        .expired(tournament.getExpired())
                        .build())
                .toList();
    }

    private Tournament toEntity(TournamentRequestDTO dto) {

        return Tournament.builder()
                .tournamentName(dto.getTournamentName())
                .tournamentCategory(dto.getTournamentCategory())
                .tournamentExpiry(dto.getTournamentExpiry())
                .tournamentPrize(dto.getTournamentPrize())
                .organizerName(dto.getOrganizerName())
                .gameName(dto.getGameName())
                .description(dto.getDescription())
                .hpReward(dto.getHpReward() != null ? dto.getHpReward() : 10)  // Default to 10 if not specified
                .build();
    }

    private TournamentResponseDTO toDto(Tournament tournament) {

        return TournamentResponseDTO.builder()
                .tournamentId(tournament.getTournamentId())
                .tournamentName(tournament.getTournamentName())
                .tournamentCategory(tournament.getTournamentCategory())
                .tournamentCreated(tournament.getTournamentCreated())
                .tournamentExpiry(tournament.getTournamentExpiry())
                .tournamentPrize(tournament.getTournamentPrize())
                .totalJoined(tournament.getTotalJoined())
                .organizerName(tournament.getOrganizerName())
                .gameName(tournament.getGameName())
                .description(tournament.getDescription())
                .hpReward(tournament.getHpReward())
                .build();
    }
}


