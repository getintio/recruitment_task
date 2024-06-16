package io.getint.recruitment_task.ticketObject.requestDTO;

public class TransitionDTO {
    private final Transition transition;

    public TransitionDTO(Integer transitionId) {
        this.transition = new Transition(transitionId);
    }

    public Transition getTransition() {
        return transition;
    }

    private static class Transition {
        private final Integer id;

        public Transition(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }
}
