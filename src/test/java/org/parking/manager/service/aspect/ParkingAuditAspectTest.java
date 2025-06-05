package org.parking.manager.service.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.model.ParkingEntryRequest;
import org.parking.manager.service.annotation.Audited;
import org.parking.manager.service.dictionary.ParkingAuditAction;
import org.parking.manager.service.repository.ParkingAuditRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ParkingAuditAspectTest {
    @Mock
    private ParkingAuditRepository auditRepository;

    @InjectMocks
    private ParkingAuditAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // закрываем ресурсы Mockito
    }

    // Вспомогательный класс с тестовым методом
    static class TestClass {
        @Audited(action = ParkingAuditAction.ENTRY_ATTEMPT)
        public String testMethod(ParkingEntryRequest request) {
            return "ok";
        }
    }

    @Test
    void auditAction_shouldWriteAuditSuccess() throws Throwable {
        // Arrange
        var method = TestClass.class.getMethod("testMethod", ParkingEntryRequest.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);

        var req = new ParkingEntryRequest().carNumber("A123BC");

        when(joinPoint.getArgs()).thenReturn(new Object[]{req});
        when(joinPoint.proceed()).thenReturn("ok");

        // Act
        var result = aspect.auditAction(joinPoint);

        // Assert
        verify(auditRepository, atLeastOnce()).save(argThat(audit ->
                audit.getCarNumber().equals("A123BC")
                        && audit.getAction().name().equals("ENTRY_SUCCESS")
        ));
        // Проверяем что результат прокидывается
        assertEquals("ok", result);
    }

    @Test
    void auditAction_shouldWriteAuditFailOnException() throws Throwable {
        // Arrange
        var method = TestClass.class.getMethod("testMethod", ParkingEntryRequest.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);

        var req = new ParkingEntryRequest().carNumber("A123BC");

        when(joinPoint.getArgs()).thenReturn(new Object[]{req});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Ошибка"));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> aspect.auditAction(joinPoint));

        verify(auditRepository, atLeastOnce()).save(argThat(audit ->
                audit.getCarNumber().equals("A123BC")
                        && audit.getAction().name().equals("ENTRY_FAIL")
        ));
    }
}
