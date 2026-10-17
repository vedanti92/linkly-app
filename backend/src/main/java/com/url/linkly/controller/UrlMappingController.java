package com.url.linkly.controller;

import com.url.linkly.dtos.ClickEventDTO;
import com.url.linkly.dtos.UrlMappingDTO;
import com.url.linkly.models.User;
import com.url.linkly.service.UrlMappingService;
import com.url.linkly.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
    private UrlMappingService urlMappingService;
    private UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createShortUrl(@RequestBody Map<String, String> request, Principal principal) {
        try {
            String originalUrl = request.get("originalUrl");
            User user = userService.findByUsername(principal.getName());
            UrlMappingDTO urlMappingDTO = urlMappingService.createShortUrl(originalUrl, user);
            return ResponseEntity.ok(urlMappingDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<UrlMappingDTO>> getUserUrls(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100");
        }

        User user = userService.findByUsername(principal.getName());

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdDate")
                        .and(Sort.by(Sort.Direction.DESC, "id")));

        Page<UrlMappingDTO> urls = urlMappingService.getUrlsByUser(user, pageable);

        return ResponseEntity.ok(urls);
    }

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(
            @PathVariable String shortUrl,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Principal principal) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        User user = userService.findByUsername(principal.getName());

        List<ClickEventDTO> clickEventDTOS = urlMappingService.getClickEventsByDate(
                shortUrl, start, end, user);

        return ResponseEntity.ok(clickEventDTOS);
    }

    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(Principal principal,
            @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        User user = userService.findByUsername(principal.getName());
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        Map<LocalDate, Long> totalClicks = urlMappingService.getTotalClicksByUserAndDate(user, start, end);
        return ResponseEntity.ok(totalClicks);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteUrl(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        urlMappingService.deleteUrl(id, user);

        return ResponseEntity.ok("URL deleted successfully!");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> updateUrl(@PathVariable Long id, @RequestBody Map<String, String> request,
            Principal principal) {
        User user = userService.findByUsername(principal.getName());
        String newOriginalUrl = request.get("originalUrl");

        UrlMappingDTO updated = urlMappingService.updateUrl(id, newOriginalUrl, user);

        return ResponseEntity.ok(updated);
    }
}
