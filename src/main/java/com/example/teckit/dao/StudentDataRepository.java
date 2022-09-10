package com.example.teckit.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StudentDataRepository extends CrudRepository<StudentData, Integer> {
}