package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddDTO {
    @JsonProperty("body")
    private String body;

    public AddDTO() {}

    private AddDTO(Builder builder) {
        this.body = builder.body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static class Builder {
        private String body;

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public AddDTO build() {
            return new AddDTO(this);
        }
    }
}
