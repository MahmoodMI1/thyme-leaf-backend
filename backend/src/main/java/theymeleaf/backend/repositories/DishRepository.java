package theymeleaf.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import theymeleaf.backend.entities.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {

}
