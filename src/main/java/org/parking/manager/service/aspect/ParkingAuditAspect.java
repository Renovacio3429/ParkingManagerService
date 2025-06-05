package org.parking.manager.service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.openapitools.model.ParkingEntryRequest;
import org.openapitools.model.ParkingExitRequest;
import org.parking.manager.service.annotation.Audited;
import org.parking.manager.service.dictionary.ParkingAuditAction;
import org.parking.manager.service.entity.ParkingAudit;
import org.parking.manager.service.repository.ParkingAuditRepository;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ParkingAuditAspect {

    private final ParkingAuditRepository auditRepository;

    @Pointcut("@annotation(org.parking.manager.service.annotation.Audited)")
    public void auditedMethod() {
    }

    @Around("auditedMethod()")
    public Object auditAction(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Audited annotation = AnnotationUtils.findAnnotation(signature.getMethod(), Audited.class);
        if (annotation == null) {
            return pjp.proceed();
        }

        ParkingAuditAction baseAction = annotation.action();
        boolean logFlag = annotation.log();

        String carNumber = extractCarNumber(pjp.getArgs());
        if (carNumber == null) {
            log.warn("Could not extract car number for audit in method {}", signature.getMethod().getName());
        }

        if (logFlag) log.info("Audit: {} - {}", baseAction, carNumber);

        try {
            Object result = pjp.proceed();

            ParkingAuditAction success = baseAction.getSuccessAction();
            if (success != null) {
                audit(carNumber, success, success.getDefaultMessage());
                log.info("Audit: {} - {} - {}", success, carNumber, success.getDefaultMessage());
            }
            return result;
        } catch (Exception ex) {
            ParkingAuditAction fail = baseAction.getFailAction();
            String message = ex.getMessage() != null ? ex.getMessage() : (fail != null ? fail.getDefaultMessage() : "Unknown error");
            if (fail != null) {
                audit(carNumber, fail, message);
                log.warn("Audit: {} - {} - {}", fail, carNumber, message);
            }
            throw ex;
        }
    }

    private void audit(String carNumber, ParkingAuditAction action, String message) {
        auditRepository.save(ParkingAudit.builder()
                .carNumber(carNumber)
                .action(action)
                .message(message)
                .eventTime(OffsetDateTime.now())
                .build());
    }

    private String extractCarNumber(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof ParkingEntryRequest req) {
                return req.getCarNumber();
            } else if (arg instanceof ParkingExitRequest req) {
                return req.getCarNumber();
            }
        }
        return null;
    }
}
