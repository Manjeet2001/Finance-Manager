package finance_manager.repository;

import finance_manager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUserIdOrIsCustom(String name, Long userId);
    List<Category> findByUserIdOrIsCustomFalse(Long userId);

    boolean existsByNameAndUserId(String name, Long userId);
}
