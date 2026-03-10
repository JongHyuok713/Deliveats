package com.example.deliveats.dto.delivery;

import com.example.deliveats.domain.delivery.DeliveryStatus;
import jakarta.validation.constraints.NotNull;

public record DeliveryStatusUpdateRequest(@NotNull DeliveryStatus status) {
}
