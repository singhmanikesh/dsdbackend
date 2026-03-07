package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.dto.JoinTournamentRequestDTO;
import com.onesolutions.dsd.dto.TournamentRequestDTO;
import com.onesolutions.dsd.dto.TournamentResponseDTO;
import com.onesolutions.dsd.dto.UserJoinedResponseDTO;
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

    @GetMapping
    public List<TournamentResponseDTO> getAllTournaments() {

        return tournamentService.getAllTournaments();
    }

    @GetMapping("/{id}")
    public TournamentResponseDTO getTournamentById(@PathVariable Long id) {

        return tournamentService.getTournamentById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteTournament(@PathVariable Long id) {

        tournamentService.deleteTournament(id);

        return "Tournament deleted successfully";
    }

    @GetMapping("/{id}/users")
    public List<UserJoinedResponseDTO> getJoinedUsers(@PathVariable Long id) {

        return tournamentService.getJoinedUsers(id);
    }


    @PostMapping("/{id}/join")
    public String joinTournament(
            @PathVariable Long id,
            @RequestBody JoinTournamentRequestDTO request) {

        tournamentService.joinTournament(id, request.getUserId());

        return "User joined tournament successfully";
    }
}