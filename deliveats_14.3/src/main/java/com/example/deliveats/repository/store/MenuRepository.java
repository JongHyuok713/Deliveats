package com.example.deliveats.repository.store;

import com.example.deliveats.domain.store.Menu;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MenuRepository extends JpaRepository<Menu, Long> {
}
