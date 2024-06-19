package io.getint.recruitment_task.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Issue {
    private String id;
    private String key;
    private String summary;
    private String description;
    private Priority priority;
    private String status;
}
