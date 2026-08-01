package theymeleaf.backend.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record IngredientRequest(

        @NotNull(message = "Ingredient is required")
        Long foodId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        @DecimalMax(value = "100000", message = "Amount is too large")
        @Digits(integer = 6, fraction = 3, message = "Amount allows at most 3 decimals")
        BigDecimal amount
) {}