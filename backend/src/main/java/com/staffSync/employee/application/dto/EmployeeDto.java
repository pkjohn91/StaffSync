package com.staffSync.employee.application.dto;

import com.staffSync.employee.domain.Employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;
    private String name;
    private String position;
    private String department;
    private Double salary;

    // Entity -> DTO 변환
    public static EmployeeDto from(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getPosition(),
                employee.getDepartment(),
                employee.getSalary());
    }
}
