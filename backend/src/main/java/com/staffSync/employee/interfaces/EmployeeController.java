package com.staffSync.employee.interfaces;

import com.staffSync.employee.application.EmployeeService;
import com.staffSync.employee.application.dto.CreateEmployeeRequest;
import com.staffSync.employee.application.dto.EmployeeDto;
import com.staffSync.employee.application.dto.UpdateEmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 전체 직원 목록 조회
     * GET /api/employees
     * 
     * @return 직원 목록
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     * 특정 직원 조회 (ID)
     * GET /api/employees/{id}
     * 
     * @param id 직원 ID
     * @return 직원 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * 사원번호로 직원 조회
     * GET /api/employees/employee-id/{employeeId}
     * 
     * @param employeeId 사원번호
     * @return 직원 정보
     */
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmployeeId(employeeId));
    }

    /**
     * 부서별 직원 조회
     * GET /api/employees/department/{department}
     * 
     * @param department 부서명
     * @return 직원 목록
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department));
    }

    /**
     * 이름으로 직원 검색
     * GET /api/employees/search?name=검색어
     * 
     * @param name 검색할 이름
     * @return 직원 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(@RequestParam String name) {
        return ResponseEntity.ok(employeeService.searchEmployeesByName(name));
    }

    /**
     * 직원 등록
     * POST /api/employees
     * 
     * @param request 직원 등록 정보
     * @return 등록된 직원 정보
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    /**
     * 직원 정보 수정
     * PUT /api/employees/{id}
     * 
     * @param id      직원 ID
     * @param request 수정할 정보
     * @return 수정된 직원 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    /**
     * 직원 삭제
     * DELETE /api/employees/{id}
     * 
     * @param id 직원 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}