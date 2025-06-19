package finance_manager.repository;

import finance_manager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.name = :name AND (c.user.id = :userId OR c.isCustom = true)")
    Optional<Category> findByNameAndUserIdOrIsCustom(String name, Long userId);

    @Query("SELECT c FROM Category c WHERE c.user.id = :userId OR c.isCustom = false")
    List<Category> findByUserIdOrIsCustomFalse(Long userId);

    boolean existsByNameAndUserId(String name, Long userId);
}
