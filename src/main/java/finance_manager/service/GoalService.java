package finance_manager.service;

import finance_manager.dto.GoalRequest;
import finance_manager.entity.Goal;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.ResourceNotFoundException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.GoalRepository;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Goal createGoal(GoalRequest request, HttpSession session) {
        Long userId = getUserId(session);
        LocalDate date = LocalDate.parse(request.getTargetDate(),
                java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        if(date.isBefore(LocalDate.now()) ) throw new RuntimeException("Target date must be in future");

        Goal goal = new Goal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getGoalAmount());
        goal.setTargetDate(date);
        goal.setStartDate(LocalDate.now());
        goal.setUser(userRepository.findById(userId).orElseThrow());
        return goalRepository.save(goal);
    }

    public List<Map<String, Object>> getGoalsWithProgress(HttpSession session){
        Long userId = getUserId(session);
        List<Goal> goals = goalRepository.findByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for(Goal goal : goals){
            double income = transactions.stream()
                    .filter(t-> t.getDate().isAfter(goal.getStartDate().minusDays(1)))
                    .filter(t-> t.getType() == TransactionType.INCOME)
                    .mapToDouble(Transaction::getAmount).sum();

            double expense = transactions.stream()
                    .filter(t->t.getDate().isAfter(goal.getStartDate().minusDays(1)))
                    .filter(t-> t.getType() == TransactionType.EXPENSE)
                    .mapToDouble(Transaction::getAmount).sum();

            double progress = income - expense;
            double percentage = Math.min((progress / goal.getTargetAmount()) * 100, 100);
            double remaining = Math.max(goal.getTargetAmount() - progress, 0);

            Map<String, Object> goalMap = new HashMap<>();
            goalMap.put("id", goal.getId());
            goalMap.put("goalName", goal.getGoalName());
            goalMap.put("targetDate", goal.getTargetDate());
            goalMap.put("targetAmount", goal.getTargetAmount());
            goalMap.put("startDate", goal.getStartDate());
            goalMap.put("currentProgress", progress);
            goalMap.put("remainingProgress", remaining);
            goalMap.put("currentPercentage", percentage);

            result.add(goalMap);
        }
        return result;
    }

    public Goal updateGoal(Long id, GoalRequest request, HttpSession session) {
        Long userId = getUserId(session);
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if(!goal.getUser().getId().equals(userId)) throw new ForbiddenException("Access Denied");

        goal.setTargetAmount(request.getGoalAmount());
        goal.setTargetDate(LocalDate.parse(request.getTargetDate()));

        return goalRepository.save(goal);
    }

    public void deleteGoal(Long id, HttpSession session) {
        Long userId = getUserId(session);
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if(!goal.getUser().getId().equals(userId)) throw new ForbiddenException("Access Denied");
        goalRepository.delete(goal);
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) throw new UnauthorizedException("Access Denied ");
        return userId;
    }
}
