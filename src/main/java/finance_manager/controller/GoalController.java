package finance_manager.controller;

import finance_manager.dto.GoalRequest;
import finance_manager.entity.Goal;
import finance_manager.service.GoalService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/goals")
public class GoalController {
    @Autowired
    private GoalService goalService;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody GoalRequest request, HttpSession session) {
        return ResponseEntity.ok(goalService.createGoal(request, session));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllGoals(HttpSession session) {
        return ResponseEntity.ok(goalService.getGoalsWithProgress(session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@Valid @RequestBody GoalRequest request, @PathVariable Long id, HttpSession session) {
        return ResponseEntity.ok(goalService.updateGoal(id, request, session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable Long id, HttpSession session) {
        goalService.deleteGoal(id, session);
        return ResponseEntity.ok("Goal deleted");
    }
}
