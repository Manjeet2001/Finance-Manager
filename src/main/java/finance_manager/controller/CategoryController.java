package finance_manager.controller;

import finance_manager.dto.CategoryRequest;
import finance_manager.entity.Category;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.security.SecurityUtils;
import finance_manager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCustomCategory(@RequestBody CategoryRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UnauthorizedException("Please login first");
        return ResponseEntity.ok(categoryService.createCustomCategory(request, username));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getUserCategories() {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UnauthorizedException("Please login first");
        return ResponseEntity.ok(categoryService.getUserCategories(username));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteCustomCategory(@PathVariable String name) {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UnauthorizedException("Please login first");
        categoryService.deleteCustomCategory(name, username);
        return ResponseEntity.ok("Category Deleted with name " + name);
    }
}
