package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PriorityDTO {
    @JsonProperty("id")
    private String id;

    public PriorityDTO() {}

    private PriorityDTO(Builder builder) {
        this.id = builder.id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Builder {
        private String id;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public PriorityDTO build() {
            return new PriorityDTO(this);
        }
    }
}