package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin("*")
public class ActivityRestController {

    @Autowired
    ActivityService activityService;

    @GetMapping
    public List<Activity> getActivities() {
        return activityService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable int id) {
        Activity activity = activityService.findById(id);
        if (activity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activity);
    }

    @PostMapping("")
    public ResponseEntity<Object> postActivity(@RequestBody Activity activity) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(activityService.save(activity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putActivity(@PathVariable int id, @RequestBody Activity activity) {
        try {
            Activity opdateret = activityService.opdater(id, activity);
            if (opdateret == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(opdateret);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable int id) {
        try {
            if (!activityService.deleteById(id)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok("Aktivitet slettet: " + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
