package finance_manager.service;

import finance_manager.dto.TransactionRequest;
import finance_manager.entity.Category;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.entity.User;
import finance_manager.exception.custom_exception.ForbiddenException;
import finance_manager.exception.custom_exception.UnauthorizedException;
import finance_manager.repository.CategoryRepository;
import finance_manager.repository.TransactionRepository;
import finance_manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    private final Long USER_ID = 1L;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);
        user.setUsername("john@example.com");

        category = new Category();
        category.setId(10L);
        category.setName("Food");
        category.setCustom(true);
        category.setType(TransactionType.EXPENSE);
        category.setUser(user);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAmount(200.0);
        request.setDate("2024-06-15");
        request.setCategory("Food");
        request.setDescription("Lunch");

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(categoryRepository.findByNameAndUserIdOrIsCustom("Food", USER_ID)).thenReturn(Optional.of(category));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = transactionService.createTransaction(request, session);

        assertNotNull(result);
        assertEquals(200.0, result.getAmount());
        assertEquals("Lunch", result.getDescription());
        assertEquals(TransactionType.EXPENSE, result.getType());
        verify(transactionRepository).save(txCaptor.capture());
        assertEquals(LocalDate.of(2024, 6, 15), txCaptor.getValue().getDate());
    }

    @Test
    void shouldThrowIfDateInFuture() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setDate(LocalDate.now().plusDays(1).toString()); // Future date
        request.setCategory("Food");

        when(session.getAttribute("userId")).thenReturn(USER_ID);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(request, session)
        );

        assertEquals("Date cannot be in future", exception.getMessage());
    }

    @Test
    void shouldGetUserTransactions() {
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(transactionRepository.findByUserIdOrderByDateDesc(USER_ID)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(session);
        assertEquals(2, result.size());
    }

    @Test
    void shouldUpdateTransactionSuccessfully() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(500.0);
        request.setDescription("Updated");
        request.setCategory("Food");

        Transaction existing = new Transaction();
        existing.setId(1L);
        existing.setUser(user);
        existing.setCategory(category);
        existing.setAmount(100.0);
        existing.setDescription("Old");

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByNameAndUserIdOrIsCustom("Food", USER_ID)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = transactionService.updateTransaction(1L, request, session);
        assertEquals(500.0, result.getAmount());
        assertEquals("Updated", result.getDescription());
    }

    @Test
    void shouldThrowForbiddenExceptionOnUpdate() {
        User anotherUser = new User();
        anotherUser.setId(99L);

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setUser(anotherUser);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        assertThrows(ForbiddenException.class, () ->
                transactionService.updateTransaction(1L, new TransactionRequest(), session)
        );
    }

    @Test
    void shouldDeleteTransactionSuccessfully() {
        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setUser(user);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        transactionService.deleteTransaction(1L, session);

        verify(transactionRepository).delete(tx);
    }

    @Test
    void shouldThrowForbiddenExceptionOnDelete() {
        User anotherUser = new User();
        anotherUser.setId(999L);

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setUser(anotherUser);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        assertThrows(ForbiddenException.class, () ->
                transactionService.deleteTransaction(1L, session)
        );
    }

    @Test
    void shouldFilterTransactions() {
        Transaction tx1 = new Transaction();
        tx1.setUser(user);
        tx1.setDate(LocalDate.of(2024, 1, 10));
        tx1.setCategory(category);
        tx1.setType(TransactionType.EXPENSE);

        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(transactionRepository.findAll()).thenReturn(List.of(tx1));

        List<Transaction> result = transactionService.filterTransactions(
                "2024-01-01", "2024-01-31", "Food", TransactionType.EXPENSE, session
        );

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowUnauthorizedIfNoSession() {
        when(session.getAttribute("userId")).thenReturn(null);

        assertThrows(UnauthorizedException.class, () ->
                transactionService.getUserTransactions(session)
        );
    }
}

