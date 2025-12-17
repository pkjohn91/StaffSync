package com.staffSync.application.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {
    private String name;
    private String email;
    private LocalDate hireDate;
    private Double salary;
    private String department;
}