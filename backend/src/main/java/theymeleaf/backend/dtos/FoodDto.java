package theymeleaf.backend.dtos;

import java.math.BigDecimal;

public record FoodDto(Long id, String name, BigDecimal qty, String unit,
                      BigDecimal price, BigDecimal unitPrice) {}
