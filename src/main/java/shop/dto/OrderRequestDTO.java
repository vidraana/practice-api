package shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(

        @NotNull UUID userId,

        @NotNull LocalDateTime createdAt,

        @NotNull @Valid AddressDTO address,

        @NotEmpty List<@NotNull @Valid Item> items

) {
    public record Item(
            @NotNull UUID productId,
            @Positive int quantity
    ) {
    }
}