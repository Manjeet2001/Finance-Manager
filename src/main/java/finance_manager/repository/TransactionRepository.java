package finance_manager.repository;

import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Query("""
    SELECT t FROM Transaction t 
    WHERE t.user.id = :userId
    AND (:startDate IS NULL OR t.date >= :startDate)
    AND (:endDate IS NULL OR t.date <= :endDate)
    AND (:category IS NULL OR LOWER(t.category.name) = LOWER(:category))
    AND (:type IS NULL OR t.type = :type)
""")
    List<Transaction> filterTransactions(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") String category,
            @Param("type") TransactionType type
    );

}
