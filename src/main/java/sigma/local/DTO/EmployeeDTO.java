package sigma.local.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeDTO {
    @JsonProperty("employeeName")
    private String employeeName;
    @JsonProperty("specialization")
    private String specialization;
    @JsonProperty("specialty")
    private String specialty;
    
}
