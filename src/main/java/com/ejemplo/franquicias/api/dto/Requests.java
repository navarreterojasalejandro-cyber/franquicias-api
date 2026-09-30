package com.ejemplo.franquicias.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class Requests {
    private Requests() { }

    public record NameRequest(@NotBlank(message = "El nombre es obligatorio") String name) { }
    public record ProductRequest(@NotBlank(message = "El nombre es obligatorio") String name,
                                 @NotNull(message = "El stock es obligatorio")
                                 @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) { }
    public record StockRequest(@NotNull(message = "El stock es obligatorio")
                               @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) { }
}
