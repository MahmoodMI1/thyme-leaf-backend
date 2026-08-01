package theymeleaf.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import theymeleaf.backend.entities.FoodItem;
import theymeleaf.backend.mappers.DishMapper;
import theymeleaf.backend.repositories.FoodItemRepository;
import theymeleaf.backend.dtos.FoodDto;
import theymeleaf.backend.dtos.FoodRequest;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    private final FoodItemRepository repo;
    private final DishMapper mapper;

    public FoodService(FoodItemRepository repo, DishMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<FoodDto> listFoods() {
        return repo.findByActiveTrueOrderByNameAsc().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public FoodDto create(FoodRequest request) {
        String name = request.name().trim();
        String unit = request.unit().trim();

        Optional<FoodItem> existing = repo.findByNameIgnoreCase(name);

        // Branch 1: an active food already owns this name
        if (existing.isPresent() && existing.get().isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A food with this name already exists");
        }

        // Branch 2: a soft-deleted food owns it — revive on the same id.
        // Every dish line still pointing at this id heals instantly.
        if (existing.isPresent()) {
            FoodItem revived = existing.get();
            revived.setName(name);
            revived.setQty(request.qty());
            revived.setUnit(unit);
            revived.setPrice(request.price());
            revived.setActive(true);
            return mapper.toDto(repo.save(revived));
        }

        // Branch 3: fresh insert
        FoodItem food = new FoodItem();
        food.setName(name);
        food.setQty(request.qty());
        food.setUnit(unit);
        food.setPrice(request.price());
        return mapper.toDto(repo.save(food));
    }

    @Transactional
    public FoodDto patch(Long id, FoodRequest request) {
        FoodItem food = repo.findById(id)
                .filter(FoodItem::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Food not found"));

        String name = request.name().trim();

        // Renaming onto another active food's name is a conflict
        Optional<FoodItem> collision = repo.findByNameIgnoreCase(name);
        if (collision.isPresent()
                && collision.get().isActive()
                && !collision.get().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A food with this name already exists");
        }

        food.setName(name);
        food.setQty(request.qty());
        food.setUnit(request.unit().trim());
        food.setPrice(request.price());
        return mapper.toDto(repo.save(food));
    }

    @Transactional
    public void delete(Long id) {
        FoodItem food = repo.findById(id)
                .filter(FoodItem::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Food not found"));

        food.setActive(false);
        repo.save(food);
    }
}