package finance_manager.service;

import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.entity.User;
import finance_manager.exception.custom_exception.ResourceNotFoundException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;


    public Map<String, Object> generateMonthlyReport(int year, int month, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .toList();

        return buildReport(transactions, year, month);
    }

    public Map<String, Object> generateYearlyReport(int year, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .filter(t -> t.getDate().getYear() == year)
                .toList();

        return buildReport(transactions, year, null);
    }

    private Map<String, Object> buildReport(List<Transaction> transactions, int year, Integer month) {
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        double totalIncome = 0, totalExpense = 0;

        for (Transaction t : transactions) {
            String category = t.getCategory().getName();
            if (t.getType() == TransactionType.INCOME) {
                incomeByCategory.put(category, incomeByCategory.getOrDefault(category, 0.0) + t.getAmount());
                totalIncome += t.getAmount();
            } else if (t.getType() == TransactionType.EXPENSE) {
                expenseByCategory.put(category, expenseByCategory.getOrDefault(category, 0.0) + t.getAmount());
                totalExpense += t.getAmount();
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        if (month != null) {
            report.put("month", month);
        }
        report.put("totalIncome", incomeByCategory);
        report.put("totalExpense", expenseByCategory);
        report.put("netSaving", totalIncome - totalExpense);

        return report;
    }

    private User getUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("User not found for username: " + username));
    }
}
