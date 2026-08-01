package theymeleaf.backend.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import theymeleaf.backend.enums.DishKind;

import java.util.List;

public record DishRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 60, message = "Name must be 1-60 characters")
        String name,

        @NotNull(message = "Course is required")
        DishKind type,

        String photo,

        @Size(max = 120, message = "Credit is too long")
        String credit,

        String creditHref,

        @NotEmpty(message = "A dish needs at least one ingredient")
        @Valid
        List<IngredientRequest> ingredients
) {}