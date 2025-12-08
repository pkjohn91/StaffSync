package com.example.prtcHr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.prtcHr.entity.Employee;
import com.example.prtcHr.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:5173") // Vite 기본 포트 허용
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // 전체 조회
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // 직원 추가
    @PostMapping
    public Employee creatEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    // 직원 삭제
    @DeleteMapping("/{id}")
    public void DeleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }
}
