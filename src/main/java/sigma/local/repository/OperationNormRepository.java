package sigma.local.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import sigma.local.entity.OperationNorm;

public interface OperationNormRepository extends JpaRepository<OperationNorm, Long> {
    
    boolean existsByWorkPppAndOperationNorm(String workPpp, BigDecimal operationNorm);
}