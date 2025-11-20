package sigma.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigma.local.entity.OperationNew;

public interface OperationNewRepository extends JpaRepository<OperationNew, Long> {
}