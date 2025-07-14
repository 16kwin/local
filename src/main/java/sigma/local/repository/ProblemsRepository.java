package sigma.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigma.local.entity.Problems;

@Repository
public interface ProblemsRepository extends JpaRepository<Problems, Long> {
}