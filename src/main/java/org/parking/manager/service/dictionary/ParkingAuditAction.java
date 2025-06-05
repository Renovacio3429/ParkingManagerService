package org.parking.manager.service.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParkingAuditAction {
    ENTRY_ATTEMPT("Попытка въезда", "ENTRY_SUCCESS", "ENTRY_FAIL"),
    ENTRY_SUCCESS("Въезд зарегистрирован", null, null),
    ENTRY_FAIL("Ошибка при въезде", null, null),
    EXIT_ATTEMPT("Попытка выезда", "EXIT_SUCCESS", "EXIT_FAIL"),
    EXIT_SUCCESS("Выезд зарегистрирован", null, null),
    EXIT_FAIL("Ошибка при выезде", null, null);

    private final String defaultMessage;
    private final String successAction;
    private final String failAction;

    public ParkingAuditAction getSuccessAction() {
        return successAction != null ? valueOf(successAction) : null;
    }

    public ParkingAuditAction getFailAction() {
        return failAction != null ? valueOf(failAction) : null;
    }
}
