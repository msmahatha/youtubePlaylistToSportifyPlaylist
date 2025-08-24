package com.playlist.converter.repository;

import com.playlist.converter.model.ConversionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ConversionResult entity
 */
@Repository
public interface ConversionRepository extends JpaRepository<ConversionResult, Long> {
    
    /**
     * Find conversion by conversion ID
     */
    Optional<ConversionResult> findByConversionId(String conversionId);
    
    /**
     * Find conversions by user ID
     */
    List<ConversionResult> findBySpotifyUserIdOrderByCreatedAtDesc(String spotifyUserId);
    
    /**
     * Find conversions by status
     */
    List<ConversionResult> findByStatus(ConversionResult.ConversionStatus status);
    
    /**
     * Find conversions created after a specific date
     */
    List<ConversionResult> findByCreatedAtAfter(LocalDateTime dateTime);
    
    /**
     * Find pending or in-progress conversions older than specified time
     * (useful for cleanup of stale conversions)
     */
    List<ConversionResult> findByStatusInAndCreatedAtBefore(
            List<ConversionResult.ConversionStatus> statuses, 
            LocalDateTime dateTime
    );
}
