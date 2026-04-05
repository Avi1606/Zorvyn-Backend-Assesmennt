package org.avi1606.financedataprocessing.repository;

import org.avi1606.financedataprocessing.model.Transaction;
import org.avi1606.financedataprocessing.enums.Category;
import org.avi1606.financedataprocessing.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByIsDeletedFalseOrderByDateDesc();
    List<Transaction> findByTypeAndIsDeletedFalseOrderByDateDesc(TransactionType type);
    List<Transaction> findByCategoryAndIsDeletedFalseOrderByDateDesc(Category category);

    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false AND t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC")
    List<Transaction> findByDateRangeAndIsDeletedFalse(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false AND (:type IS NULL OR t.type = :type) AND (:category IS NULL OR t.category = :category) AND (:startDate IS NULL OR t.date >= :startDate) AND (:endDate IS NULL OR t.date <= :endDate) ORDER BY t.date DESC")
    List<Transaction> findByMultipleCriteria(@Param("type") TransactionType type, @Param("category") Category category, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.isDeleted = false AND t.type = 'INCOME'")
    BigDecimal calculateTotalIncome();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.isDeleted = false AND t.type = 'EXPENSE'")
    BigDecimal calculateTotalExpense();

    boolean existsByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false ORDER BY t.date DESC LIMIT :limit")
    List<Transaction> findRecentTransactions(@Param("limit") int limit);

    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.isDeleted = false")
    Optional<Transaction> findByIdAndNotDeleted(@Param("id") UUID id);
}

