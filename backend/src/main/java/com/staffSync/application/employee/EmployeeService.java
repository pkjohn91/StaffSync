package com.staffSync.application.employee;

import com.staffSync.application.employee.dto.CreateEmployeeRequest;
import com.staffSync.application.employee.dto.EmployeeDto;
import com.staffSync.application.employee.dto.UpdateEmployeeRequest;
import com.staffSync.domain.employee.Employee;
import com.staffSync.domain.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * 전체 직원 목록을 조회합니다.
     * 
     * @return 직원 목록
     */
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 직원의 정보를 조회합니다.
     * 
     * @param id 직원 ID
     * @return 직원 정보
     */
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        return EmployeeDto.from(employee);
    }

    /**
     * 사원번호로 직원을 조회합니다.
     * 
     * @param employeeId 사원번호
     * @return 직원 정보
     */
    public EmployeeDto getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        return EmployeeDto.from(employee);
    }

    /**
     * 부서별 직원을 조회합니다.
     * 
     * @param department 부서명
     * @return 직원 목록
     */
    public List<EmployeeDto> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 이름으로 직원을 검색합니다.
     * 
     * @param name 검색할 이름
     * @return 직원 목록
     */
    public List<EmployeeDto> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 직원을 등록합니다.
     * 
     * @param request 직원 등록 정보
     * @return 등록된 직원 정보
     */
    @Transactional
    public EmployeeDto createEmployee(CreateEmployeeRequest request) {
        // 이메일 중복 검사
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 새로운 사원번호 생성
        String nextEmployeeId = employeeRepository.findTopByOrderByIdDesc()
                .map(lastEmployee -> {
                    String lastId = lastEmployee.getEmployeeId();
                    int number = Integer.parseInt(lastId.substring(3));
                    return String.format("EMP%03d", number + 1);
                })
                .orElse("EMP001");

        Employee employee = new Employee(
                nextEmployeeId,
                request.getName(),
                request.getEmail(),
                request.getHireDate(),
                request.getSalary(),
                request.getDepartment());

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeDto.from(savedEmployee);
    }

    /**
     * 직원 정보를 수정합니다.
     * 
     * @param id      직원 ID
     * @param request 수정할 정보
     * @return 수정된 직원 정보
     */
    @Transactional
    public EmployeeDto updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

        // 이메일 중복 체크 (본인 이메일이 아닌 경우)
        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        }

        // 도메인 객체의 비즈니스 로직 호출
        employee.update(
                request.getName(),
                request.getEmail(),
                request.getSalary(),
                request.getDepartment());

        return EmployeeDto.from(employee);
    }

    /**
     * 직원을 삭제합니다.
     * 
     * @param id 직원 ID
     */
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("직원을 찾을 수 없습니다.");
        }
        employeeRepository.deleteById(id);
    }
}