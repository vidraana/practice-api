package shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product_category")
public class ProductCategory extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    public ProductCategory(String name, String description) {
        setName(name);
        setDescription(description);
    }

    public void setName(String name) {
        this.name = ModelValidation.requiredText(name, 100, "Category name");
    }

    public void setDescription(String description) {
        this.description = ModelValidation.optionalText(description, 500, "Category description");
    }
}
