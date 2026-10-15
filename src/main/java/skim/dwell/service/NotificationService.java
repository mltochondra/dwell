package skim.dwell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import skim.dwell.model.Visit;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final JavaMailSender mailSender;
    private final String from;
    private final String teacherEmail;

    public NotificationService(JavaMailSender mailSender,
                               @Value("${spring.mail.username:}") String from,
                               @Value("${dwell.teacher-email:}") String teacherEmail) {
        this.mailSender = mailSender;
        this.from = from;
        this.teacherEmail = teacherEmail;
    }

    @Async
    public void notifyCheckIn(Visit visit) {
        String name = visit.getStudent().getName();
        send(visit,
             "[Dwell] " + name + " checked in",
             name + " checked in to the Tech & Innovation room at "
                 + visit.getTimeIn().format(TIME) + ".\n"
                 + "Supervisor: " + visit.getSupervisor().getName());
    }

    @Async
    public void notifyCheckOut(Visit visit) {
        String name = visit.getStudent().getName();
        send(visit,
             "[Dwell] " + name + " checked out",
             name + " checked out at " + visit.getTimeOut().format(TIME)
                 + " after " + visit.getMinutes() + " minutes.\n"
                 + "Method: " + visit.getCheckoutMethod());
    }

    private void send(Visit visit, String subject, String body) {
        Set<String> to = new LinkedHashSet<>();
        if (!teacherEmail.isBlank()) to.add(teacherEmail);
        to.add(visit.getSupervisor().getEmail());

        if (from.isBlank()) {
            log.warn("Email not configured; skipping: {}", subject);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to.toArray(String[]::new));
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.warn("Email failed: {}", e.getMessage());
        }
    }
}