package finance_manager.controller;

import finance_manager.dto.GoalRequest;
import finance_manager.entity.Goal;
import finance_manager.security.SecurityUtils;
import finance_manager.service.GoalService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody GoalRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UsernameNotFoundException("Please login first");
        return ResponseEntity.ok(goalService.createGoal(request, username));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllGoals() {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UsernameNotFoundException("Please login first");
        return ResponseEntity.ok(goalService.getGoalsWithProgress(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@Valid @RequestBody GoalRequest request, @PathVariable Long id) {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UsernameNotFoundException("Please login first");
        return ResponseEntity.ok(goalService.updateGoal(id, request, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable Long id) {
        String username = SecurityUtils.getCurrentUsername();
        if(username == null) throw new UsernameNotFoundException("Please login first");
        goalService.deleteGoal(id, username);
        return ResponseEntity.ok("Goal deleted");
    }
}
