package theymeleaf.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngredientDto(Long foodId, String name, String unit,
                            BigDecimal amount, BigDecimal lineCost) {}
