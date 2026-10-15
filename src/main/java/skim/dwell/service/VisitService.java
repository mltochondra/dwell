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
    private final NotificationService notifications;

    public VisitService(StudentRepository students,
                        SupervisorRepository supervisors,
                        VisitRepository visits,
                        NotificationService notifications) {
        this.students = students;
        this.supervisors = supervisors;
        this.visits = visits;
        this.notifications = notifications;
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

        Visit saved = visits.save(new Visit(student, supervisor));
        notifications.notifyCheckIn(saved);
        return saved;
    }

    @Transactional
    public Visit checkOut(String email, CheckoutMethod method) {
        Visit visit = findOpenVisit(email)
                .orElseThrow(() -> new IllegalStateException("Not checked in"));
        visit.checkOut(method);
        Visit saved = visits.save(visit);
        notifications.notifyCheckOut(saved);
        return saved;
    }

    public List<Visit> whoIsHere() {
        return visits.findByTimeOutIsNullOrderByTimeInAsc();
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}