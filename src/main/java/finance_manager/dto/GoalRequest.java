package finance_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GoalRequest {

    @NotBlank(message = "goalName cannot be blank")
    private String goalName;

    @Positive(message = "goalAmount must be greater than zero")
    private Double goalAmount;

    @NotNull(message = "targetDate cannot be blank")
    private String targetDate;

}
