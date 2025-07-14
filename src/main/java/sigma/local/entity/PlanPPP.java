package sigma.local.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="ppp")
public class PlanPPP {
    @Id
    @Column(name="transaction")
    private String transaction_local;
    
    @Column(name = "status")
    private String status_local;

    @Column(name = "plan_ppp")
    private Integer planPpp_local;

    @Column(name = "plan_date_start")
    private LocalDate planDateStart_local;

    @Column(name = "forecast_date_start")
    private LocalDate forecastDateStart_local;

    @Column(name = "fact_date_start")
    private LocalDate factDateStart_local;

    @Column(name = "plan_date_stop")
    private LocalDate planDateStop_local;

    @Column(name = "forecast_date_stop")
    private LocalDate forecastDateStop_local;

    @Column(name = "fact_date_stop")
    private LocalDate factDateStop_local;

    @Column(name = "plan_date_shipment")
    private LocalDate planDateShipment_local;

    @Column(name = "forecast_date_shipment")
    private LocalDate forecastDateShipment_local;

    @Column(name = "fact_date_shipment")
    private LocalDate factDateShipment_local;
}
