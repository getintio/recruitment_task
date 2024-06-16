package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraCreateRequestDTO {
    @JsonProperty("fields")
    private FieldsDTO fields;

    public JiraCreateRequestDTO() {}

    private JiraCreateRequestDTO(Builder builder) {
        this.fields = builder.fields;
    }

    public FieldsDTO getFields() {
        return fields;
    }

    public void setFields(FieldsDTO fields) {
        this.fields = fields;
    }

    public static class Builder {
        private FieldsDTO fields;

        public Builder setFields(FieldsDTO fields) {
            this.fields = fields;
            return this;
        }

        public JiraCreateRequestDTO build() {
            return new JiraCreateRequestDTO(this);
        }
    }
}
