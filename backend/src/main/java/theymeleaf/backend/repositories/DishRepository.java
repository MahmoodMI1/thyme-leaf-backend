package theymeleaf.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import theymeleaf.backend.entities.Dish;

import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> findByNameIgnoreCase(String name);

}
