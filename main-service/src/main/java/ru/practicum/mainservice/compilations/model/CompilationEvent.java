package ru.practicum.mainservice.compilations.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainservice.events.model.Event;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations_events")
public class CompilationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Compilation compilation;

    @ManyToOne
    private Event event;
}
