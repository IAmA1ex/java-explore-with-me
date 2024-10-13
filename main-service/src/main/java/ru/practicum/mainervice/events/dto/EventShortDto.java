package ru.practicum.mainervice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainervice.categories.dto.CategoryDto;
import ru.practicum.mainervice.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private Long views;
}
