package sigma.local.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "operation_norms")
public class OperationNorm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "machine_type", nullable = true)
    private String machineType;

    @Column(name = "work_ppp", nullable = true)
    private String workPpp;

    @Column(name = "specialty", nullable = true)
    private String specialty;

    @Column(name = "operation_norm", nullable = true)
    private BigDecimal operationNorm;

    @Column(name = "operation_option_ppp", nullable = true)
    private String operationOptionPpp;
}