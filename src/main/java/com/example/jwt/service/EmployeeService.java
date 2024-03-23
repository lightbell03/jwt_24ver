package com.example.jwt.service;

import com.example.jwt.entity.Employee;
import com.example.jwt.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Page<Employee> getEmployeesByPaging(Pageable pageable) {
        return employeeRepository.findEmployeesPaging(pageable);
    }
}
