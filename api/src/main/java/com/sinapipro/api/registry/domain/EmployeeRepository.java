package com.sinapipro.api.registry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Page<Employee> findByActiveTrue(Pageable pageable);
    Page<Employee> findByActiveTrueAndType(String type, Pageable pageable);
    Page<Employee> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
}
