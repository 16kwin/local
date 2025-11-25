package sigma.local.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationNewDTO {

    @JsonProperty("id")
    private IdDTO id;

    @JsonProperty("transaction")
    private String transaction;

    @JsonProperty("workPpp")
    private String workPpp;

    @JsonProperty("start")
    private LocalDateTime start;

    @JsonProperty("stagePpp")
    private String stagePpp;

    @JsonProperty("statusWorkPpp")
    private String statusWorkPpp;

    @JsonProperty("stop")
    private LocalDateTime stop;

    @JsonProperty("employees")
    private String employees;

    @JsonProperty("statusPpp")
    private String statusPpp;

    @Data
    public static class IdDTO {
        @JsonProperty("transaction")
        private String transaction;

        @JsonProperty("workPpp")
        private String workPpp;

        @JsonProperty("start")
        private LocalDateTime start;
    }
}