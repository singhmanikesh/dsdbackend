package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.dto.TournamentResponseDTO;
import com.onesolutions.dsd.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/global/tournaments")
@RequiredArgsConstructor
public class GlobalTournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public List<TournamentResponseDTO> getGlobalTournaments() {
        return tournamentService.getAllTournaments();
    }
}

