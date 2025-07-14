package sigma.local.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlanDto {
    @JsonProperty("transaction")
    private String transaction;
    @JsonProperty("availability")
    private String availability;
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("planPpp")
    private Integer planPpp;
    
    @JsonProperty("planDateStart")
    private LocalDate planDateStart;
    
    @JsonProperty("forecastDateStart")
    private LocalDate forecastDateStart;
    
    @JsonProperty("factDateStart")
    private LocalDate factDateStart;
    
    // Остальные даты
    @JsonProperty("planDateStop")
    private LocalDate planDateStop;
    
    @JsonProperty("forecastDateStop")
    private LocalDate forecastDateStop;
    
    @JsonProperty("factDateStop")
    private LocalDate factDateStop;
    
    @JsonProperty("planDateShipment")
    private LocalDate planDateShipment;
    
    @JsonProperty("forecastDateShipment")
    private LocalDate forecastDateShipment;
    
    @JsonProperty("factDateShipment")
    private LocalDate factDateShipment;
}
