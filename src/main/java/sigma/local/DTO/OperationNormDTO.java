package sigma.local.DTO;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OperationNormDTO {

    @JsonProperty("id")
    private IdDTO id;
    @JsonProperty("machineType")
        private String machineType;

        @JsonProperty("workPpp")
        private String workPpp;

        @JsonProperty("specialty")
        private String specialty;

        @JsonProperty("operationNorm")
        private BigDecimal operationNorm;

        @JsonProperty("operationOptionPpp")
        private String operationOptionPpp;
    @Data
    public static class IdDTO {
        @JsonProperty("machineType")
        private String machineType;

        @JsonProperty("workPpp")
        private String workPpp;

        @JsonProperty("specialty")
        private String specialty;

        @JsonProperty("operationNorm")
        private BigDecimal operationNorm;

        @JsonProperty("operationOptionPpp")
        private String operationOptionPpp;
    }
}