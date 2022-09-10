package com.example.teckit.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface TicketRepository extends PagingAndSortingRepository<Ticket, Integer> {
    List<Ticket> findByCreatorId(int creatorId, Sort sort);
}