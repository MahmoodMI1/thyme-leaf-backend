package theymeleaf.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import theymeleaf.backend.dtos.DishDto;
import theymeleaf.backend.dtos.DishRequest;
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

    @GetMapping("/{id}")
    public DishDto get(@PathVariable Long id) {
        return dishService.getDish(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DishDto create(@Valid @RequestBody DishRequest request) {
        return dishService.create(request);
    }

    @PutMapping("/{id}")
    public DishDto update(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        return dishService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        dishService.delete(id);
    }

}
