package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.Role;
import com.example.Social_Media_WhichApp.repository.RoleRepository;
import com.example.Social_Media_WhichApp.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/roles")
public class RoleController {
    @Autowired
    private  RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public List<Role> getAllRoles(){
        return roleService.getAllRoles();
    }
}
