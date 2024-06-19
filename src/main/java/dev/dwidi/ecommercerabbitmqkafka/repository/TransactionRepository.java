package dev.dwidi.ecommercerabbitmqkafka.repository;

import dev.dwidi.ecommercerabbitmqkafka.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    @Query("SELECT DISTINCT t.user.id FROM Transaction t WHERE t.product.id IN :productIds AND t.user.id != :userId")
    List<Long> findSimilarUserIdsByProductIds(List<Long> productIds, Long userId);

    @Query("SELECT DISTINCT t.product.id FROM Transaction t WHERE t.user.id IN :userIds")
    List<Long> findProductIdsByUserIds(List<Long> userIds);

    @Query("SELECT DISTINCT t.product.id FROM Transaction t WHERE t.user.id = :userId")
    List<Long> findProductIdsByUserId(Long userId);
}
