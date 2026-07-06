package com.accenture.springsecurity.repository;

import com.accenture.springsecurity.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
