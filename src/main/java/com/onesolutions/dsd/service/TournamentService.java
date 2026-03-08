package com.onesolutions.dsd.service;

import com.onesolutions.dsd.dto.CreateTeamRequestDTO;
import com.onesolutions.dsd.dto.TournamentRequestDTO;
import com.onesolutions.dsd.dto.TournamentResponseDTO;
import com.onesolutions.dsd.dto.UserJoinedResponseDTO;

import java.util.List;

public interface TournamentService {

    TournamentResponseDTO createTournament(TournamentRequestDTO request);

    List<TournamentResponseDTO> getAllTournaments();

    TournamentResponseDTO getTournamentById(Long id);

    void createTeam(CreateTeamRequestDTO request);

    void deleteTournament(Long id);
    List<UserJoinedResponseDTO> getJoinedUsers(Long tournamentId);

    void joinTournament(Long tournamentId, Long userId);
}