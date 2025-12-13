package com.staffSync.employee.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사원번호 (예: EMP001, EMP002)
     */
    @Column(name = "employee_id", nullable = false, unique = true, length = 20)
    private String employeeId;

    /**
     * 이름
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 이메일 (고유값)
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 입사일
     */
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    /**
     * 급여
     */
    @Column(name = "salary", nullable = false)
    private Double salary;

    /**
     * 부서
     */
    @Column(name = "department", length = 50)
    private String department;

    /**
     * 생성자
     * 
     * @param employeeId 사원번호
     * @param name       이름
     * @param email      이메일
     * @param hireDate   입사일
     * @param salary     급여
     * @param department 부서
     */
    public Employee(String employeeId, String name, String email,
            LocalDate hireDate, Double salary, String department) {
        validateEmployeeId(employeeId);
        validateName(name);
        validateEmail(email);
        validateSalary(salary);

        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
        this.department = department;
    }

    /**
     * 직원 정보 수정
     * 
     * @param name       이름
     * @param email      이메일
     * @param salary     급여
     * @param department 부서
     */
    public void update(String name, String email, Double salary, String department) {
        if (name != null && !name.trim().isEmpty()) {
            validateName(name);
            this.name = name;
        }
        if (email != null && !email.trim().isEmpty()) {
            validateEmail(email);
            this.email = email;
        }
        if (salary != null && salary >= 0) {
            validateSalary(salary);
            this.salary = salary;
        }
        if (department != null) {
            this.department = department;
        }
    }

    // ===== Validation 메서드 =====

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("사원번호는 필수입니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        // 간단한 이메일 정규식
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    private void validateSalary(Double salary) {
        if (salary == null || salary < 0) {
            throw new IllegalArgumentException("급여는 0 이상이어야 합니다.");
        }
    }
}