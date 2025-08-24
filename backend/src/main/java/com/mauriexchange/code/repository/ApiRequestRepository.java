package com.mauriexchange.code.repository;

import com.mauriexchange.code.entity.ApiRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiRequestRepository extends JpaRepository<ApiRequest, Long> {
    
    @Query("SELECT ar FROM ApiRequest ar WHERE ar.userIp = :userIp AND ar.createdAt >= :since")
    List<ApiRequest> findByUserIpAndCreatedAtSince(@Param("userIp") String userIp, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(ar) FROM ApiRequest ar WHERE ar.userIp = :userIp AND ar.createdAt >= :since")
    long countByUserIpAndCreatedAtSince(@Param("userIp") String userIp, @Param("since") LocalDateTime since);
    
    @Query("SELECT ar FROM ApiRequest ar WHERE ar.endpoint = :endpoint AND ar.createdAt >= :since ORDER BY ar.createdAt DESC")
    List<ApiRequest> findByEndpointAndCreatedAtSince(@Param("endpoint") String endpoint, @Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(ar.responseTimeMs) FROM ApiRequest ar WHERE ar.endpoint = :endpoint AND ar.createdAt >= :since")
    Double getAverageResponseTimeByEndpointAndSince(@Param("endpoint") String endpoint, @Param("since") LocalDateTime since);
}
