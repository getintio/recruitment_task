package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldsDTO {
    @JsonProperty("project")
    private ProjectDTO project;
    @JsonProperty("description")
    private String description;
    @JsonProperty("summary")
    private String summary;
    @JsonProperty("priority")
    private PriorityDTO priority;
    @JsonProperty("issuetype")
    private IssueTypeDTO issueType;

    public FieldsDTO() {
    }

    private FieldsDTO(Builder builder) {
        this.project = builder.project;
        this.description = builder.description;
        this.summary = builder.summary;
        this.priority = builder.priority;
        this.issueType = builder.issueType;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public String getDescription() {
        return description;
    }

    public String getSummary() {
        return summary;
    }

    public PriorityDTO getPriority() {
        return priority;
    }

    public IssueTypeDTO getIssueType() {
        return issueType;
    }

    public static class Builder {
        private ProjectDTO project;
        private String description;
        private String summary;
        private PriorityDTO priority;
        private IssueTypeDTO issueType;

        public Builder setProject(ProjectDTO project) {
            this.project = project;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder setPriority(PriorityDTO priority) {
            this.priority = priority;
            return this;
        }

        public Builder setIssueType(IssueTypeDTO issueType) {
            this.issueType = issueType;
            return this;
        }

        public FieldsDTO build() {
            return new FieldsDTO(this);
        }
    }
}
