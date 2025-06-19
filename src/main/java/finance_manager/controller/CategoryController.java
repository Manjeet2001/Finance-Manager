package finance_manager.controller;

import finance_manager.dto.CategoryRequest;
import finance_manager.entity.Category;
import finance_manager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCustomCategory(@RequestBody CategoryRequest request, HttpSession session) {
        return ResponseEntity.ok(categoryService.createCustomCategory(request, session));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getUserCategories(HttpSession session) {
        return ResponseEntity.ok(categoryService.getUserCategories(session));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteCustomCategory(@PathVariable String name, HttpSession session) {
        categoryService.deleteCustomCategory(name, session);
        return ResponseEntity.ok("Category Deleted with name " + name);
    }

}
