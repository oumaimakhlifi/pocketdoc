package pocketDock.com.pocketDock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pocketDock.com.pocketDock.dto.MailRequest;
import pocketDock.com.pocketDock.dto.MailResponse;
import pocketDock.com.pocketDock.service.EmailService;

import java.util.HashMap;
import java.util.Map;
@CrossOrigin("*")
@EnableScheduling
@SpringBootApplication
@RestController
public class PocketDockApplication {
	@Autowired
	private EmailService service;

	@PostMapping("/api/sendingEmail")
	public MailResponse sendEmail(@RequestBody MailRequest request) {
		Map<String, Object> model = new HashMap<>();
		model.put("Name", "omaima");
		model.put("location", "Bangalore,India");
		return service.sendEmail(request, model);

	}

	public static void main(String[] args) {
		SpringApplication.run(PocketDockApplication.class, args);
	}

}
