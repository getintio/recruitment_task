package io.getint.recruitment_task.ticketObject.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UpdateDTO {
    @JsonProperty("comment")
    private List<CommentDTO> comments;

    public UpdateDTO() {}

    private UpdateDTO(Builder builder) {
        this.comments = builder.comments;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public static class Builder {
        private List<CommentDTO> comments;

        public Builder setComments(List<CommentDTO> comments) {
            this.comments = comments;
            return this;
        }

        public UpdateDTO build() {
            return new UpdateDTO(this);
        }
    }
}
