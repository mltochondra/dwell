package skim.dwell.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    @Column(nullable = false)
    private LocalDateTime timeIn;

    private LocalDateTime timeOut;

    @Enumerated(EnumType.STRING)
    private CheckoutMethod checkoutMethod;

    protected Visit() {}

    public Visit(Student student, Supervisor supervisor) {
        this.student = student;
        this.supervisor = supervisor;
        this.timeIn = LocalDateTime.now();
    }

    public void checkOut(CheckoutMethod method) {
        this.timeOut = LocalDateTime.now();
        this.checkoutMethod = method;
    }

    public boolean isActive() {
        return timeOut == null;
    }

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Supervisor getSupervisor() { return supervisor; }
    public LocalDateTime getTimeIn() { return timeIn; }
    public LocalDateTime getTimeOut() { return timeOut; }
    public CheckoutMethod getCheckoutMethod() { return checkoutMethod; }
}