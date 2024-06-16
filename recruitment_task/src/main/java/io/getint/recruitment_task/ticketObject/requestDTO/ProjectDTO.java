package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDTO {
    @JsonProperty("key")
    private String key;

    public ProjectDTO() {}

    private ProjectDTO(Builder builder) {
        this.key = builder.key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static class Builder {
        private String key;

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public ProjectDTO build() {
            return new ProjectDTO(this);
        }
    }
}
