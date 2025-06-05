package org.parking.manager.service.annotation;

import org.parking.manager.service.dictionary.ParkingAuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    ParkingAuditAction action();
    boolean log() default true;
}
