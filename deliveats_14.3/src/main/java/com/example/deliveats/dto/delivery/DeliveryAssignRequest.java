package com.example.deliveats.dto.delivery;

import jakarta.validation.constraints.NotNull;

public record DeliveryAssignRequest(@NotNull Long orderId) {
}
