package com.atnjupt.mongo.web;

import com.atnjupt.mongo.model.DemoUser;
import com.atnjupt.mongo.service.AggregationService;
import com.atnjupt.mongo.service.DemoUserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/demo-users")
public class DemoUserController {

    private final DemoUserService demoUserService;
    private final AggregationService aggregationService;

    public DemoUserController(DemoUserService demoUserService, AggregationService aggregationService) {
        this.demoUserService = demoUserService;
        this.aggregationService = aggregationService;
    }

    @PostMapping
    public ResponseEntity<DemoUser> create(@Valid @RequestBody DemoUser input) {
        return ResponseEntity.ok(demoUserService.create(input));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemoUser> findById(@PathVariable String id) {
        Optional<DemoUser> found = demoUserService.findById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DemoUser>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(demoUserService.searchByName(name));
    }

    @GetMapping("/searchByAge")
    public ResponseEntity<Page<DemoUser>> findByAgeRange(@RequestParam(required = false) Integer min,
                                                         @RequestParam(required = false) Integer max,
                                                         @RequestParam(defaultValue = "0") @Min(0) int page,
                                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        return ResponseEntity.ok(demoUserService.findByAgeRange(min, max, page, size));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DemoUser> updatePartial(@PathVariable String id,
                                                  @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(demoUserService.updatePartial(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        demoUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/top-tags")
    public ResponseEntity<List<AggregationService.AggregationResult>> topTags(@RequestParam(defaultValue = "5") @Min(1) int limit) {
        return ResponseEntity.ok(aggregationService.topTagsByUserCount(limit));
    }
}


