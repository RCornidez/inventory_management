package com.cornidezstudios.inventory.controllers;

import com.cornidezstudios.inventory.dtos.NewItemRequestDTO;
import com.cornidezstudios.inventory.dtos.UpdateItemRequestDTO;
import com.cornidezstudios.inventory.models.Item;
import com.cornidezstudios.inventory.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createItem(@ModelAttribute @Valid NewItemRequestDTO dto) {
        service.createItem(dto);
    }

    @GetMapping
    public List<Item> getAllItems() throws Exception {
        return service.getAllItems();
    }

    @GetMapping("/{id}")
    public Item getOneItem(@PathVariable java.util.UUID id) throws Exception {
        return service.getItemById(id);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID id, @ModelAttribute UpdateItemRequestDTO dto) {
        service.updateItemById(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteItemById(id);
    }
}
