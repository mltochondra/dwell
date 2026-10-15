package skim.dwell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skim.dwell.model.Student;
import skim.dwell.model.Visit;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<Visit> findByStudentAndTimeOutIsNull(Student student);
    List<Visit> findByTimeOutIsNullOrderByTimeInAsc();
}