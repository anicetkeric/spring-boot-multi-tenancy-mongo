package com.tutorial.springbootmultitenancymongo.service;


import com.tutorial.springbootmultitenancymongo.domain.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAll();

    Employee save(Employee employee);

    Optional<Employee> getById(String id);

    void deleteById(String id);
}