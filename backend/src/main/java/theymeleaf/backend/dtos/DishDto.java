package theymeleaf.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import theymeleaf.backend.enums.DishKind;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DishDto(Long id, String name, DishKind type, String photo,
                      String credit, String creditHref, BigDecimal cost,
                      List<IngredientDto> ingredients) {}
