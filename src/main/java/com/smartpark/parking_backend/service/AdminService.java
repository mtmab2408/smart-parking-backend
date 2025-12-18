package com.smartpark.parking_backend.service;

import com.smartpark.parking_backend.model.Admin;
import com.smartpark.parking_backend.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    //will have to see how i can encrypt the password here.
    public Admin createAdmin(Admin admin){
        return adminRepository.save(admin);
    }

    public List<Admin> getAllAdmins(){
        return adminRepository.findAll();
    }

    public boolean validateLogin(String username,String password){
        List<Admin> admins = adminRepository.findAll();
        for(Admin admin : admins){
            if(admin.getUsername().equals(username)&&admin.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

}
