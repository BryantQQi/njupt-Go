package com.atnjupt.mongo.repository;

import com.atnjupt.mongo.model.DemoUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DemoUserRepository extends MongoRepository<DemoUser, String> {
    List<DemoUser> findByName(String name);
    List<DemoUser> findByAgeBetween(Integer min, Integer max);
}


