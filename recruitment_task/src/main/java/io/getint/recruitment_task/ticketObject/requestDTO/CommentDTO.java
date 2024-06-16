package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentDTO {
    @JsonProperty("add")
    private AddDTO add;

    public CommentDTO() {}

    private CommentDTO(Builder builder) {
        this.add = builder.add;
    }

    public AddDTO getAdd() {
        return add;
    }

    public void setAdd(AddDTO add) {
        this.add = add;
    }

    public static class Builder {
        private AddDTO add;

        public Builder setAdd(AddDTO add) {
            this.add = add;
            return this;
        }

        public CommentDTO build() {
            return new CommentDTO(this);
        }
    }
}
