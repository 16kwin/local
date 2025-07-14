package sigma.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sigma.local.entity.PlanPPP;

public interface PlanRepository  extends JpaRepository<PlanPPP, String>{
    
}
