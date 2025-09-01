package com.atnjupt.mongo;

import com.atnjupt.mongo.model.DemoUser;
import com.atnjupt.mongo.service.AggregationService;
import com.atnjupt.mongo.service.DemoUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MongoDbTestApplication.class)
@Testcontainers
class MongoDbIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    DemoUserService demoUserService;

    @Autowired
    AggregationService aggregationService;

    @Test
    void crud_and_aggregation_should_work() {
        DemoUser u1 = new DemoUser();
        u1.setName("Alice");
        u1.setAge(20);
        u1.setTags(Arrays.asList("java", "spring"));
        DemoUser saved = demoUserService.create(u1);

        assertThat(saved.getId()).isNotBlank();

        assertThat(demoUserService.findById(saved.getId())).isPresent();

        List<DemoUser> byName = demoUserService.searchByName("Alice");
        assertThat(byName).extracting(DemoUser::getName).contains("Alice");

        demoUserService.updatePartial(saved.getId(), java.util.Collections.singletonMap("age", 21));
        assertThat(demoUserService.findById(saved.getId()).get().getAge()).isEqualTo(21);

        List<AggregationService.AggregationResult> top = aggregationService.topTagsByUserCount(5);
        assertThat(top).isNotEmpty();

        demoUserService.deleteById(saved.getId());
        assertThat(demoUserService.findById(saved.getId())).isNotPresent();
    }
}


