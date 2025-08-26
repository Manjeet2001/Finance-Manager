package finance_manager.service;

import finance_manager.dto.CategoryRequest;
import finance_manager.entity.Category;
import finance_manager.entity.User;
import finance_manager.exception.custom_exception.ConflictException;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.ResourceNotFoundException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.CategoryRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public Category createCustomCategory(CategoryRequest request, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        if(categoryRepository.existsByNameAndUserId(request.getName(), userId))
            throw new ConflictException("Category already exists");

        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setCustom(true);
        category.setUser(userRepository.findById(userId).orElseThrow());

        return categoryRepository.save(category);
    }

    public List<Category> getUserCategories(String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        return categoryRepository.findByUserIdOrIsCustomFalse(userId);
    }

    public void deleteCustomCategory(String name, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        Category category = categoryRepository.findByNameAndUserIdOrIsCustom(name, userId)
                .orElseThrow(()-> new ResourceNotFoundException("category not found"));

        if(!category.isCustom()) throw new ForbiddenException("cannot delete default Category");
        categoryRepository.delete(category);
    }

    private User getUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("User not found for username: " + username));
    }
}
