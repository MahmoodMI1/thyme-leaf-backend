package theymeleaf.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import theymeleaf.backend.entities.*;
import theymeleaf.backend.enums.Role;
import theymeleaf.backend.mappers.DishMapper;
import theymeleaf.backend.repositories.DishRepository;
import theymeleaf.backend.repositories.FoodItemRepository;
import theymeleaf.backend.dtos.DishDto;
import theymeleaf.backend.dtos.DishRequest;
import theymeleaf.backend.dtos.IngredientRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DishService {

    private final DishRepository dishRepo;
    private final FoodItemRepository foodRepo;
    private final DishMapper mapper;

    public DishService(DishRepository dishRepo, FoodItemRepository foodRepo, DishMapper mapper) {
        this.dishRepo = dishRepo;
        this.foodRepo = foodRepo;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<DishDto> listDishes() {
        Role role = currentRole();
        return dishRepo.findAll().stream()
                .map(dish -> mapper.toDto(dish, role))
                .toList();
    }

    @Transactional(readOnly = true)
    public DishDto getDish(Long id) {
        Dish dish = dishRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        return mapper.toDto(dish, currentRole());
    }

    @Transactional
    public DishDto create(DishRequest request) {
        // Every ingredient of a NEW dish must reference an active food.
        for (IngredientRequest line : request.ingredients()) {
            requireActive(line.foodId());
        }

        Dish dish = new Dish();
        applyRequest(dish, request);

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dish.setChefId(current.getId());

        return mapper.toDto(dishRepo.save(dish), currentRole());
    }

    @Transactional
    public DishDto update(Long id, DishRequest request) {
        Dish dish = dishRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));

        // Surviving lines may reference deactivated foods; NEWLY ADDED lines must be active.
        Set<Long> survivors = new HashSet<>();
        for (DishIngredient existing : dish.getIngredients()) {
            survivors.add(existing.getFoodItemId());
        }

        for (IngredientRequest line : request.ingredients()) {
            if (!survivors.contains(line.foodId())) {
                requireActive(line.foodId());
            }
        }

        applyRequest(dish, request);   // full replace
        return mapper.toDto(dishRepo.save(dish), currentRole());
    }

    @Transactional
    public void delete(Long id) {
        if (!dishRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found");
        }
        dishRepo.deleteById(id);   // hard delete; SQL cascade + orphanRemoval sweep the lines
    }

    // ---- helpers ----

    /** Full-replace the dish's fields and lines from the request, then recompute the cost cache. */
    private void applyRequest(Dish dish, DishRequest request) {
        dish.setName(request.name().trim());
        dish.setKind(request.type());
        dish.setPhoto(request.photo());
        dish.setCredit(request.credit());
        dish.setCreditHref(request.creditHref());

        dish.getIngredients().clear();          // orphanRemoval deletes the old rows
        for (IngredientRequest line : request.ingredients()) {
            DishIngredient ingredient = new DishIngredient();
            ingredient.setDish(dish);
            ingredient.setFoodItemId(line.foodId());
            ingredient.setAmount(line.amount());
            dish.getIngredients().add(ingredient);
        }

        dish.setCost(computeCost(request.ingredients()));
    }

    /** Server-side cost cache: sum of priceable lines only. Client-sent costs are never read. */
    private BigDecimal computeCost(List<IngredientRequest> lines) {
        BigDecimal total = BigDecimal.ZERO;

        for (IngredientRequest line : lines) {
            FoodItem food = foodRepo.findById(line.foodId()).orElse(null);
            if (food == null || !food.isActive()) continue;   // unpriceable line — excluded

            BigDecimal lineCost = food.getPrice()
                    .divide(food.getQty(), 6, RoundingMode.HALF_UP)
                    .multiply(line.amount());
            total = total.add(lineCost);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void requireActive(Long foodId) {
        boolean active = foodRepo.findById(foodId)
                .map(FoodItem::isActive)
                .orElse(false);

        if (!active) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ingredient " + foodId + " is not available");
        }
    }

    private Role currentRole() {
        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return current.getRole();
    }
}