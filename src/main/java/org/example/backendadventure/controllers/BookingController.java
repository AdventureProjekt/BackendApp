package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.LedigTid;
import org.example.backendadventure.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // GET /api/bookings              -> alle bookinger
    // GET /api/bookings?dato=2026-10-20 -> bookinger på en dato (US10)
    @GetMapping
    public List<Booking> findAll(@RequestParam(required = false) LocalDate dato) {
        if (dato == null) {
            return bookingService.findAll();
        }
        return bookingService.findByDato(dato);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> findById(@PathVariable int id) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    // GET /api/bookings/aktivitet/1?dato=2026-10-20 -> bookinger for én aktivitet (US11)
    @GetMapping("/aktivitet/{activityId}")
    public List<Booking> findByActivity(@PathVariable int activityId, @RequestParam LocalDate dato) {
        return bookingService.findByActivityOgDato(activityId, dato);
    }

    // GET /api/bookings/ledige?activityId=1&dato=2026-10-20 -> ledige tider (US2)
    @GetMapping("/ledige")
    public ResponseEntity<Object> ledigeTider(@RequestParam int activityId, @RequestParam LocalDate dato) {
        try {
            List<LedigTid> tider = bookingService.ledigeTider(activityId, dato);
            return new ResponseEntity<>(tider, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // US3 + US8
    @PostMapping
    public ResponseEntity<Object> opret(@RequestBody Booking booking) {
        try {
            Booking gemt = bookingService.opret(booking);
            return new ResponseEntity<>(gemt, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // US9
    @PutMapping("/{id}")
    public ResponseEntity<Object> opdater(@PathVariable int id, @RequestBody Booking booking) {
        try {
            Booking opdateret = bookingService.opdater(id, booking);
            if (opdateret == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(opdateret, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // US9
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        boolean slettet = bookingService.deleteById(id);
        if (!slettet) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
