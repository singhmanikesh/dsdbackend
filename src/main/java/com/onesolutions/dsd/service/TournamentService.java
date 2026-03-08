package com.onesolutions.dsd.service;

import com.onesolutions.dsd.dto.*;

import java.util.List;

public interface TournamentService {

    TournamentResponseDTO createTournament(TournamentRequestDTO request);

    List<TournamentResponseDTO> getAllTournaments();

    List<TeamResponseDTO> getTeamsByTournament(Long tournamentId);

    TournamentResponseDTO getTournamentById(Long id);

    void createTeam(CreateTeamRequestDTO request);

    void deleteTournament(Long id);
    List<UserJoinedResponseDTO> getJoinedUsers(Long tournamentId);

    void joinTournament(Long tournamentId, Long userId);
}