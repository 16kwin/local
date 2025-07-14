package sigma.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sigma.local.entity.Operation;

public interface OperationRepository  extends JpaRepository<Operation, Long>{

}
