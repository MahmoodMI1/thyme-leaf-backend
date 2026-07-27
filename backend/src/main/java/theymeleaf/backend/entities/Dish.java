package theymeleaf.backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import theymeleaf.backend.enums.DishKind;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dishes")
@Getter @Setter
@NoArgsConstructor
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private DishKind kind;
    private String photo;
    private String credit;

    @Column(name = "credit_href")
    private String creditHref;
    private BigDecimal cost;

    @Column(name = "chef_id")
    private long chefId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DishIngredient> ingredients = new ArrayList<>();
}
