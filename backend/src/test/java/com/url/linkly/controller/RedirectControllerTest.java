package com.url.linkly.controller;

import com.url.linkly.models.UrlMapping;
import com.url.linkly.service.UrlMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlMappingService urlMappingService;

    // ───── /{shortUrl} ─────

    @Test
    @WithMockUser
    void redirect_Success_WhenShortUrlExists() throws Exception {
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl("https://example.com");
        urlMapping.setShortUrl("abc12345");

        when(urlMappingService.getOriginalUrl("abc12345")).thenReturn(urlMapping);

        mockMvc.perform(get("/abc12345"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    @WithMockUser
    void redirect_Returns404_WhenShortUrlNotFound() throws Exception {
        when(urlMappingService.getOriginalUrl("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }
}