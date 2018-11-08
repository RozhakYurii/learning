package com.example.learning.controllers;

import com.example.learning.model.Message;
import com.example.learning.model.User;
import com.example.learning.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.example.learning.utils.ValidationUtils.getValidationErrors;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    MessageRepository messageRepository;

    private static final String MESSAGE_VIEW_NAME = "message/messages";

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model) {

        Iterable<Message> messages;

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepository.findAllByTag(filter);
        } else {
            messages = messageRepository.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return MESSAGE_VIEW_NAME;
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid Message message,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile file,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorAttributes = getValidationErrors(bindingResult);
            model.mergeAttributes(errorAttributes);
        } else {
            message.setAuthor(user);
            if (file != null && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
                final File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    boolean isDirCreated = uploadDir.mkdir();
                }
                String uuidFileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
                try {
                    final File dest = new File(uploadDir.getAbsolutePath(), uuidFileName);
                    file.transferTo(dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message.setFilename(uuidFileName);
            }
            messageRepository.save(message);
        }
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);

        return MESSAGE_VIEW_NAME;
    }


}
