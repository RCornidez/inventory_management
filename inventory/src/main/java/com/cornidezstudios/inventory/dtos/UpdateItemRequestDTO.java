package com.cornidezstudios.inventory.dtos;

import com.cornidezstudios.inventory.enums.Status;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateItemRequestDTO(
        @NotNull UUID id,
        BigDecimal length,
        BigDecimal width,
        BigDecimal height,
        BigDecimal volume,
        BigDecimal weight,
        List<MultipartFile> images,
        String manufacturer,
        String model,
        BigDecimal price,
        Integer quantity,
        Status status,
        String shopifyTitle,
        String shopifyDescription,
        String notes
) {}
