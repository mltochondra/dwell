package skim.dwell.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import skim.dwell.service.VisitService;

@Controller
public class RoomController {

    private final VisitService visitService;

    public RoomController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping({"/", "/room"})
    public String room(Model model) {
        model.addAttribute("visits", visitService.whoIsHere());
        return "room";
    }
}