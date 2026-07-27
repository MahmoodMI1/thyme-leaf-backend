package theymeleaf.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theymeleaf.backend.dtos.DishDto;
import theymeleaf.backend.services.DishService;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<DishDto> list(){
        return dishService.listDishes();
    }

}
