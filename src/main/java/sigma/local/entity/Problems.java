package sigma.local.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="problems")
public class Problems{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id")
    private Long problemsId;

    @Column(name = "transaction")
    private String transaction;

    @Column(name = "type")
    private String type;
    @Column(name = "description")
    private String description;
    
    @Column(name = "norm_hours")
    private Double hours; 

     @Column(name = "employee")
    private String employee;
}