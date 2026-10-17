package com.url.linkly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.url.linkly.dtos.UrlMappingDTO;
import com.url.linkly.models.User;
import com.url.linkly.service.UrlMappingService;
import com.url.linkly.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlMappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlMappingService urlMappingService;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private UrlMappingDTO urlMappingDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("vedanti");
        user.setEmail("vedanti@gmail.com");
        user.setRole("ROLE_USER");

        urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(1L);
        urlMappingDTO.setOriginalUrl("https://example.com");
        urlMappingDTO.setShortUrl("abc12345");
        urlMappingDTO.setClickCount(0);
        urlMappingDTO.setCreatedDate(LocalDateTime.now());
        urlMappingDTO.setUsername("vedanti");
    }

    // ───── POST /api/urls/shorten ─────

    @Test
    @WithMockUser(username = "vedanti", roles = "USER")
    void createShortUrl_Success() throws Exception {
        when(userService.findByUsername("vedanti")).thenReturn(user);
        when(urlMappingService.createShortUrl(anyString(), any(User.class))).thenReturn(urlMappingDTO);

        mockMvc.perform(post("/api/urls/shorten")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("originalUrl", "https://example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("abc12345"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    // ───── GET /api/urls/myurls ─────

    @Test
    @WithMockUser(username = "vedanti", roles = "USER")
    void getUserUrls_Success() throws Exception {
        when(userService.findByUsername("vedanti")).thenReturn(user);

        org.springframework.data.domain.Page<UrlMappingDTO> urlPage = new org.springframework.data.domain.PageImpl<>(
                List.of(urlMappingDTO),
                org.springframework.data.domain.PageRequest.of(0, 5),
                1);

        when(urlMappingService.getUrlsByUser(
                eq(user),
                any(org.springframework.data.domain.Pageable.class))).thenReturn(urlPage);

        mockMvc.perform(get("/api/urls/myurls")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].shortUrl").value("abc12345"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    // ───── DELETE /api/urls/{id} ─────

    @Test
    @WithMockUser(username = "vedanti", roles = "USER")
    void deleteUrl_Success() throws Exception {
        when(userService.findByUsername("vedanti")).thenReturn(user);

        mockMvc.perform(delete("/api/urls/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("URL deleted successfully!"));
    }

    // ───── PUT /api/urls/{id} ─────

    @Test
    @WithMockUser(username = "vedanti", roles = "USER")
    void updateUrl_Success() throws Exception {
        urlMappingDTO.setOriginalUrl("https://newurl.com");

        when(userService.findByUsername("vedanti")).thenReturn(user);
        when(urlMappingService.updateUrl(eq(1L), eq("https://newurl.com"), any(User.class)))
                .thenReturn(urlMappingDTO);

        mockMvc.perform(put("/api/urls/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("originalUrl", "https://newurl.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://newurl.com"));
    }

    // ───── GET /api/urls/analytics/{shortUrl} ─────

    @Test
    @WithMockUser(username = "vedanti", roles = "USER")
    void getUrlAnalytics_Success() throws Exception {
        when(userService.findByUsername("vedanti")).thenReturn(user);

        when(urlMappingService.getClickEventsByDate(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(User.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/urls/analytics/abc12345")
                .param("startDate", "2024-01-01T00:00:00")
                .param("endDate", "2024-12-31T23:59:59"))
                .andExpect(status().isOk());
    }
}