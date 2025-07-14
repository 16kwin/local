package sigma.local.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class OperationDTO {

    @JsonProperty("id")
    private IdDTO id; // Вложенный объект id

    @JsonProperty("stagePpp")
    private String stagePpp;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("transaction")
    private String transaction;

    @JsonProperty("startWork")
    private Timestamp startWork;

    @JsonProperty("stopWork")
    private Timestamp stopWork;

    @Data
    public static class IdDTO {
        @JsonProperty("stage_ppp")
        private String stagePpp;

        @JsonProperty("employee")
        private String employee;

        @JsonProperty("transaction")
        private String transaction;

        @JsonProperty("start_work")
        private Timestamp startWork;

        @JsonProperty("stop_work")
        private Timestamp stopWork;
    }
}