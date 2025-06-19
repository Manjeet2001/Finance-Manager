package finance_manager;

import finance_manager.dto.*;
import finance_manager.entity.TransactionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

// Category
    @Test
    void validCategoryRequest_shouldPass() {
        CategoryRequest dto = new CategoryRequest();
        dto.setName("Food");
        dto.setType(TransactionType.EXPENSE);

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidCategoryRequest_missingName_shouldFail() {
        CategoryRequest dto = new CategoryRequest();
        dto.setName("");
        dto.setType(TransactionType.INCOME);

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

//UserSignUp
    @Test
    void validUserSignUpRequest_shouldPass() {
        UserSignUpRequest dto = new UserSignUpRequest(
                "john@example.com",
                "password123",
                "John Doe",
                "+1234567890"
        );

        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUserSignUpRequest_invalidEmail_shouldFail() {
        UserSignUpRequest dto = new UserSignUpRequest(
                "invalid-email",
                "12345",
                "",
                "notaphone"
        );

        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(dto);
        assertEquals(4, violations.size()); // All fields invalid
    }

//userLogin
    @Test
    void validUserLoginRequest_shouldPass() {
        UserLoginRequest dto = new UserLoginRequest("john@example.com", "secret");

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUserLoginRequest_blankPassword_shouldFail() {
        UserLoginRequest dto = new UserLoginRequest("john@example.com", "");

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

// Transaction
    @Test
    void validTransactionRequest_shouldPass() {
        TransactionRequest dto = new TransactionRequest();
        dto.setAmount(100.0);
        dto.setDate("2024-01-01");
        dto.setCategory("Food");
        dto.setDescription("Groceries");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidTransactionRequest_negativeAmount_shouldFail() {
        TransactionRequest dto = new TransactionRequest();
        dto.setAmount(-10.0);
        dto.setDate("");
        dto.setCategory(null);

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

// Goal Tests
    @Test
    void validGoalRequest_shouldPass() {
        GoalRequest dto = new GoalRequest();
        dto.setGoalName("Emergency Fund");
        dto.setGoalAmount(5000.0);
        dto.setTargetDate("2026-12-31");

        Set<ConstraintViolation<GoalRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidGoalRequest_missingFields_shouldFail() {
        GoalRequest dto = new GoalRequest();
        dto.setGoalName("");
        dto.setGoalAmount(0.0);
        dto.setTargetDate(null);

        Set<ConstraintViolation<GoalRequest>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }
}
