package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IssueTypeDTO {
    @JsonProperty("name")
    private String name;

    public IssueTypeDTO() {}

    private IssueTypeDTO(Builder builder) {
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public IssueTypeDTO build() {
            return new IssueTypeDTO(this);
        }
    }
}