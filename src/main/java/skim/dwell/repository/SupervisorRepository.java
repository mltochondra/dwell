package skim.dwell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skim.dwell.model.Supervisor;

public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {
}