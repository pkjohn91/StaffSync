package com.staffSync.application.employee.dto;

import com.staffSync.domain.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String employeeId; // 사원번호
    private String name;
    private String email;
    private LocalDate hireDate;
    private Double salary;
    private String department;

    /**
     * Entity -> DTO 변환
     */
    public static EmployeeDto from(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getHireDate(),
                employee.getSalary(),
                employee.getDepartment());
    }
}