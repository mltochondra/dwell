package skim.dwell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skim.dwell.model.Student;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}