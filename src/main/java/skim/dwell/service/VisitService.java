package skim.dwell.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skim.dwell.model.*;
import skim.dwell.repository.*;
import java.util.List;
import java.util.Optional;

@Service
public class VisitService {

    private final StudentRepository students;
    private final SupervisorRepository supervisors;
    private final VisitRepository visits;

    public VisitService(StudentRepository students,
                        SupervisorRepository supervisors,
                        VisitRepository visits) {
        this.students = students;
        this.supervisors = supervisors;
        this.visits = visits;
    }

    public Optional<Visit> findOpenVisit(String email) {
        return students.findByEmail(normalize(email))
                .flatMap(visits::findByStudentAndTimeOutIsNull);
    }

    public Optional<Student> findStudent(String email) {
    return students.findByEmail(normalize(email));
}

    @Transactional
    public Visit checkIn(String name, String email, Long supervisorId) {
        String cleanEmail = normalize(email);

        Student student = students.findByEmail(cleanEmail)
                .orElseGet(() -> students.save(new Student(name.trim(), cleanEmail)));

        if (visits.findByStudentAndTimeOutIsNull(student).isPresent()) {
            throw new IllegalStateException("Already checked in");
        }

        Supervisor supervisor = supervisors.findById(supervisorId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown supervisor"));

        return visits.save(new Visit(student, supervisor));
    }

    @Transactional
    public Visit checkOut(String email, CheckoutMethod method) {
        Visit visit = findOpenVisit(email)
                .orElseThrow(() -> new IllegalStateException("Not checked in"));
        visit.checkOut(method);
        return visits.save(visit);
    }

    public List<Visit> whoIsHere() {
        return visits.findByTimeOutIsNull();
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}