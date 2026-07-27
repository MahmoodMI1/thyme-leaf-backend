package theymeleaf.backend.services;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import theymeleaf.backend.dtos.FoodDto;
import theymeleaf.backend.mappers.DishMapper;
import theymeleaf.backend.repositories.FoodItemRepository;

import java.util.List;

@Service
public class FoodService {
    final private FoodItemRepository foodItemRepo;
    final private DishMapper dishMapper;

    public FoodService(FoodItemRepository foodItemRepo, DishMapper dishMapper) {
        this.foodItemRepo = foodItemRepo;
        this.dishMapper = dishMapper;
    }

    public List<FoodDto> listFoods() {
        return foodItemRepo.findByActiveTrueOrderByNameAsc().stream()
                .map(dishMapper::toDto)
                .toList();
    }


}
