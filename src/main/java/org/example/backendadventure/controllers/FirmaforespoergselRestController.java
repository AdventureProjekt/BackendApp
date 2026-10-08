package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Firmaforespoergsel;
import org.example.backendadventure.model.Status;
import org.example.backendadventure.model.TidsValg;
import org.example.backendadventure.services.FirmaforespoergselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forespoergsler")
@CrossOrigin("*")
public class FirmaforespoergselRestController {

    @Autowired
    FirmaforespoergselService forespoergselService;

    // /api/forespoergsler eller /api/forespoergsler?status=AFVENTER
    @GetMapping
    public List<Firmaforespoergsel> getForespoergsler(@RequestParam(required = false) Status status) {
        return forespoergselService.findAll(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Firmaforespoergsel> getForespoergsel(@PathVariable int id) {
        Firmaforespoergsel forespoergsel = forespoergselService.findById(id);
        if (forespoergsel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(forespoergsel);
    }

    @PostMapping("")
    public ResponseEntity<Object> postForespoergsel(@RequestBody Firmaforespoergsel forespoergsel) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(forespoergselService.opret(forespoergsel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Body: [{"activityId": 1, "tid": "10:00"}, {"activityId": 2, "tid": "11:00"}]
    @PutMapping("/{id}/godkend")
    public ResponseEntity<Object> godkend(@PathVariable int id, @RequestBody List<TidsValg> tider) {
        try {
            Firmaforespoergsel godkendt = forespoergselService.godkend(id, tider);
            if (godkendt == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(godkendt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/afvis")
    public ResponseEntity<Object> afvis(@PathVariable int id) {
        try {
            Firmaforespoergsel afvist = forespoergselService.afvis(id);
            if (afvist == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(afvist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
