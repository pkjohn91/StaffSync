package com.staffSync.employee.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 부서별 조회
    List<Employee> findByDepartment(String department);

    // 직급별 조회
    List<Employee> findByPosition(String position);

    // 이름으로 검색 (부분 일치)
    List<Employee> findByNameContaining(String keyword);
}
