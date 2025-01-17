package com.chromeextension.emailassistant.rest;

import com.chromeextension.emailassistant.data.EmailBody;
import com.chromeextension.emailassistant.service.EmailGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AssistantController {

    private final EmailGeneratorService emailGeneratorService;

    public AssistantController(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }

    @CrossOrigin(origins = "https://mail.google.com")
    @PostMapping("/generateEmail")
    public ResponseEntity<String> getAIGeneratedReply(@RequestBody EmailBody emailBody) {
        return ResponseEntity.ok(emailGeneratorService.generateEmailReply(emailBody));
    }

}
