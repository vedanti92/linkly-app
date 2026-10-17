package com.url.linkly.service;

import com.url.linkly.dtos.ClickEventDTO;
import com.url.linkly.dtos.UrlMappingDTO;
import com.url.linkly.models.ClickEvent;
import com.url.linkly.models.UrlMapping;
import com.url.linkly.models.User;
import com.url.linkly.repository.ClickEventRepository;
import com.url.linkly.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlMappingServiceTest {

        @Mock
        private UrlMappingRepository urlMappingRepository;

        @Mock
        private ClickEventRepository clickEventRepository;

        @InjectMocks
        private UrlMappingService urlMappingService;

        private User user;
        private UrlMapping urlMapping;

        @BeforeEach
        void setUp() {
                user = new User();
                user.setId(1L);
                user.setUsername("vedanti");
                user.setEmail("vedanti@gmail.com");
                user.setRole("ROLE_USER");

                urlMapping = new UrlMapping();
                urlMapping.setId(1L);
                urlMapping.setOriginalUrl("https://example.com");
                urlMapping.setShortUrl("abc12345");
                urlMapping.setClickCount(0);
                urlMapping.setCreatedDate(LocalDateTime.now());
                urlMapping.setUser(user);
        }

        // ───── createShortUrl ─────

        @Test
        void createShortUrl_Success() {
                when(urlMappingRepository.saveAndFlush(any(UrlMapping.class)))
                                .thenReturn(urlMapping);

                UrlMappingDTO result = urlMappingService.createShortUrl("https://example.com", user);

                assertNotNull(result);
                assertEquals("https://example.com", result.getOriginalUrl());
                assertEquals("vedanti", result.getUsername());
                verify(urlMappingRepository).saveAndFlush(any(UrlMapping.class));
        }

        @Test
        void createShortUrl_RetriesWhenShortCodeAlreadyExists() {
                when(urlMappingRepository.existsByShortUrl(anyString()))
                                .thenReturn(true, false);

                when(urlMappingRepository.saveAndFlush(any(UrlMapping.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                UrlMappingDTO result = urlMappingService.createShortUrl("https://example.com", user);

                assertNotNull(result);
                assertNotEquals("abc12345", result.getShortUrl());

                verify(urlMappingRepository, times(2))
                                .existsByShortUrl(anyString());

                verify(urlMappingRepository)
                                .saveAndFlush(argThat(mapping -> !"abc12345".equals(mapping.getShortUrl())));
        }

        @Test
        void createShortUrl_RejectsJavaScriptUrl() {
                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> urlMappingService.createShortUrl(
                                                "javascript:alert(1)", user));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

                verify(urlMappingRepository, never()).save(any(UrlMapping.class));
        }

        @Test
        void createShortUrl_RejectsUrlWithoutHost() {
                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> urlMappingService.createShortUrl(
                                                "https:///missing-host", user));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

                verify(urlMappingRepository, never()).save(any(UrlMapping.class));
        }

        // ───── getOriginalUrl ─────

        @Test
        void getOriginalUrl_Success_IncrementsClickCount() {
                when(urlMappingRepository.findByShortUrl("abc12345")).thenReturn(urlMapping);
                when(urlMappingRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);
                when(clickEventRepository.save(any(ClickEvent.class))).thenReturn(new ClickEvent());

                UrlMapping result = urlMappingService.getOriginalUrl("abc12345");

                assertNotNull(result);
                assertEquals(1, result.getClickCount());
                verify(clickEventRepository).save(any(ClickEvent.class));
        }

        @Test
        void getOriginalUrl_ReturnsNull_WhenShortUrlNotFound() {
                when(urlMappingRepository.findByShortUrl("nonexistent")).thenReturn(null);

                UrlMapping result = urlMappingService.getOriginalUrl("nonexistent");

                assertNull(result);
                verify(clickEventRepository, never()).save(any());
        }

        // ───── getUrlsByUser ─────

        @Test
        void getUrlsByUser_ReturnsListOfDTOs() {
                when(urlMappingRepository.findByUser(user)).thenReturn(List.of(urlMapping));

                List<UrlMappingDTO> result = urlMappingService.getUrlsByUser(user);

                assertEquals(1, result.size());
                assertEquals("https://example.com", result.get(0).getOriginalUrl());
        }

        // ───── deleteUrl ─────

        @Test
        void deleteUrl_Success_WhenOwner() {
                when(urlMappingRepository.findById(1L)).thenReturn(Optional.of(urlMapping));

                assertDoesNotThrow(() -> urlMappingService.deleteUrl(1L, user));
                verify(urlMappingRepository).delete(urlMapping);
        }

        @Test
        void deleteUrl_ThrowsException_WhenNotOwner() {
                User anotherUser = new User();
                anotherUser.setId(2L);
                anotherUser.setUsername("someone_else");

                when(urlMappingRepository.findById(1L)).thenReturn(Optional.of(urlMapping));

                assertThrows(RuntimeException.class, () -> urlMappingService.deleteUrl(1L, anotherUser));
                verify(urlMappingRepository, never()).delete(any());
        }

        @Test
        void deleteUrl_ThrowsException_WhenUrlNotFound() {
                when(urlMappingRepository.findById(99L)).thenReturn(Optional.empty());

                assertThrows(RuntimeException.class, () -> urlMappingService.deleteUrl(99L, user));
        }

        // ───── updateUrl ─────

        @Test
        void updateUrl_Success_WhenOwner() {
                when(urlMappingRepository.findById(1L)).thenReturn(Optional.of(urlMapping));
                when(urlMappingRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);

                UrlMappingDTO result = urlMappingService.updateUrl(1L, "https://newurl.com", user);

                assertNotNull(result);
                assertEquals("https://newurl.com", result.getOriginalUrl());
        }

        @Test
        void updateUrl_ThrowsException_WhenNotOwner() {
                User anotherUser = new User();
                anotherUser.setId(2L);

                when(urlMappingRepository.findById(1L)).thenReturn(Optional.of(urlMapping));

                assertThrows(RuntimeException.class,
                                () -> urlMappingService.updateUrl(1L, "https://newurl.com", anotherUser));
        }

        @Test
        void updateUrl_RejectsInvalidUrl() {
                when(urlMappingRepository.findById(1L))
                                .thenReturn(Optional.of(urlMapping));

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> urlMappingService.updateUrl(
                                                1L, "javascript:alert(1)", user));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

                verify(urlMappingRepository, never()).save(any(UrlMapping.class));
        }

        // ───── getClickEventsByDate ─────

        @Test
        void getClickEventsByDate_ReturnsGroupedEvents() {
                ClickEvent clickEvent = new ClickEvent();
                clickEvent.setClickDate(LocalDateTime.now());
                clickEvent.setUrlMapping(urlMapping);

                when(urlMappingRepository.findByShortUrl("abc12345"))
                                .thenReturn(urlMapping);

                when(clickEventRepository.findByUrlMappingAndClickDateBetween(
                                any(), any(), any()))
                                .thenReturn(List.of(clickEvent));

                List<ClickEventDTO> result = urlMappingService.getClickEventsByDate(
                                "abc12345",
                                LocalDateTime.now().minusDays(1),
                                LocalDateTime.now().plusDays(1),
                                user);

                assertNotNull(result);
                assertEquals(1, result.size());
        }

        @Test
        void getClickEventsByDate_ThrowsNotFound_WhenShortUrlNotFound() {
                when(urlMappingRepository.findByShortUrl("invalid"))
                                .thenReturn(null);

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> urlMappingService.getClickEventsByDate(
                                                "invalid",
                                                LocalDateTime.now().minusDays(1),
                                                LocalDateTime.now(),
                                                user));

                assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }

        @Test
        void getClickEventsByDate_ThrowsNotFound_WhenNotOwner() {
                User anotherUser = new User();
                anotherUser.setId(2L);
                anotherUser.setUsername("someone_else");

                when(urlMappingRepository.findByShortUrl("abc12345"))
                                .thenReturn(urlMapping);

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> urlMappingService.getClickEventsByDate(
                                                "abc12345",
                                                LocalDateTime.now().minusDays(1),
                                                LocalDateTime.now(),
                                                anotherUser));

                assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

                verify(clickEventRepository, never())
                                .findByUrlMappingAndClickDateBetween(
                                                any(), any(), any());
        }

        // ───── getTotalClicksByUserAndDate ─────

        @Test
        void getTotalClicksByUserAndDate_ReturnsMap() {
                ClickEvent clickEvent = new ClickEvent();
                clickEvent.setClickDate(LocalDateTime.now());
                clickEvent.setUrlMapping(urlMapping);

                when(urlMappingRepository.findByUser(user)).thenReturn(List.of(urlMapping));
                when(clickEventRepository.findByUrlMappingInAndClickDateBetween(any(), any(), any()))
                                .thenReturn(List.of(clickEvent));

                Map<LocalDate, Long> result = urlMappingService.getTotalClicksByUserAndDate(
                                user,
                                LocalDate.now().minusDays(1),
                                LocalDate.now());

                assertNotNull(result);
                assertEquals(1, result.size());
        }
}