package com.example.prtcHr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.prtcHr.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
