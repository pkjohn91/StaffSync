package com.staffSync.employee.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staffSync.employee.application.dto.CreateEmployeeRequest;
import com.staffSync.employee.application.dto.EmployeeDto;
import com.staffSync.employee.domain.Employee;
import com.staffSync.employee.domain.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // 전체 직원 조회
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }

    // 부서별 직원 조회
    public List<EmployeeDto> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }

    // 직원 등록
    @Transactional
    public EmployeeDto createEmployee(CreateEmployeeRequest request) {
        Employee employee = new Employee(
                request.getName(),
                request.getPosition(),
                request.getDepartment(),
                request.getSalary());

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeDto.from(savedEmployee);
    }

    // 직원 삭제
    @Transactional
    public void deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("직원을 찾을 수 없습니다.");
        }
        employeeRepository.deleteById(employeeId);
    }

    // 급여 업데이트
    @Transactional
    public EmployeeDto updateSalary(Long employeeId, Double newSalary) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

        employee.updateSalary(newSalary);
        return EmployeeDto.from(employee);
    }
}
