package com.cornidezstudios.inventory.services;

import com.cornidezstudios.inventory.dtos.NewItemRequestDTO;
import com.cornidezstudios.inventory.dtos.UpdateItemRequestDTO;
import com.cornidezstudios.inventory.mappers.ItemMapper;
import com.cornidezstudios.inventory.models.Item;
import com.cornidezstudios.inventory.repositories.ItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final WebClient mediaClient;

    @Transactional
    public void createItem(NewItemRequestDTO newItemRequestDTO) {

        if (newItemRequestDTO == null) {
            throw new IllegalArgumentException("New item request cannot be null");
        }

        // http call to upload images
        List<String> imageNames = uploadImages(newItemRequestDTO.images());

        // Create item using the mapper
        Item item = ItemMapper.toItem(newItemRequestDTO, imageNames);

        // Save item to the database
        try {
            itemRepository.save(item);
        } catch (Exception e) {
            // Unsuccessful save, remove the uploaded images
            try {
                deleteImages(imageNames);
            } catch (Exception ex) {
                // Log the deletion failure but do not throw again
                System.err.println("Failed to delete images after save failure: " + ex.getMessage());
            }
            throw new RuntimeException("Failed to save item: " + e.getMessage(), e);
        }

    }

    public List<Item> getAllItems() throws Exception {
        return itemRepository.findAll();
    }

    public Item getItemById(UUID id) throws Exception {
        return itemRepository.findById(id)
                .orElseThrow(() -> new Exception("Item not found"));
    }

    @Transactional
    public void updateItemById(UUID id, UpdateItemRequestDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Update item request cannot be null");
        }

        // fetch existing item
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("item not found"));

        // upload new images (if any)
        List<String> originalImageNames = List.copyOf(item.getImages());
        List<String> newImageNames = dto.images() == null || dto.images().isEmpty() ? List.of() : uploadImages(dto.images());

        // update item with new data
        Item updatedItem = ItemMapper.toItem(dto, newImageNames, item);

        // save and delete old images on success; on failure roll back + delete new images
        try {
            itemRepository.save(updatedItem);
            // If there are new images, we need to delete the old ones
            if (!newImageNames.isEmpty() && !originalImageNames.isEmpty() && !originalImageNames.equals(newImageNames)) {
                deleteImages(originalImageNames);
            }
        } catch (Exception ex) {
            if (!newImageNames.isEmpty()) deleteImages(newImageNames);
            throw new RuntimeException("update failed", ex);
        }
    }

    @Transactional
    public void deleteItemById(UUID id) {

        if (id == null) throw new IllegalArgumentException("id null");

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        List<String> names = List.copyOf(item.getImages());   // snapshot before delete

        try {
            itemRepository.delete(item);                      // flushes on commit
        } catch (Exception ex) {
            throw new RuntimeException("Delete failed", ex);
        }

        if (!names.isEmpty()) {
            try { deleteImages(names); }
            catch (Exception ex) {
                System.err.println("Failed to delete images: " + ex.getMessage());
            }
        }
    }


    private List<String> uploadImages(@NotEmpty List<MultipartFile> images) {
        if (images == null || images.isEmpty()) throw new IllegalArgumentException("Images required");

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        images.forEach(f -> mb.part("files", f.getResource()));

        return mediaClient.post()
                .uri("/api/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mb.build()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    private void deleteImages(List<String> names) {
        Mono.when(
                names.stream()
                        .map(n -> mediaClient.delete()
                                .uri("/api/files/{name}", n)
                                .retrieve()
                                .toBodilessEntity()
                                .then())
                        .toList()
        ).subscribe();
    }

}
