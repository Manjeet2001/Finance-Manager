package finance_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Date is required")
    private String date;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;
}
