package soon.capstone.infrastructure.github.service.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KanbanStatus {
    TODO("Todo", "BLUE"),
    IN_PROGRESS("In Progress", "YELLOW"),
    DONE("Done", "GREEN")
    ;

    private final String name;
    private final String color;

}