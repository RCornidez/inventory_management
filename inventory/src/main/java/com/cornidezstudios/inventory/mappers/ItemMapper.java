package com.cornidezstudios.inventory.mappers;

import com.cornidezstudios.inventory.dtos.NewItemRequestDTO;
import com.cornidezstudios.inventory.dtos.UpdateItemRequestDTO;
import com.cornidezstudios.inventory.enums.Status;
import com.cornidezstudios.inventory.models.Item;

import java.util.List;

public final class ItemMapper {
    public static Item toItem(NewItemRequestDTO dto, List<String> imageNames) {
        Item item = new Item();

        // Required fields
        item.setLength(dto.length());
        item.setWidth(dto.width());
        item.setHeight(dto.height());
        item.setVolume(dto.volume());
        item.setWeight(dto.weight());

        // Preprocessed field
        item.setImages(imageNames);

        // Optional fields with defaults
        item.setManufacturer(dto.manufacturer() == null ? "" : dto.manufacturer());
        item.setModel(dto.model() == null ? "" : dto.model());
        item.setQuantity(dto.quantity() == null ? 1 : dto.quantity());

        // Automatically set field
        item.setStatus(Status.CREATED);
        return item;
    }

    public static Item toItem(UpdateItemRequestDTO dto, List<String> newImageNames, Item item) {

        // Conditional updates based on non-null values in the DTO
        if (dto.length() != null) item.setLength(dto.length());
        if (dto.width() != null) item.setWidth(dto.width());
        if (dto.height() != null) item.setHeight(dto.height());
        if (dto.volume() != null) item.setVolume(dto.volume());
        if (dto.weight() != null) item.setWeight(dto.weight());
        if (dto.manufacturer() != null) item.setManufacturer(dto.manufacturer());
        if (dto.model() != null) item.setModel(dto.model());
        if (dto.price() != null) item.setPrice(dto.price());
        if (dto.quantity() != null) item.setQuantity(dto.quantity());
        if (dto.status() != null) item.setStatus(dto.status());
        if (dto.shopifyTitle() != null) item.setShopifyTitle(dto.shopifyTitle());
        if (dto.shopifyDescription() != null) item.setShopifyDescription(dto.shopifyDescription());
        if (dto.notes() != null) item.setNotes(dto.notes());
        if (newImageNames != null && !newImageNames.isEmpty()) item.setImages(newImageNames);

        return item;
    }
}
