package theymeleaf.backend.dtos;

import java.math.BigDecimal;

public record IngredientDto(Long foodId, String name, String unit,
                            BigDecimal amount, BigDecimal lineCost) {}
