package com.smartpark.parking_backend.repository;
import com.smartpark.parking_backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface AdminRepository extends JpaRepository<Admin,Long> {}