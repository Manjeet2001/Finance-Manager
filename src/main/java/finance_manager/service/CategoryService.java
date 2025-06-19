package finance_manager.service;

import finance_manager.dto.CategoryRequest;
import finance_manager.entity.Category;
import finance_manager.exception.custom_exception.ConflictException;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.CategoryRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public Category createCustomCategory(CategoryRequest request, HttpSession session) {
        Long userId = getUserId(session);

        if(categoryRepository.existsByNameAndUserId(request.getName(), userId))
            throw new ConflictException("Category already exists");

        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setCustom(true);
        category.setUser(userRepository.findById(userId).orElseThrow());

        return categoryRepository.save(category);
    }

    public List<Category> getUserCategories(HttpSession session) {
        Long userId = getUserId(session);
        return categoryRepository.findByUserIdOrIsCustomFalse(userId);
    }

    public void deleteCustomCategory(String name, HttpSession session) {
        Long userId = getUserId(session);
        Category category = categoryRepository.findByNameAndUserIdOrIsCustom(name, userId)
                .orElseThrow(()-> new UnauthorizedException("category not found"));

        if(!category.isCustom()) throw new ForbiddenException("cannot delete default Category");
        categoryRepository.delete(category);
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) throw new UnauthorizedException("Access denied");
        return userId;
    }
}
