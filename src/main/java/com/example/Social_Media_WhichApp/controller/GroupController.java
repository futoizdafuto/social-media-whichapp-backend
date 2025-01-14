package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:8443")
@RestController
@RequestMapping("api/groups")
public class GroupController {
    @Autowired
    private MessageService messageService;



}
