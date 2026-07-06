package com.accenture.springsecurity.controller;

import com.accenture.springsecurity.model.Item;
import com.accenture.springsecurity.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Item> create(@Valid @RequestBody Item item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(item));
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @Valid @RequestBody Item item) {
        return itemService.update(id, item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
