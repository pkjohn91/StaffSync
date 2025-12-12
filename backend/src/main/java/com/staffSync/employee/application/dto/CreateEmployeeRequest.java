package com.staffSync.employee.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "직급은 필수입니다.")
    private String position;

    @NotBlank(message = "부서는 필수입니다.")
    private String department;

    @NotNull(message = "급여는 필수입니다.")
    @Positive(message = "급여는 0보다 커야 합니다.")
    private Double salary;
}
