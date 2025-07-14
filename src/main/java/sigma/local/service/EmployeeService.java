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
import sigma.local.DTO.EmployeeDTO;
import sigma.local.entity.Employee;
import sigma.local.repository.EmployeeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("http://192.168.88.7:8080/api/employees")
    private String serverApiEmployeeUrl;

    @Scheduled(fixedRate = 300000, initialDelay = 5000) // Run every 5 minutes after 5 seconds
    @Transactional
    public void syncEmployees() {
        try {
            log.info("=== Starting Employee Synchronization (Full Refresh) ===");

            // 1. Delete all existing records
            log.info("Deleting all existing records from the local database...");
            employeeRepository.deleteAllInBatch(); // Use deleteAllInBatch for efficiency

            // 2. Get data from the server
            ResponseEntity<EmployeeDTO[]> response = restTemplate.getForEntity(serverApiEmployeeUrl, EmployeeDTO[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to get employees from server. Status: {}", response.getStatusCode());
                return;
            }

            EmployeeDTO[] employeeData = response.getBody();
            log.debug("Received {} employee records from server", employeeData.length);

            // 3. Convert DTOs to entities
            List<Employee> employeesToSave = Arrays.stream(employeeData)
                .map(this::convertToLocalEntity)
                .collect(Collectors.toList());

            // 4. Save all new records
            log.info("Saving {} new records...", employeesToSave.size());
            employeeRepository.saveAll(employeesToSave); // Save all at once
            log.info("Successfully synchronized {} employee records", employeesToSave.size());

        } catch (DataAccessException e) {
            log.error("Database access error during employee synchronization: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error during employee synchronization: {}", e.getMessage(), e);
        }
    }

    private Employee convertToLocalEntity(EmployeeDTO dto) {
        Employee employee = new Employee();

        String specializationValue = (dto.getSpecialty() != null && !dto.getSpecialty().isEmpty()) ?
            dto.getSpecialty() : dto.getSpecialization();

        employee.setEmployeeName(dto.getEmployeeName());
        employee.setSpecialization(specializationValue);

        return employee;
    }
}