package com.staffSync.application;

import com.staffSync.application.employee.EmployeeService;
import com.staffSync.application.employee.dto.CreateEmployeeRequest;
import com.staffSync.application.employee.dto.EmployeeDto;
import com.staffSync.application.employee.dto.UpdateEmployeeRequest;
import com.staffSync.domain.employee.Employee;
import com.staffSync.domain.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;

    @BeforeEach
    void setUp() {
        employee1 = createTestEmployee(1L, "EMP001", "홍길동", "hong@example.com", LocalDate.of(2020, 1, 1),
                50000000.0, "개발팀");
        employee2 = createTestEmployee(2L, "EMP002", "김철수", "kim@example.com", LocalDate.of(2021, 3, 15),
                60000000.0, "디자인팀");

        createRequest = new CreateEmployeeRequest(
                "이영희", "lee@example.com", LocalDate.of(2022, 5, 1), 45000000.0, "기획팀");

        updateRequest = new UpdateEmployeeRequest(
                "이영희2", "lee2@example.com", 55000000.0, "마케팅팀");
    }

    // Helper method to create an Employee with a specific ID for testing purposes
    private Employee createTestEmployee(Long id, String employeeId, String name, String email, LocalDate hireDate,
            Double salary, String department) {
        Employee employee = new Employee(employeeId, name, email, hireDate, salary, department);
        try {
            // Use reflection to set the ID, as it's not exposed by constructor or setter
            java.lang.reflect.Field field = employee.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(employee, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set employee ID via reflection for testing", e);
        }
        return employee;
    }

    @Test
    @DisplayName("모든 직원을 조회한다")
    void getAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("홍길동");
        assertThat(result.get(1).getName()).isEqualTo("김철수");
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 직원을 조회한다 - 성공")
    void getEmployeeById_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        EmployeeDto result = employeeService.getEmployeeById(1L);

        assertThat(result.getName()).isEqualTo("홍길동");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID로 직원을 조회한다 - 실패 (직원 없음)")
    void getEmployeeById_notFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(99L));
        verify(employeeRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("사원번호로 직원을 조회한다 - 성공")
    void getEmployeeByEmployeeId_success() {
        when(employeeRepository.findByEmployeeId("EMP001")).thenReturn(Optional.of(employee1));

        EmployeeDto result = employeeService.getEmployeeByEmployeeId("EMP001");

        assertThat(result.getName()).isEqualTo("홍길동");
        verify(employeeRepository, times(1)).findByEmployeeId("EMP001");
    }

    @Test
    @DisplayName("사원번호로 직원을 조회한다 - 실패 (직원 없음)")
    void getEmployeeByEmployeeId_notFound() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeByEmployeeId("E999"));
        verify(employeeRepository, times(1)).findByEmployeeId("E999");
    }

    @Test
    @DisplayName("부서별 직원을 조회한다")
    void getEmployeesByDepartment() {
        when(employeeRepository.findByDepartment("개발팀")).thenReturn(List.of(employee1));

        List<EmployeeDto> result = employeeService.getEmployeesByDepartment("개발팀");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("홍길동");
        verify(employeeRepository, times(1)).findByDepartment("개발팀");
    }

    @Test
    @DisplayName("이름으로 직원을 검색한다")
    void searchEmployeesByName() {
        when(employeeRepository.findByNameContainingIgnoreCase("홍")).thenReturn(List.of(employee1));

        List<EmployeeDto> result = employeeService.searchEmployeesByName("홍");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("홍길동");
        verify(employeeRepository, times(1)).findByNameContainingIgnoreCase("홍");
    }

    @Test
    @DisplayName("새로운 직원을 등록한다 (첫 직원) - 성공")
    void createEmployee_firstEmployee_success() {
        // given
        when(employeeRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        // when save is called, return the same employee object passed to it.
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        EmployeeDto result = employeeService.createEmployee(createRequest);

        // then
        assertThat(result.getName()).isEqualTo(createRequest.getName());
        assertThat(result.getEmployeeId()).isEqualTo("EMP001");
        verify(employeeRepository, times(1)).findTopByOrderByIdDesc();
        verify(employeeRepository, times(1)).existsByEmail(createRequest.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("새로운 직원을 등록한다 (기존 직원 존재) - 성공")
    void createEmployee_subsequentEmployee_success() {
        // given
        when(employeeRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(employee1)); // employee1 has EMP001
        when(employeeRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        EmployeeDto result = employeeService.createEmployee(createRequest);

        // then
        assertThat(result.getName()).isEqualTo(createRequest.getName());
        assertThat(result.getEmployeeId()).isEqualTo("EMP002");
        verify(employeeRepository, times(1)).findTopByOrderByIdDesc();
        verify(employeeRepository, times(1)).existsByEmail(createRequest.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("새로운 직원을 등록한다 - 실패 (이메일 중복)")
    void createEmployee_duplicateEmail() {
        when(employeeRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(createRequest));

        verify(employeeRepository, times(1)).existsByEmail(createRequest.getEmail());
        verify(employeeRepository, never()).findTopByOrderByIdDesc();
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("직원 정보를 수정한다 - 성공")
    void updateEmployee_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        // Ensure email check only happens if email is actually changing
        when(employeeRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);

        EmployeeDto result = employeeService.updateEmployee(1L, updateRequest);

        assertThat(result.getName()).isEqualTo(updateRequest.getName());
        assertThat(result.getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(result.getSalary()).isEqualTo(updateRequest.getSalary());
        assertThat(result.getDepartment()).isEqualTo(updateRequest.getDepartment());
        verify(employeeRepository, times(1)).findById(1L);
        // Only verify existsByEmail if email is changing AND it's not the employee's
        // original email
        verify(employeeRepository, times(1)).existsByEmail(updateRequest.getEmail());
    }

    @Test
    @DisplayName("직원 정보를 수정한다 - 실패 (직원 없음)")
    void updateEmployee_notFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(99L, updateRequest));
        verify(employeeRepository, times(1)).findById(99L);
        verify(employeeRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("직원 정보를 수정한다 - 실패 (이메일 중복)")
    void updateEmployee_duplicateEmail() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1L, updateRequest));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).existsByEmail(updateRequest.getEmail());
    }

    @Test
    @DisplayName("직원을 삭제한다 - 성공")
    void deleteEmployee_success() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("직원을 삭제한다 - 실패 (직원 없음)")
    void deleteEmployee_notFound() {
        when(employeeRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> employeeService.deleteEmployee(99L));
        verify(employeeRepository, times(1)).existsById(99L);
        verify(employeeRepository, never()).deleteById(anyLong());
    }
}
