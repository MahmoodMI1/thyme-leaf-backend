package theymeleaf.backend.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theymeleaf.backend.dtos.DishDto;
import theymeleaf.backend.entities.User;
import theymeleaf.backend.mappers.DishMapper;
import theymeleaf.backend.repositories.DishRepository;

import java.util.List;

@Service
public class DishService {
    final private DishRepository dishRepository;
    final private DishMapper dishMapper;


    public DishService(DishRepository dishRepository, DishMapper dishMapper) {
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
    }
    @Transactional(readOnly = true)
    public List<DishDto> listDishes() {
        User current = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return dishRepository.findAll().stream()
                .map(dish -> dishMapper.toDto(dish, current.getRole()))
                .toList();
    }


}
