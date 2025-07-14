package sigma.local.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProblemsDTO {

    @JsonProperty("id")
    private IdDTO id;

    @JsonProperty("transaction")
    private String transaction;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("description")
    private String description;

    @JsonProperty("norm_horse")
    private Double norm_horse;

    @JsonProperty("employees")
    private String employees;

    @Data
    public static class IdDTO {
        @JsonProperty("transaction")
        private String transaction;

        @JsonProperty("unit")
        private String unit;

        @JsonProperty("description")
        private String description;

        @JsonProperty("norm_horse")
        private Double norm_horse;

        @JsonProperty("employees")
        private String employees;
    }
}