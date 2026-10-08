package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Udstyr;
import org.example.backendadventure.model.UdstyrBehov;
import org.example.backendadventure.services.UdstyrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/udstyr")
@CrossOrigin("*")
public class UdstyrRestController {

    @Autowired
    UdstyrService udstyrService;

    // /api/udstyr?activityId=1
    @GetMapping
    public List<Udstyr> getUdstyr(@RequestParam int activityId) {
        return udstyrService.findByActivity(activityId);
    }

    // /api/udstyr/behov?dato=2026-10-20 (alle aktiviteter)
    // /api/udstyr/behov?dato=2026-10-20&activityId=1 (én aktivitet)
    @GetMapping("/behov")
    public List<UdstyrBehov> getBehov(@RequestParam LocalDate dato, @RequestParam(defaultValue = "0") int activityId) {
        return udstyrService.behov(dato, activityId);
    }

    @PostMapping("")
    public ResponseEntity<Object> postUdstyr(@RequestBody Udstyr udstyr) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(udstyrService.opret(udstyr));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUdstyr(@PathVariable int id) {
        if (!udstyrService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Udstyr slettet: " + id);
    }
}
