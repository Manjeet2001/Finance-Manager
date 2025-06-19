package finance_manager.service;

import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.entity.User;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;


    public Map<String, Object> generateMonthlyReport(int year, int month, HttpSession session) {
        Long userId = getUserId(session);
        List<Transaction> transactions =
                transactionRepository.findByUserIdOrderByDateDesc(userId);

        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        double totalIncome = 0, totalExpense = 0;

        for (Transaction t : transactions) {
            if(t.getDate().getYear() == year && t.getDate().getMonthValue() == month){
                String category = t.getCategory().getName();
                if(t.getType() == TransactionType.INCOME){
                    incomeByCategory.put(category, incomeByCategory.getOrDefault(category, 0.0) + t.getAmount());
                    totalIncome += t.getAmount();
                }
                else if(t.getType() == TransactionType.EXPENSE){
                    expenseByCategory.put(category, expenseByCategory.getOrDefault(category, 0.0) + t.getAmount());
                    totalExpense += t.getAmount();
                }
            }
        }
        Map<String, Object> report = new HashMap<>();
        report.put("month", month);
        report.put("year", year);
        report.put("totalIncome", incomeByCategory);
        report.put("totalExpense", expenseByCategory);
        report.put("netSaving", totalIncome - totalExpense);
        return report;
    }

    public Map<String, Object> generateYearlyReport(int year, HttpSession session) {
        Long userId = getUserId(session);
        List<Transaction> transactions =
                transactionRepository.findByUserIdOrderByDateDesc(userId);

        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        double totalIncome = 0, totalExpense = 0;

        for (Transaction t : transactions) {
            if(t.getDate().getYear() == year){
                String category = t.getCategory().getName();
                if(t.getType() == TransactionType.INCOME){
                    incomeByCategory.put(category, incomeByCategory.getOrDefault(category, 0.0) + t.getAmount());
                    totalIncome += t.getAmount();
                }
                else if(t.getType() == TransactionType.EXPENSE){
                    expenseByCategory.put(category, expenseByCategory.getOrDefault(category, 0.0) + t.getAmount());
                    totalExpense += t.getAmount();
                }
            }
        }
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("totalIncome", incomeByCategory);
        report.put("totalExpense", expenseByCategory);
        report.put("netSaving", totalIncome - totalExpense);
        return report;
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) throw new UnauthorizedException("Access Denied ");
        return userId;
    }
}
