package sigma.local.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "operation_new")
public class OperationNew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction")
    private String transaction;

    @Column(name = "work_ppp")
    private String workPpp;

    @Column(name = "start")
    private Timestamp start;

    @Column(name = "stage_ppp")
    private String stagePpp;

    @Column(name = "status_work_ppp")
    private String statusWorkPpp;

    @Column(name = "stop")
    private Timestamp stop;

    @Column(name = "employees")
    private String employees;

    @Column(name = "status_ppp")
    private String statusPpp;
}