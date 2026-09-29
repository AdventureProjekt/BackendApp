package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.services.ActivityService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private final ActivityService activityService;

    public RestController(ActivityService activityService) {
        this.activityService = activityService;
    }
    @GetMapping

    public List<Activity> findAll() {

    return activityService.findAll();
    }

    @GetMapping("/{id]")
    public Activity findbyId(int id) {
        return activityService.findById(id);
}
@PostMapping
public Activity save(Activity activity) {
        return activityService.save(activity);
}
@PutMapping("/{id]")
    public Activity opdater(int id, Activity activity) {
        return activityService.opdater(id,activity);
}
@DeleteMapping("/{id]")
    public boolean deleteByid(int id) {
        return activityService.deleteById(id);
}



}
