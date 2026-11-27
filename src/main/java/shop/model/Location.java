package shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "location")
public class Location extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private Address address;

    public Location(String name, Address address) {
        setName(name);
        setAddress(address);
    }

    public void setName(String name) {
        this.name = ModelValidation.requiredText(name, 100, "Location name");
    }

    public void setAddress(Address address) {
        this.address = Objects.requireNonNull(address, "Address is required");
    }
}
