package sigma.local.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Используйте IDENTITY для автоинкрементного поля
    @Column(name = "id")
    private Long employeesId;

    @Column(name = "name")
    private String employeeName;

    @Column(name = "specialization")
    private String specialization;
}