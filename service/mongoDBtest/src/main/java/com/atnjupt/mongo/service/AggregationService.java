package com.atnjupt.mongo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AggregationService {
    private final MongoTemplate mongoTemplate;

    public AggregationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Data
    @AllArgsConstructor
    public static class AggregationResult {
        private String tag;
        private long count;
    }

    public List<AggregationResult> topTagsByUserCount(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("$tags"),
                Aggregation.group(Fields.field("tag", "$tags")).count().as("count"),
                Aggregation.project("count").and("$_id.tag").as("tag"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "count"),
                Aggregation.limit(limit)
        );
        AggregationResults<AggregationResult> results = mongoTemplate.aggregate(
                aggregation, "demo_users", AggregationResult.class);
        return results.getMappedResults();
    }
}


