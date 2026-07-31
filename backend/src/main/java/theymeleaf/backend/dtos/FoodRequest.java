package theymeleaf.backend.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record FoodRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 60, message = "Name must be 1-60 characters")
        String name,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        @DecimalMax(value = "100000", message = "Quantity is too large")
        BigDecimal qty,

        @NotBlank(message = "Unit is required")
        @Size(min = 1, max = 12, message = "Unit must be 1-12 characters")
        String unit,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0", message = "Price cannot be negative")
        @DecimalMax(value = "100000", message = "Price is too large")
        BigDecimal price
) {}
