package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Firmaforespoergsel;
import org.example.backendadventure.model.Status;
import org.example.backendadventure.model.TidsValg;
import org.example.backendadventure.services.FirmaforespoergselService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/forespoergsler")
public class FirmaforespoergselController {
    private final FirmaforespoergselService forespoergselService;

    public FirmaforespoergselController(FirmaforespoergselService forespoergselService) {
        this.forespoergselService = forespoergselService;
    }

    // US5: GET /api/forespoergsler eller /api/forespoergsler?status=AFVENTER
    @GetMapping
    public List<Firmaforespoergsel> findAll(@RequestParam(required = false) Status status) {
        return forespoergselService.findAll(status);
    }

    // US6 (og firmakunden kan se status)
    @GetMapping("/{id}")
    public ResponseEntity<Firmaforespoergsel> findById(@PathVariable int id) {
        Firmaforespoergsel forespoergsel = forespoergselService.findById(id);
        if (forespoergsel == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forespoergsel, HttpStatus.OK);
    }

    // US4
    @PostMapping
    public ResponseEntity<Object> opret(@RequestBody Firmaforespoergsel forespoergsel) {
        try {
            Firmaforespoergsel gemt = forespoergselService.opret(forespoergsel);
            return new ResponseEntity<>(gemt, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // US7: body = [{"activityId": 1, "tid": "10:00"}, {"activityId": 2, "tid": "11:00"}]
    @PutMapping("/{id}/godkend")
    public ResponseEntity<Object> godkend(@PathVariable int id, @RequestBody List<TidsValg> tider) {
        try {
            Firmaforespoergsel godkendt = forespoergselService.godkend(id, tider);
            if (godkendt == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(godkendt, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // US7
    @PutMapping("/{id}/afvis")
    public ResponseEntity<Object> afvis(@PathVariable int id) {
        try {
            Firmaforespoergsel afvist = forespoergselService.afvis(id);
            if (afvist == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(afvist, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
