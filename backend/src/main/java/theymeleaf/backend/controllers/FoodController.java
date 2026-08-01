package theymeleaf.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import theymeleaf.backend.dtos.FoodDto;
import theymeleaf.backend.dtos.FoodRequest;
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodDto create(@Valid @RequestBody FoodRequest request) {
        return foodService.create(request);
    }

    @PatchMapping("/{id}")
    public FoodDto patch(@PathVariable Long id, @Valid @RequestBody FoodRequest request) {
        return foodService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        foodService.delete(id);
    }

}
