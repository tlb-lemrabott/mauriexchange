package com.mauriexchange.code.repository;

import com.mauriexchange.code.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    
    Optional<Bank> findByCode(String code);
    
    List<Bank> findByIsActiveTrue();
    
    @Query("SELECT b FROM Bank b WHERE b.isActive = true ORDER BY b.name")
    List<Bank> findAllActiveBanks();
    
    boolean existsByCode(String code);
}
