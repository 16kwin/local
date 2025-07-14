package sigma.local.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="operation")
public class Operation{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id")
    private Long operationId;

    @Column(name = "transaction")
    private String transaction;

    @Column(name = "type")
    private String type;
    
    @Column(name = "start")
    private Timestamp startWork; 

    @Column(name = "stop")
    private Timestamp stopWork;
     @Column(name = "employee")
    private String employee;
}