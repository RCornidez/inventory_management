package com.cornidezstudios.inventory.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record NewItemRequestDTO(
        @NotNull BigDecimal length,
        @NotNull BigDecimal width,
        @NotNull BigDecimal height,
        @NotNull BigDecimal volume,
        @NotNull BigDecimal weight,
        @NotEmpty List<MultipartFile> images,
        String manufacturer,
        String model,
        Integer quantity
) {
}
