package com.smartbank.notificationservice.customvalidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.smartbank.notificationservice.enums.NotificationType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom Validator to make destination account type mandatory in case of notification type as {@link NotificationType.TRANSFER}
 * @author Sachin
 */
@Constraint(validatedBy = DestinationAccountValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDestinationAccount {
    String message() default "Destination account number is required for transfer notifications.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
