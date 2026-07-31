package theymeleaf.backend.mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import theymeleaf.backend.dtos.DishDto;
import theymeleaf.backend.dtos.FoodDto;
import theymeleaf.backend.dtos.IngredientDto;
import theymeleaf.backend.entities.Dish;
import theymeleaf.backend.entities.DishIngredient;
import theymeleaf.backend.entities.FoodItem;
import theymeleaf.backend.enums.Role;
import theymeleaf.backend.repositories.FoodItemRepository;


@Service
public class DishMapper {

    private static final int MONEY_SCALE = 2;
    private static final int DIVISION_SCALE = 6;

    private final FoodItemRepository foodRepo;

    public DishMapper(FoodItemRepository foodRepo) {
        this.foodRepo = foodRepo;
    }

    public FoodDto toDto(FoodItem food) {
        BigDecimal unitPrice = unitPrice(food);

        return new FoodDto(
                food.getId(),
                food.getName(),
                food.getQty(),
                food.getUnit(),
                food.getPrice(),
                unitPrice
        );
    }

    public DishDto toDto(Dish dish, Role role) {
        boolean money = role != Role.STAFF;

        List<IngredientDto> lines = new ArrayList<>();
        for (DishIngredient ingredient : dish.getIngredients()) {
            IngredientDto line = toLine(ingredient);
            lines.add(money ? line
                    : new IngredientDto(line.foodId(), line.name(), line.unit(),
                    line.amount(), null));
        }

        return new DishDto(dish.getId(), dish.getName(), dish.getKind(),
                dish.getPhoto(), dish.getCredit(), dish.getCreditHref(),
                money ? dish.getCost() : null,
                lines);
    }

    private IngredientDto toLine(DishIngredient line) {
        Optional<FoodItem> found = foodRepo.findById(line.getFoodItemId());

        // Case 1: row missing entirely. Should not happen under soft-delete,
        // but no FK means no guarantee — degrade gracefully, never 500.
        if (found.isEmpty()) {
            return new IngredientDto(
                    line.getFoodItemId(),
                    "Unknown",
                    null,
                    line.getAmount(),
                    null
            );
        }

        FoodItem food = found.get();

        // Case 2: food soft-deleted. Name and unit still resolve;
        // price is null — the UI renders this as "No price".
        if (!food.isActive()) {
            return new IngredientDto(
                    food.getId(),
                    food.getUnit() == null ? null : food.getName(),
                    food.getUnit(),
                    line.getAmount(),
                    null
            );
        }

        // Case 3: normal — live cost from current price.
        BigDecimal lineCost = unitPrice(food)
                .multiply(line.getAmount())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        return new IngredientDto(
                food.getId(),
                food.getName(),
                food.getUnit(),
                line.getAmount(),
                lineCost
        );
    }

    private BigDecimal unitPrice(FoodItem food) {
        return food.getPrice().divide(food.getQty(), DIVISION_SCALE, RoundingMode.HALF_UP);
    }
}
