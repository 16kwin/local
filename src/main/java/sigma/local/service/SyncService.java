package sigma.local.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sigma.local.DTO.PlanDto;
import sigma.local.entity.PlanPPP;
import sigma.local.repository.PlanRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final RestTemplate restTemplate;
    private final PlanRepository planRepository;

    @Value("http://192.168.88.7:8080/api/plan") // Inject URL from application.properties
    private String serverApiUrl;

    public SyncService(RestTemplate restTemplate, PlanRepository planRepository) {
        this.restTemplate = restTemplate;
        this.planRepository = planRepository;
        log.info("SyncService initialized!");
    }

    @Scheduled(fixedRate = 18000000, initialDelay = 5000)  // Run every 5 minutes after a 5-second delay
    @Transactional
    public void syncDataFullRefresh() {
        try {
            log.info("=== Starting FULL synchronization ===");

            // 1. Get data from the server
            ResponseEntity<PlanDto[]> response = restTemplate.getForEntity(serverApiUrl, PlanDto[].class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to get data from server. Status: {}", response.getStatusCode());
                // Handle the error appropriately (e.g., retry, notify, etc.)
                return; // Exit if the server request fails
            }

            PlanDto[] data = response.getBody();

            if (data == null || data.length == 0) {
                log.info("No data received from the server. Skipping update.");
                return; // Exit if there's no data to process
            }
            log.debug("Received {} records from the server", data.length);

            // 2. Delete existing records in the local database
            log.info("Deleting all existing records from the local database...");
            planRepository.deleteAllInBatch(); // Use deleteAllInBatch for efficiency

            // 3. Convert DTOs to entities
            List<PlanPPP> entities = Arrays.stream(data)
                    .map(this::convertToLocalEntity)
                    .collect(Collectors.toList());

            // 4. Save all new records in the local database
            log.info("Saving {} new records...", entities.size());
            planRepository.saveAll(entities);

            log.info("=== Full synchronization completed successfully ===");

        } catch (RestClientException e) {
            log.error("Error communicating with the server: {}", e.getMessage(), e);
            //Handle RestClientException (e.g., server down, connection issues)
        } catch (DataAccessException e) {
            log.error("Database access error during synchronization: {}", e.getMessage(), e);
            //Handle DataAccessException (e.g., database connection issues)
        } catch (Exception e) {
            log.error("Unexpected error during synchronization: {}", e.getMessage(), e);
            // Handle any other exceptions
        }
    }

    private PlanPPP convertToLocalEntity(PlanDto dto) {
        PlanPPP entity = new PlanPPP();
        entity.setTransaction_local(dto.getTransaction());
        entity.setStatus_local(dto.getStatus());
        entity.setPlanPpp_local(dto.getPlanPpp());
        entity.setPlanDateStart_local(dto.getPlanDateStart());
        entity.setForecastDateStart_local(dto.getForecastDateStart());
        entity.setFactDateStart_local(dto.getFactDateStart());
        entity.setPlanDateStop_local(dto.getPlanDateStop());
        entity.setForecastDateStop_local(dto.getForecastDateStop());
        entity.setFactDateStop_local(dto.getFactDateStop());
        entity.setPlanDateShipment_local(dto.getPlanDateShipment());
        entity.setForecastDateShipment_local(dto.getForecastDateShipment());
        entity.setFactDateShipment_local(dto.getFactDateShipment());
        return entity;
    }
}