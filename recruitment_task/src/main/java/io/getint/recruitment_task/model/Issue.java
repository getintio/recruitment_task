package io.getint.recruitment_task.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Issue {
    private String issueTypeId;
    private String key;
    private String summary;
    private JsonNode description;
    private Priority priority;
    private String status;
}
