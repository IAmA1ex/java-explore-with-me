package ru.practicum.mainservice.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.events.dto.UpdateEventRequest;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.exception.errors.NotFoundException;

@Component
@RequiredArgsConstructor
public class ServiceGeneralFunctionality {

    private final CategoryRepository categoryRepository;

    public void updateEvent(Event event, UpdateEventRequest eventUpdate) {
        if (eventUpdate.getAnnotation() != null) event.setAnnotation(eventUpdate.getAnnotation());
        if (eventUpdate.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(() ->
                    new NotFoundException("There is no such category.",
                            "Category with id = " + eventUpdate.getCategory() + " does not exist."));
            event.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) event.setDescription(eventUpdate.getDescription());
        if (eventUpdate.getLocation() != null) {
            event.setLat(eventUpdate.getLocation().getLat());
            event.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) event.setPaid(eventUpdate.getPaid());
        if (eventUpdate.getParticipantLimit() != null) event.setParticipantLimit(eventUpdate.getParticipantLimit());
        if (eventUpdate.getRequestModeration() != null) event.setRequestModeration(eventUpdate.getRequestModeration());
        if (eventUpdate.getTitle() != null) event.setTitle(eventUpdate.getTitle());
    }
}
