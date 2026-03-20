package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.dto.*;
import com.onesolutions.dsd.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping("/create")
    public TournamentResponseDTO createTournament(
            @RequestBody TournamentRequestDTO request) {

        return tournamentService.createTournament(request);
    }

    @PostMapping("/create-team")
    public String createTeam(@RequestBody CreateTeamRequestDTO request){

        tournamentService.createTeam(request);

        return "Team created successfully";
    }

    @GetMapping("/{id:\\d+}/teams")
    public List<TeamResponseDTO> getTeamsByTournament(@PathVariable Long id){

        return tournamentService.getTeamsByTournament(id);

    }

    @GetMapping
    public List<TournamentResponseDTO> getAllTournaments() {

        return tournamentService.getAllTournaments();
    }

    @GetMapping("/{id:\\d+}")
    public TournamentResponseDTO getTournamentById(@PathVariable Long id) {

        return tournamentService.getTournamentById(id);
    }

    @DeleteMapping("/{id:\\d+}")
    public String deleteTournament(@PathVariable Long id) {

        tournamentService.deleteTournament(id);

        return "Tournament deleted successfully";
    }

    @GetMapping("/{id:\\d+}/users")
    public List<UserJoinedResponseDTO> getJoinedUsers(@PathVariable Long id) {

        return tournamentService.getJoinedUsers(id);
    }


    @PostMapping("/{id:\\d+}/join")
    public String joinTournament(
            @PathVariable Long id,
            @RequestBody JoinTournamentRequestDTO request) {

        tournamentService.joinTournament(id, request.getUserId());

        return "User joined tournament successfully";
    }
}