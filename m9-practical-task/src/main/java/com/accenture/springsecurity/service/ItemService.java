package com.accenture.springsecurity.service;

import com.accenture.springsecurity.model.Item;
import com.accenture.springsecurity.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + id));
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public Item update(Long id, Item updated) {
        Item existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return itemRepository.save(existing);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
}
