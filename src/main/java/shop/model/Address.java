package shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String county;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    public Address(String country, String city, String county, String streetAddress) {
        this.country = ModelValidation.requiredText(country, 100, "Country");
        this.city = ModelValidation.requiredText(city, 100, "City");
        this.county = ModelValidation.optionalText(county, 100, "County");
        this.streetAddress = ModelValidation.requiredText(streetAddress, 255, "Street address");
    }
}
