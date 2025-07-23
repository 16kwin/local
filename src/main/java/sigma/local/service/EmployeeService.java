package sigma.local.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    // URL остаётся внутри файла (как было)
    private final String serverApiEmployeeUrl = "http://83.219.12.178:8080/api/employees";

    // Изменили интервал на 6 часов (21600000 мс)
    @Scheduled(fixedRate = 21600000, initialDelay = 5000)
    @Transactional
    public void syncEmployees() {
        try {
            log.info("=== Starting Employee Synchronization (Только 'Полное ППП') ===");

            // 1. Удаляем старые записи
            employeeRepository.deleteAllInBatch();

            // 2. Получаем данные с сервера
            ResponseEntity<EmployeeDTO[]> response = restTemplate.getForEntity(
                serverApiEmployeeUrl, 
                EmployeeDTO[].class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Ошибка при запросе к API. Статус: {}", response.getStatusCode());
                return;
            }

            // 3. Фильтруем и конвертируем:
            //    - Берём только с specialization = "Полное ППП"
            //    - Специальность берём из поля specialty
            List<Employee> employeesToSave = Arrays.stream(response.getBody())
                .filter(dto -> "Подготовка к ППП".equals(dto.getSpecialization()))
                .map(dto -> {
                    Employee emp = new Employee();
                    emp.setEmployeeName(dto.getEmployeeName());
                    emp.setSpecialization(dto.getSpecialty()); // Берём только из specialty!
                    return emp;
                })
                .collect(Collectors.toList());

            // 4. Сохраняем
            employeeRepository.saveAll(employeesToSave);
            log.info("Добавлено {} сотрудников с 'Полное ППП'.", employeesToSave.size());

        } catch (DataAccessException e) {
            log.error("Ошибка БД: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Ошибка синхронизации: {}", e.getMessage(), e);
        }
    }
}