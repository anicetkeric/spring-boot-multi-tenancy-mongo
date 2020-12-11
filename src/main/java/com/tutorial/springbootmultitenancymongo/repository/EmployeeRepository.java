package com.tutorial.springbootmultitenancymongo.repository;

import com.tutorial.springbootmultitenancymongo.domain.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
