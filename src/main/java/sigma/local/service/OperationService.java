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
import sigma.local.DTO.OperationDTO;
import sigma.local.entity.Operation;
import sigma.local.repository.OperationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OperationService {

    private static final Logger log = LoggerFactory.getLogger(OperationService.class);

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("http://192.168.88.7:8080/api/operations") // Замените на ваш URL
    private String serverApiOperationUrl;

    @Scheduled(fixedRate = 300000, initialDelay = 5000) // Run every 5 minutes after 5 seconds
    @Transactional
    public void synchronizeOperations() {
        try {
            log.info("=== Starting Operation Synchronization (Full Refresh) ===");

            // 1. Get data from the server
            ResponseEntity<OperationDTO[]> response = restTemplate.getForEntity(serverApiOperationUrl, OperationDTO[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to get operations from server. Status: {}", response.getStatusCode());
                return;
            }

            OperationDTO[] operationData = response.getBody();
            log.debug("Received {} operation records from server", operationData.length);

            //List<OperationDTO> operationDTOs = Arrays.asList(operationData);
             List<Operation> operations = Arrays.stream(operationData)
                .filter(Objects::nonNull) // Отфильтруйте null значения
                .map(this::convertToEntity)
                .filter(Objects::nonNull) // Отфильтруйте null значения, которые могли быть возвращены из convertToEntity
                .collect(Collectors.toList());


            // 2. Delete all existing operations
            log.info("Deleting all existing operations from the database...");
            operationRepository.deleteAllInBatch();  // Use deleteAllInBatch for better performance

            // 3. Save the new entities
            log.info("Saving {} new operations to the database...", operations.size());
            operationRepository.saveAll(operations);

            log.info("Successfully synchronized operations.");

        } catch (DataAccessException e) {
            log.error("Database access error during operation synchronization: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error during operation synchronization: {}", e.getMessage(), e);
        }
    }

    private Operation convertToEntity(OperationDTO dto) {
        if (dto == null) {
            log.warn("Received null DTO, skipping conversion.");
            return null; // Или выбросьте исключение, если это недопустимо
        }

        Operation operation = new Operation();
        operation.setTransaction(dto.getTransaction());
        operation.setType(dto.getStagePpp()); // Используйте stagePpp из DTO для type
        operation.setStartWork(dto.getStartWork()); // Установите startWork
        operation.setStopWork(dto.getStopWork());  // Установите stopWork
        operation.setEmployee(dto.getEmployee()); // Установите employee

        return operation;
    }
}