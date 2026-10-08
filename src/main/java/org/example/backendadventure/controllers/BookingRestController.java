package org.example.backendadventure.controllers;

import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.LedigTid;
import org.example.backendadventure.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin("*")
public class BookingRestController {

    @Autowired
    BookingService bookingService;

    // /api/bookings eller /api/bookings?dato=2026-10-20
    @GetMapping
    public List<Booking> getBookings(@RequestParam(required = false) LocalDate dato) {
        if (dato == null) {
            return bookingService.findAll();
        }
        return bookingService.findByDato(dato);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable int id) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    // /api/bookings/aktivitet/1?dato=2026-10-20
    @GetMapping("/aktivitet/{activityId}")
    public List<Booking> getBookingsForActivity(@PathVariable int activityId, @RequestParam LocalDate dato) {
        return bookingService.findByActivityOgDato(activityId, dato);
    }

    // /api/bookings/ledige?activityId=1&dato=2026-10-20
    @GetMapping("/ledige")
    public List<LedigTid> getLedigeTider(@RequestParam int activityId, @RequestParam LocalDate dato) {
        return bookingService.ledigeTider(activityId, dato);
    }

    @PostMapping("")
    public ResponseEntity<Object> postBooking(@RequestBody Booking booking) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.opret(booking));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putBooking(@PathVariable int id, @RequestBody Booking booking) {
        try {
            Booking opdateret = bookingService.opdater(id, booking);
            if (opdateret == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(opdateret);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable int id) {
        if (!bookingService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Booking slettet: " + id);
    }
}
