package com.example.jwt.controller;

import com.example.jwt.entity.Employee;
import com.example.jwt.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Page<Employee>> getEmployeesWithPage(Pageable pageable) {
        log.info("page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Employee> employees = employeeService.getEmployeesByPaging(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(employees);
    }
}
