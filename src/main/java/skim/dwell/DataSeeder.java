package skim.dwell;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import skim.dwell.model.Supervisor;
import skim.dwell.repository.SupervisorRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SupervisorRepository supervisors;

    public DataSeeder(SupervisorRepository supervisors) {
        this.supervisors = supervisors;
    }

    @Override
    public void run(String... args) {
        if (supervisors.count() == 0) {

            // currently placeholder: will add actual supervisors later
            supervisors.save(new Supervisor("Supervisor One", "one@example.com"));
            supervisors.save(new Supervisor("Supervisor Two", "two@example.com"));
        }
    }
}