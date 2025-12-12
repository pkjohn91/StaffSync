package com.staffSync.employee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false)
    private Double salary;

    // 생성자
    public Employee(String name, String position, String department, Double salary) {
        validateName(name);
        validatePosition(position);
        validateDepartment(department);
        validateSalary(salary);

        this.name = name;
        this.position = position;
        this.department = department;
        this.salary = salary;
    }

    // 검증 메서드
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }

    private void validatePosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("직급은 필수입니다.");
        }
    }

    private void validateDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("부서는 필수입니다.");
        }
    }

    private void validateSalary(Double salary) {
        if (salary == null || salary <= 0) {
            throw new IllegalArgumentException("급여는 0보다 커야 합니다.");
        }
    }

    // 비즈니스 메서드 (급여 조정)
    public void updateSalary(Double newSalary) {
        validateSalary(newSalary);
        this.salary = newSalary;
    }
}
