package theymeleaf.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theymeleaf.backend.dtos.FoodDto;
import theymeleaf.backend.services.FoodService;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    final private FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<FoodDto> list(){
        return foodService.listFoods();
    }

}
