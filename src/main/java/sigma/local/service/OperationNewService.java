package sigma.local.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sigma.local.DTO.OperationNewDTO;
import sigma.local.entity.OperationNew;
import sigma.local.repository.OperationNewRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OperationNewService {

    private static final Logger log = LoggerFactory.getLogger(OperationNewService.class);

    @Autowired
    private OperationNewRepository operationNewRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("http://83.219.12.178:8080/api/operations_new")
    private String serverApiOperationNewUrl;

    @Scheduled(fixedRate = 3000000, initialDelay = 5000)
    @Transactional
    public void synchronizeOperationsNew() {
        try {
            log.info("=== Starting OperationNew Synchronization ===");

            ResponseEntity<OperationNewDTO[]> response = restTemplate.getForEntity(serverApiOperationNewUrl, OperationNewDTO[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to get operations new from server. Status: {}", response.getStatusCode());
                return;
            }

            OperationNewDTO[] operationNewData = response.getBody(); 
            log.debug("Received {} operation new records from server", operationNewData.length);

            List<OperationNew> operationsNew = Arrays.stream(operationNewData)
                .filter(Objects::nonNull)
                .map(this::convertToEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            log.info("Deleting all existing operations new from the database...");
            operationNewRepository.deleteAllInBatch();

            log.info("Saving {} new operations new to the database...", operationsNew.size());
            operationNewRepository.saveAll(operationsNew);

            log.info("Successfully synchronized operations new.");

        } catch (DataAccessException e) {
            log.error("Database access error during operation new synchronization: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error during operation new synchronization: {}", e.getMessage(), e);
        }
    }

    private OperationNew convertToEntity(OperationNewDTO dto) {
        if (dto == null || dto.getId() == null) {
            log.warn("Received null DTO or null ID, skipping conversion.");
            return null;
        }

        OperationNew operationNew = new OperationNew();
        
        // ИСПРАВЛЕНИЕ: данные берутся из dto.getId()!
        operationNew.setTransaction(dto.getId().getTransaction());
        operationNew.setWorkPpp(dto.getId().getWorkPpp());
        operationNew.setStart(dto.getId().getStart());
        
        operationNew.setStagePpp(dto.getStagePpp());
        operationNew.setStatusWorkPpp(dto.getStatusWorkPpp());
        operationNew.setStop(dto.getStop());
        operationNew.setEmployees(dto.getEmployees());
        operationNew.setStatusPpp(dto.getStatusPpp());

        log.debug("Converted DTO to Entity: transaction={}, workPpp={}, start={}", 
            dto.getId().getTransaction(), dto.getId().getWorkPpp(), dto.getId().getStart());
        
        return operationNew;
    }
}