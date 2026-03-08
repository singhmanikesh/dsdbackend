package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.dto.*;
import com.onesolutions.dsd.entity.Team;
import com.onesolutions.dsd.entity.TeamMember;
import com.onesolutions.dsd.entity.Tournament;
import com.onesolutions.dsd.entity.UserEntity;
import com.onesolutions.dsd.repository.TeamMemberRepository;
import com.onesolutions.dsd.repository.TeamRepository;
import com.onesolutions.dsd.repository.TournamentRepository;
import com.onesolutions.dsd.repository.profileRepo;
import com.onesolutions.dsd.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        Tournament tournament = toEntity(request);
        tournament = tournamentRepository.save(tournament);

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

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .tournamentId(request.getTournamentId())
                .teamLeaderGamerName(request.getTeamLeaderGamerName())
                .build();

        teamRepository.save(team);

        for(String gamerName : request.getGamerNames()){

            UserEntity user = profileRepo.findByGamerName(gamerName)
                    .orElseThrow(() -> new RuntimeException("User not found: " + gamerName));

            TeamMember member = TeamMember.builder()
                    .userId(user.getId())
                    .team(team)
                    .build();

            teamMemberRepository.save(member);
        }
    }


    @Override
    public void joinTournament(Long tournamentId, Long userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        UserEntity user = profileRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if tournament expired
        if (tournament.getExpired()) {
            throw new RuntimeException("Tournament already expired");
        }

        // Check duplicate join
        if (tournament.getUsersJoined().contains(user)) {
            throw new RuntimeException("User already joined");
        }

        // Add user
        tournament.getUsersJoined().add(user);

        // Update count
        tournament.setTotalJoined(tournament.getTotalJoined() + 1);

        tournamentRepository.save(tournament);
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

    private Tournament toEntity(TournamentRequestDTO dto) {

        return Tournament.builder()
                .tournamentName(dto.getTournamentName())
                .tournamentCategory(dto.getTournamentCategory())
                .tournamentExpiry(dto.getTournamentExpiry())
                .tournamentPrize(dto.getTournamentPrize())
                .organizerName(dto.getOrganizerName())
                .gameName(dto.getGameName())
                .description(dto.getDescription())
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
                .build();
    }
}


