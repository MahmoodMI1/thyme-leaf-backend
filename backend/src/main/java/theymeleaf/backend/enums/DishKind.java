package theymeleaf.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DishKind {
    CANAPES, ENTREES, MAINS, SIDES, SWEETS, TEA, COMPONENTS;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static DishKind fromJson(String value) {
        return valueOf(value.toUpperCase());
    }
}
