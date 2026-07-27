package theymeleaf.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theymeleaf.backend.dtos.DishDto;
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
        return dishRepository.findAll().stream()
                .map(dishMapper::toDto)
                .toList();
    }


}
