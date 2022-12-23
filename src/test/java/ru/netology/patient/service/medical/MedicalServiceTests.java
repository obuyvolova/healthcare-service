package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class MedicalServiceTests {
    @ParameterizedTest
    @MethodSource("sourseForCheckBloodPressure")
    void testCheckBloodPressure(PatientInfo patientInfo, String id, int times) {

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(id)).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
        medicalService.checkBloodPressure(id, new BloodPressure(120, 80));

        Mockito.verify(alertService, Mockito.times(times)).
                send(String.format("Warning, patient with id: %s, need help", patientInfo.getId()));

    }

    private static Stream<Arguments> sourseForCheckBloodPressure() {
        return Stream.of(Arguments.of(new PatientInfo("123", "Masha", "Sidorova", null,
                        new HealthInfo(new BigDecimal("36.66667"), new BloodPressure(120, 70))), "123", 1),
                Arguments.of(new PatientInfo("124", "Katya", "Ivanova", null,
                        new HealthInfo(new BigDecimal("37.66667"), new BloodPressure(120, 80))), "124", 0));
    }

    @ParameterizedTest
    @MethodSource("sourseForCheckTemperature")
    void testCheckTemperature(PatientInfo patientInfo, String id, int times) {

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(id)).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
        medicalService.checkTemperature(id, new BigDecimal("36.666666"));

        Mockito.verify(alertService, Mockito.times(times)).
                send(String.format("Warning, patient with id: %s, need help", patientInfo.getId()));

    }

    private static Stream<Arguments> sourseForCheckTemperature() {
        return Stream.of(Arguments.of(new PatientInfo("123", "Masha", "Sidorova", null,
                        new HealthInfo(new BigDecimal("39.66667"), new BloodPressure(120, 70))), "123", 1),
                Arguments.of(new PatientInfo("124", "Katya", "Ivanova", null,
                        new HealthInfo(new BigDecimal("37.66667"), new BloodPressure(120, 80))), "124", 0));
    }

    @Test
    void testCheckBloodPressureSendCaptor() {
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById("12345")).
                thenReturn(new PatientInfo("12345", "Katya", "Ivanova", null,
                        new HealthInfo(new BigDecimal("37.66667"), new BloodPressure(120, 100))));

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
        medicalService.checkBloodPressure("12345", new BloodPressure(120, 80));

        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals(String.format("Warning, patient with id: %s, need help", 12345),
                argumentCaptor.getValue());
    }

}
