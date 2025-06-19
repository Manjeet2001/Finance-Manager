package finance_manager.service;

import finance_manager.dto.TransactionRequest;
import finance_manager.entity.Category;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.CategoryRepository;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction createTransaction(TransactionRequest request, HttpSession session) {
        Long userId = getUserId(session);
        LocalDate date = LocalDate.parse(request.getDate());
        if(date.isAfter(LocalDate.now())) throw new RuntimeException("Date cannot be in future");

        Category category = categoryRepository.findByNameAndUserIdOrIsCustom(request.getCategory(), userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDate(date);
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setUser(userRepository.findById(userId).orElseThrow());
        transaction.setType(category.getType());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(HttpSession session) {
        Long userId = getUserId(session);
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }

    public Transaction updateTransaction(Long id, TransactionRequest request, HttpSession session) {
        Long userId = getUserId(session);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if(!transaction.getUser().getId().equals(userId)) throw new ForbiddenException("Access Denied");

        Category category = categoryRepository.findByNameAndUserIdOrIsCustom(request.getCategory(), userId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setType(category.getType());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id, HttpSession session) {
        Long userId = getUserId(session);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if(!transaction.getUser().getId().equals(userId)) throw new ForbiddenException("Access Denied");
        transactionRepository.delete(transaction);
    }

    public List<Transaction> filterTransactions(String startDate, String endDate, String category, TransactionType type, HttpSession session) {
        Long userId = getUserId(session);
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.MAX;

        return transactionRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId))
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .filter(t -> category == null || t.getCategory().getName().equalsIgnoreCase(category))
                .filter(t -> type == null || t.getType() == type)
                .toList();
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) throw new UnauthorizedException("Access Denied ");
        return userId;
    }

}
