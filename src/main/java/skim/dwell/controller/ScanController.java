package skim.dwell.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import skim.dwell.model.CheckoutMethod;
import skim.dwell.model.Visit;
import skim.dwell.repository.SupervisorRepository;
import skim.dwell.service.VisitService;

@Controller
public class ScanController {

    private static final String COOKIE = "dwellEmail";

    private final VisitService visitService;
    private final SupervisorRepository supervisors;

    public ScanController(VisitService visitService, SupervisorRepository supervisors) {
        this.visitService = visitService;
        this.supervisors = supervisors;
    }

    @GetMapping("/scan")
    public String scan(@CookieValue(value = COOKIE, required = false) String email, Model model) {
        if (email == null) {
            model.addAttribute("step", "identify");
            return "scan";
        }
        return showStep(email, model);
    }

    @PostMapping("/scan")
    public String identify(@RequestParam String email, Model model) {
        return showStep(email, model);
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestParam(required = false) String name,
                          @RequestParam String email,
                          @RequestParam Long supervisorId,
                          HttpServletResponse response,
                          Model model) {
        Visit visit = visitService.checkIn(name, email, supervisorId);
        rememberEmail(response, email);
        model.addAttribute("step", "done");
        model.addAttribute("message", "Checked in. Welcome, " + visit.getStudent().getName() + "!");
        return "scan";
    }

    @PostMapping("/checkout")
    public String checkOut(@RequestParam String email, Model model) {
        visitService.checkOut(email, CheckoutMethod.QR);
        model.addAttribute("step", "done");
        model.addAttribute("message", "Checked out. See you next time!");
        return "scan";
    }

    @GetMapping("/scan/forget")
    public String forget(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/scan";
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public String handleError(RuntimeException e, Model model) {
        model.addAttribute("step", "done");
        model.addAttribute("message", e.getMessage());
        return "scan";
    }

    private String showStep(String email, Model model) {
        model.addAttribute("email", email);
        if (visitService.findOpenVisit(email).isPresent()) {
            model.addAttribute("step", "checkout");
        } else {
            model.addAttribute("step", "checkin");
            model.addAttribute("isNew", visitService.findStudent(email).isEmpty());
            model.addAttribute("supervisors", supervisors.findAll());
        }
        return "scan";
    }

    private void rememberEmail(HttpServletResponse response, String email) {
        Cookie cookie = new Cookie(COOKIE, email.trim().toLowerCase());
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}