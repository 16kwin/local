package sigma.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sigma.local.entity.Employee;

public interface EmployeeRepository  extends JpaRepository<Employee, Long>{

}
