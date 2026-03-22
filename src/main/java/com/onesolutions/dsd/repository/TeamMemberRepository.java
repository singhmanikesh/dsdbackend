package com.onesolutions.dsd.repository;

import com.onesolutions.dsd.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamTeamId(Long teamId);
    void deleteByTeamTeamId(Long teamId);
}