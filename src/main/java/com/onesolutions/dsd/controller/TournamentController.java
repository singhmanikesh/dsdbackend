package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.dto.*;
import com.onesolutions.dsd.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/paginated")
    public ResponseEntity<PaginatedTournamentResponseDTO> getAllTournamentsPaginated(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        PaginatedTournamentResponseDTO response = tournamentService.getAllTournamentsPaginated(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
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
    public ResponseEntity<JoinTournamentResponseDTO> joinTournament(
            @PathVariable Long id,
            @RequestBody JoinTournamentRequestDTO request) {

        JoinTournamentResponseDTO response = tournamentService.joinTournamentWithResponse(id, request.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId:\\d+}")
    public List<UserTournamentDTO> getUserTournaments(@PathVariable Long userId) {

        return tournamentService.getUserTournaments(userId);
    }
}