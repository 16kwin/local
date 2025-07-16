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
import sigma.local.DTO.ProblemsDTO;
import sigma.local.entity.Problems;
import sigma.local.repository.ProblemsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProblemsService {

    private static final Logger log = LoggerFactory.getLogger(ProblemsService.class);

    @Autowired
    private ProblemsRepository problemsRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("http://83.219.12.178:8080/api/problems") // Change to the problems URL
    private String serverApiProblemsUrl;

    @Scheduled(fixedRate = 18000000, initialDelay = 5000) // Run every 5 minutes after 5 seconds
    @Transactional
    public void synchronizeProblems() {
        try {
            log.info("=== Starting Problems Synchronization (Full Refresh) ===");

            // 1. Get data from the server
            ResponseEntity<ProblemsDTO[]> response = restTemplate.getForEntity(serverApiProblemsUrl, ProblemsDTO[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to get problems from server. Status: {}", response.getStatusCode());
                return;
            }

            ProblemsDTO[] problemsData = response.getBody();
            log.debug("Received {} problem records from server", problemsData.length);

            List<Problems> problems = Arrays.stream(problemsData)
                .filter(Objects::nonNull) // Filter null values
                .map(this::convertToEntity)
                .filter(Objects::nonNull) // Filter null values which might be returned from convertToEntity
                .collect(Collectors.toList());

            // 2. Delete all existing problems
            log.info("Deleting all existing problems from the database...");
            problemsRepository.deleteAllInBatch(); // Use deleteAllInBatch for better performance

            // 3. Save the new entities
            log.info("Saving {} new problems to the database...", problems.size());
            problemsRepository.saveAll(problems);

            log.info("Successfully synchronized problems.");

        } catch (DataAccessException e) {
            log.error("Database access error during problem synchronization: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error during problem synchronization: {}", e.getMessage(), e);
        }
    }

    private Problems convertToEntity(ProblemsDTO dto) {
        if (dto == null) {
            log.warn("Received null DTO, skipping conversion.");
            return null; // Or throw an exception if that is not acceptable
        }

        Problems problems = new Problems();
        problems.setTransaction(dto.getTransaction());
        problems.setType(dto.getUnit());
        problems.setDescription(dto.getDescription());
        problems.setHours(dto.getNorm_horse());
        problems.setEmployee(dto.getEmployees());


        return problems;
    }
}