package com.url.linkly.repository;

import com.url.linkly.models.UrlMapping;
import com.url.linkly.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Page<UrlMapping> findByUser(User user, Pageable pageable);

    UrlMapping findByShortUrl(String shortUrl);

    List<UrlMapping> findByUser(User user);

    boolean existsByOriginalUrlAndUser(String originalUrl, User user);

    boolean existsByShortUrl(String shortUrl);
}
