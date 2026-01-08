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

    public Admin updateAdmin(Long id, Admin updatedAdmin){
        return adminRepository.findById(id)
            .map(existing -> {
                existing.setUsername(updatedAdmin.getUsername());
                // update password only if provided
                if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
                    existing.setPassword(updatedAdmin.getPassword());
                }
                return adminRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public List<Admin> getAllAdmins(){
        return adminRepository.findAll();
    }

    public void deleteAdmin(Long id){
        adminRepository.deleteById(id);
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
