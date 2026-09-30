package com.ejemplo.franquicias.repository;

import com.ejemplo.franquicias.domain.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranchiseRepository extends ReactiveMongoRepository<Franchise, String> { }
