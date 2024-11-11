package ru.practicum.mainservice.location.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull(message = "LAT date cannot be null.")
    private Double lat;

    @NotNull(message = "LON date cannot be null.")
    private Double lon;
}
