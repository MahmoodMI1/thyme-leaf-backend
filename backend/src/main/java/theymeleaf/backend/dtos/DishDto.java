package theymeleaf.backend.dtos;

import theymeleaf.backend.enums.DishKind;

import java.math.BigDecimal;
import java.util.List;

public record DishDto(Long id, String name, DishKind type, String photo,
                      String credit, String creditHref, BigDecimal cost,
                      List<IngredientDto> ingredients) {}
