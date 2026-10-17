package com.url.linkly.service;

import com.url.linkly.dtos.ClickEventDTO;
import com.url.linkly.dtos.UrlMappingDTO;
import com.url.linkly.models.ClickEvent;
import com.url.linkly.models.UrlMapping;
import com.url.linkly.models.User;
import com.url.linkly.repository.ClickEventRepository;
import com.url.linkly.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {
    private UrlMappingRepository urlMappingRepository;
    private ClickEventRepository clickEventRepository;
    private static final int MAX_SHORT_URL_ATTEMPTS = 10;

    public UrlMappingDTO createShortUrl(String originalUrl, User user) {
        if (originalUrl != null) {
            originalUrl = originalUrl.trim();
        }

        validateOriginalUrl(originalUrl);

        if (urlMappingRepository.existsByOriginalUrlAndUser(originalUrl, user)) {
            throw new RuntimeException(
                    "This URL already exists for your account!");
        }

        for (int attempt = 0; attempt < MAX_SHORT_URL_ATTEMPTS; attempt++) {
            String shortUrl = generateShortUrl();

            if (urlMappingRepository.existsByShortUrl(shortUrl)) {
                continue;
            }

            UrlMapping urlMapping = new UrlMapping();
            urlMapping.setOriginalUrl(originalUrl);
            urlMapping.setShortUrl(shortUrl);
            urlMapping.setUser(user);
            urlMapping.setCreatedDate(LocalDateTime.now());

            try {
                UrlMapping savedUrlMapping = urlMappingRepository.saveAndFlush(urlMapping);

                return convertToDto(savedUrlMapping);
            } catch (DataIntegrityViolationException e) {
                // A concurrent request may have saved the same code.
                // Generate another code and retry.
            }
        }

        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not generate a unique short URL. Please try again.");
    }

    private UrlMappingDTO convertToDto(UrlMapping urlMapping) {
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setUsername(urlMapping.getUser().getUsername());
        return urlMappingDTO;
    }

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    }

    public List<UrlMappingDTO> getUrlsByUser(User user) {
        return urlMappingRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<ClickEventDTO> getClickEventsByDate(
            String shortUrl,
            LocalDateTime start,
            LocalDateTime end,
            User user) {

        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);

        // Return 404 if the URL doesn't exist or belongs to another user.
        if (urlMapping == null
                || urlMapping.getUser() == null
                || !urlMapping.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "URL not found");
        }

        return clickEventRepository
                .findByUrlMappingAndClickDateBetween(urlMapping, start, end)
                .stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickDate().toLocalDate(),
                        Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    ClickEventDTO dto = new ClickEventDTO();
                    dto.setClickDate(entry.getKey());
                    dto.setCount(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings,
                start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        return clickEvents.stream()
                .collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()));
    }

    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping != null) {
            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
            urlMappingRepository.save(urlMapping);

            // Record click event
            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setClickDate(LocalDateTime.now());
            clickEvent.setUrlMapping(urlMapping);
            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }

    public void deleteUrl(Long id, User user) {
        UrlMapping urlMapping = urlMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found."));

        if (!urlMapping.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this URL.");
        }

        urlMappingRepository.delete(urlMapping);
    }

    public UrlMappingDTO updateUrl(Long id, String newOriginalUrl, User user) {
        UrlMapping urlMapping = urlMappingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found."));

        if (!urlMapping.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this URL.");
        }

        if (newOriginalUrl != null) {
            newOriginalUrl = newOriginalUrl.trim();
        }

        validateOriginalUrl(newOriginalUrl);

        urlMapping.setOriginalUrl(newOriginalUrl);
        UrlMapping saved = urlMappingRepository.save(urlMapping);
        return convertToDto(saved);
    }

    private void validateOriginalUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "URL is required");
        }

        try {
            URI uri = new URI(originalUrl);

            String scheme = uri.getScheme();

            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http")
                            || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null
                    || uri.getHost().isBlank()
                    || uri.getPort() == 0
                    || uri.getPort() > 65535) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Please provide a valid HTTP or HTTPS URL");
            }
        } catch (URISyntaxException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please provide a valid HTTP or HTTPS URL");
        }
    }

    public Page<UrlMappingDTO> getUrlsByUser(
            User user, Pageable pageable) {

        return urlMappingRepository
                .findByUser(user, pageable)
                .map(this::convertToDto);
    }
}
