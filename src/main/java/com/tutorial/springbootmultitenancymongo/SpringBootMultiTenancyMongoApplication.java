package com.tutorial.springbootmultitenancymongo;

import com.tutorial.springbootmultitenancymongo.domain.Employee;
import com.tutorial.springbootmultitenancymongo.filter.TenantContext;
import com.tutorial.springbootmultitenancymongo.repository.EmployeeRepository;
import com.tutorial.springbootmultitenancymongo.service.RedisDatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SpringBootMultiTenancyMongoApplication implements CommandLineRunner {

    @Autowired
    private RedisDatasourceService redisDatasourceService;
    @Autowired
    private EmployeeRepository employeeRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMultiTenancyMongoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> aliasList = redisDatasourceService.getTenantsAlias();
        if (!aliasList.isEmpty()) {
            //perform actions for each tenant
            aliasList.forEach(alias -> {
                TenantContext.setTenantId(alias);
                employeeRepository.deleteAll();

                Employee employee = Employee.builder()
                        .firstName(alias)
                        .lastName(alias)
                        .email(String.format("%s%s", alias, "@localhost.com" ))
                        .build();
                employeeRepository.save(employee);

                TenantContext.clear();
            });
        }

    }
}
