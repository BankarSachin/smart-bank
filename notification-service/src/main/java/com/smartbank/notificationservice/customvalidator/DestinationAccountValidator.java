package com.smartbank.notificationservice.customvalidator;
import com.smartbank.notificationservice.dto.NotificationRequest;
import com.smartbank.notificationservice.enums.NotificationType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DestinationAccountValidator implements ConstraintValidator<ValidDestinationAccount, NotificationRequest> {

    @Override
    public void initialize(ValidDestinationAccount constraintAnnotation) {
    }

    @Override
    public boolean isValid(NotificationRequest dto, ConstraintValidatorContext context) {
        if (dto.getNotificationType() == NotificationType.TRANSFER) {
            return dto.getDestinationAccountNumber() != null && !dto.getDestinationAccountNumber().isEmpty() && dto.getDestinationCurrentBalance()!=null;
        }
        return true;
    }
}
