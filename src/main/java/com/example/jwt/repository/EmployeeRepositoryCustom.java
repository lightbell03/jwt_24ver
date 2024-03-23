package com.example.jwt.repository;

import com.example.jwt.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmployeeRepositoryCustom {

    Page<Employee> findEmployeesPaging(Pageable pageable);
}
