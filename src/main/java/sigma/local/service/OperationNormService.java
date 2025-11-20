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
import sigma.local.DTO.OperationNormDTO;
import sigma.local.entity.OperationNorm;
import sigma.local.repository.OperationNormRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OperationNormService {

    private static final Logger log = LoggerFactory.getLogger(OperationNormService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OperationNormRepository operationNormRepository;

    private final String serverApiOperationNormUrl = "http://83.219.12.178:8080/api/operationsnorm";

    @Scheduled(fixedRate = 3000000, initialDelay = 10000)
    @Transactional
    public void syncOperationNorms() {
        try {
            log.info("=== Starting OperationNorm Synchronization ===");

            // 1. Получаем данные с сервера
            ResponseEntity<OperationNormDTO[]> response = restTemplate.getForEntity(
                serverApiOperationNormUrl, 
                OperationNormDTO[].class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Ошибка при запросе к API. Статус: {}", response.getStatusCode());
                return; 
            }

            // 2. Конвертируем и фильтруем данные
            List<OperationNorm> normsToSave = Arrays.stream(response.getBody())
                .filter(Objects::nonNull)
                .map(this::convertToEntity)
                .filter(Objects::nonNull) // Фильтруем null после конвертации
                .filter(this::isNotShabrenie) // Фильтруем "Шабрение"
                .collect(Collectors.toList());

            // 3. Полная очистка и сохранение новых данных
            log.info("Удаляем существующие записи...");
            operationNormRepository.deleteAllInBatch();

            log.info("Сохраняем {} отфильтрованных записей...", normsToSave.size());
            operationNormRepository.saveAll(normsToSave);

            // 4. Удаляем дубликаты по бизнес-ключу (игнорируя ID и machine_type)
            removeDuplicatesByBusinessKey();

            log.info("Успешно синхронизировано OperationNorm записей: {}", normsToSave.size());

        } catch (DataAccessException e) {
            log.error("Ошибка БД: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Ошибка синхронизации: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void removeDuplicatesByBusinessKey() {
        try {
            log.info("=== Starting duplicate removal by business key ===");
            
            List<OperationNorm> allNorms = operationNormRepository.findAll();
            Map<String, OperationNorm> uniqueNorms = new LinkedHashMap<>();
            List<OperationNorm> duplicatesToRemove = new ArrayList<>();
            
            for (OperationNorm norm : allNorms) {
                // Бизнес-ключ: workPpp + specialty + operationNorm + operationOptionPpp
                String businessKey = norm.getWorkPpp() + "|" + 
                                   norm.getSpecialty() + "|" + 
                                   norm.getOperationNorm() + "|" + 
                                   norm.getOperationOptionPpp();
                
                if (uniqueNorms.containsKey(businessKey)) {
                    // Найден дубликат - добавляем в список на удаление
                    duplicatesToRemove.add(norm);
                    log.debug("Found duplicate: businessKey={}, id={}", businessKey, norm.getId());
                } else {
                    // Первое вхождение - сохраняем
                    uniqueNorms.put(businessKey, norm);
                }
            }
            
            // Удаляем дубликаты
            if (!duplicatesToRemove.isEmpty()) {
                log.info("Found {} duplicates by business key, removing...", duplicatesToRemove.size());
                operationNormRepository.deleteAll(duplicatesToRemove);
                log.info("Successfully removed {} duplicates", duplicatesToRemove.size());
            } else {
                log.info("No duplicates found by business key");
            }
            
        } catch (Exception e) {
            log.error("Error during duplicate removal by business key: {}", e.getMessage(), e);
        }
    }

    private OperationNorm convertToEntity(OperationNormDTO dto) {
        if (dto == null) {
            return null;
        }

        OperationNorm norm = new OperationNorm();
        norm.setMachineType(dto.getMachineType());
        norm.setWorkPpp(dto.getWorkPpp());
        norm.setSpecialty(dto.getSpecialty());
        norm.setOperationNorm(dto.getOperationNorm());
        
        // Заполняем operationOptionPpp на основе workPpp
        norm.setOperationOptionPpp(determineOperationOption(dto.getWorkPpp()));
        
        return norm;
    }

    private String determineOperationOption(String workPpp) {
        if (workPpp == null) {
            log.debug("workPpp is null, returning 'Опция'");
            return "Опция";
        }
        
        String lowerWorkPpp = workPpp.toLowerCase();
        log.debug("Processing workPpp: '{}' -> lowercase: '{}'", workPpp, lowerWorkPpp);
        
        // Проверяем на "Операция"
        if (lowerWorkPpp.contains("подготовка к ппп") ||
            lowerWorkPpp.contains("подключение") ||
            lowerWorkPpp.contains("проверка механиком") ||
            lowerWorkPpp.contains("проверка технологом") ||
            lowerWorkPpp.contains("проверка электронщиком") ||
            lowerWorkPpp.contains("входной контроль") ||
            lowerWorkPpp.contains("выходной контроль")) {
            log.debug("Определено как 'Операция' для workPpp: {}", workPpp);
            return "Операция";
        }
        
        log.debug("Определено как 'Опция' для workPpp: {}", workPpp);
        return "Опция";
    }

    private boolean isNotShabrenie(OperationNorm norm) {
        if (norm.getWorkPpp() == null) {
            return true;
        }
        
        boolean isShabrenie = norm.getWorkPpp().toLowerCase().startsWith("шабрение");
        if (isShabrenie) {
            log.debug("Пропущена запись с 'Шабрение': workPpp={}", norm.getWorkPpp());
        }
        return !isShabrenie;
    }
}