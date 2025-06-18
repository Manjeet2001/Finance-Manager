package finance_manager.dto;

import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class CategoryRequest {

    @NotBlank
    private String name;

    @NonNull
    private TransactionType type;

}
