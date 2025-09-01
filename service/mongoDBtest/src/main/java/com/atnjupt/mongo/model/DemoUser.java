package com.atnjupt.mongo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "demo_users")
public class DemoUser {

    @Id
    private String id;

    @Indexed
    private String name;

    private Integer age;

    private List<String> tags;

    private Instant createdAt;
}


