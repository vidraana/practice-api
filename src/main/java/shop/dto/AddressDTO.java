package shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDTO(

        @NotBlank
        @Size(max = 100)
        String country,

        @NotBlank
        @Size(max = 100)
        String city,

        @Size(max = 100)
        String county,

        @NotBlank
        @Size(max = 255)
        String streetAddress
) {
}