package com.staffSync.infrastructure.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.staffSync.domain.employee.Employee;
import com.staffSync.domain.employee.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeDataInitializer implements CommandLineRunner {

        private final EmployeeRepository employeeRepository;

        @Override
        public void run(String... args) {
                // 이미 데이터가 있으면 스킵
                if (employeeRepository.count() > 0) {
                        return;
                }

                // 샘플 데이터 추가
                if (employeeRepository.count() == 0) {
                        employeeRepository.save(new Employee(
                                        "EMP001", "김철수", "kim@staffsync.com",
                                        LocalDate.of(2020, 1, 15), 50000000.0, "개발팀"));
                        employeeRepository.save(new Employee(
                                        "EMP002", "이영희", "lee@staffsync.com",
                                        LocalDate.of(2021, 3, 20), 45000000.0, "디자인팀"));
                        employeeRepository.save(new Employee(
                                        "EMP003", "박민수", "park@staffsync.com",
                                        LocalDate.of(2019, 7, 10), 60000000.0, "개발팀"));
                        employeeRepository.save(new Employee(
                                        "EMP004", "최지은", "choi@staffsync.com",
                                        LocalDate.of(2022, 5, 1), 40000000.0, "마케팅팀"));
                        employeeRepository.save(new Employee(
                                        "EMP005", "정대호", "jung@staffsync.com",
                                        LocalDate.of(2018, 11, 30), 70000000.0, "인사팀"));
                }
        }
}
