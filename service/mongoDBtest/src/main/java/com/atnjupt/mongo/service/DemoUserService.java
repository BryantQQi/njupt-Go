package com.atnjupt.mongo.service;

import com.atnjupt.mongo.model.DemoUser;
import com.atnjupt.mongo.repository.DemoUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DemoUserService {
    private final DemoUserRepository repository;
    private final MongoTemplate mongoTemplate;

    public DemoUserService(DemoUserRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public DemoUser create(DemoUser input) {
        input.setId(null);
        input.setCreatedAt(Instant.now());
        return repository.save(input);
    }

    public Optional<DemoUser> findById(String id) {
        return repository.findById(id);
    }

    public List<DemoUser> searchByName(String name) {
        return repository.findByName(name);
    }

    public Page<DemoUser> findByAgeRange(Integer min, Integer max, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = new Query();
        if (min != null) {
            query.addCriteria(Criteria.where("age").gte(min));
        }
        if (max != null) {
            query.addCriteria(Criteria.where("age").lte(max));
        }
        long total = mongoTemplate.count(query, DemoUser.class);
        query.with(pageable);
        List<DemoUser> content = mongoTemplate.find(query, DemoUser.class);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }

    public DemoUser updatePartial(String id, Map<String, Object> updates) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        updates.forEach(update::set);
        mongoTemplate.updateFirst(query, update, DemoUser.class);
        return mongoTemplate.findById(id, DemoUser.class);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}


