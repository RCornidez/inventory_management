package com.cornidezstudios.inventory.repositories;

import com.cornidezstudios.inventory.models.Item;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> { }