package theymeleaf.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import theymeleaf.backend.entities.FoodItem;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByActiveTrueOrderByNameAsc();
    Optional<FoodItem> findByNameIgnoreCase(String name);
}
