package com.smartpark.parking_backend.controller;
import com.smartpark.parking_backend.model.Admin;
import com.smartpark.parking_backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService=adminService;
    }

    //for loging in
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        boolean isValid = adminService.validateLogin(username, password);

        if(isValid){
            return ResponseEntity.ok().body(Map.of("message","Login Seuccessful", "status","success"));            
        }
        else{
            return ResponseEntity.status(401).body(Map.of("message","Login Unsuccessful", "status","error"));
        }    
    }


    //Admin Management
    @PostMapping("/register")
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }

    @GetMapping("/all")
    public List<Admin> getAllAdmins(){
        return adminService.getAllAdmins();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id){
        adminService.deleteAdmin(id);
        return ResponseEntity.ok().body("Admin deleted");
    }

    @PutMapping("/{id}")
    public Admin updateAdmin(@PathVariable Long id, @RequestBody Admin admin){
        return adminService.updateAdmin(id, admin);
    }

    
    
    
}
