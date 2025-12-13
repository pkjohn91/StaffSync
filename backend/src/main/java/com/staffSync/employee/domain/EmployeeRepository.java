package com.staffSync.employee.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * 사원번호로 직원 조회
     * 
     * @param employeeId 사원번호
     * @return 직원 정보
     */
    Optional<Employee> findByEmployeeId(String employeeId);

    /**
     * 이메일로 직원 조회
     * 
     * @param email 이메일
     * @return 직원 정보
     */
    Optional<Employee> findByEmail(String email);

    /**
     * 사원번호 중복 체크
     * 
     * @param employeeId 사원번호
     * @return 존재 여부
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * 이메일 중복 체크
     * 
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 부서별 직원 조회
     * 
     * @param department 부서명
     * @return 직원 목록
     */
    List<Employee> findByDepartment(String department);

    /**
     * 이름으로 검색 (대소문자 무시)
     * 
     * @param name 이름
     * @return 직원 목록
     */
    List<Employee> findByNameContainingIgnoreCase(String name);

    /**
     * 가장 마지막에 추가된 직원을 조회 (ID 기준 내림차순)
     * @return 마지막 직원 정보
     */
    Optional<Employee> findTopByOrderByIdDesc();
}