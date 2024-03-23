package com.example.jwt.repository;

import com.example.jwt.entity.Employee;
import com.example.jwt.entity.QEmployee;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {
    private final JPAQueryFactory query;

    @Override
    public Page<Employee> findEmployeesPaging(Pageable pageable) {
        List<Employee> employees = query
                .select(QEmployee.employee)
                .from(QEmployee.employee)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(employees, pageable, employees.size());
    }
}
