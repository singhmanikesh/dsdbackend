package com.onesolutions.dsd.repository;

import com.onesolutions.dsd.entity.Team;
import com.onesolutions.dsd.entity.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Team> findByTournamentId(Long tournamentId);

    Page<Tournament> findAll(Pageable pageable);

}