package finance_manager.service;

import finance_manager.dto.TransactionRequest;
import finance_manager.entity.Category;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.entity.User;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.ResourceNotFoundException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.CategoryRepository;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import finance_manager.security.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public Transaction createTransaction(TransactionRequest request, String username) {
        User user = getUserByUserName(username);

        LocalDate date = LocalDate.parse(request.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(date.isAfter(LocalDate.now())) throw new RuntimeException("Date cannot be in future");

        Category category = categoryRepository.findByNameAndUserIdOrIsCustom(request.getCategory(), user.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDate(date);
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setUser(userRepository.findById(user.getId()).orElseThrow());
        transaction.setType(category.getType());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }

    public Transaction updateTransaction(Long id, TransactionRequest request, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if(!transaction.getUser().getId().equals(userId)) throw new ForbiddenException("Access Denied");

        Category category = categoryRepository.findByNameAndUserIdOrIsCustom(request.getCategory(), userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setType(category.getType());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if(!transaction.getUser().getId().equals(userId)) throw new ForbiddenException("Access Denied");
        transactionRepository.delete(transaction);
    }

    public List<Transaction> filterTransactions(String startDate, String endDate, String category, TransactionType type, String username) {
        User user = getUserByUserName(username);
        Long userId = user.getId();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.MAX;

        return transactionRepository.filterTransactions(userId, start, end, category, type);

    }

    private User getUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("User not found for username: " + username));
    }

}
