package io.getint.recruitment_task.ticketObject.requestDTO;

import io.getint.recruitment_task.ticketObject.TicketInfoHolder;

import static io.getint.recruitment_task.ticketObject.NameHolder.TASK;

public class JiraCreateRequestBuilder {
    public JiraCreateRequestDTO build(TicketInfoHolder ticket, String projectKey) {
        return new JiraCreateRequestDTO.Builder()
                .setFields(resolveFieldsDTO(ticket, projectKey))
                .build();
    }

    private FieldsDTO resolveFieldsDTO(TicketInfoHolder ticket, String projectKey) {
        return new FieldsDTO.Builder()
                .setProject(resolveProjectDTO(projectKey))
                .setDescription(ticket.getDescription())
                .setSummary(ticket.getSummary())
                .setPriority(resolvePriority(ticket))
                .setIssueType(resolveIssueTypeDTO())
                .build();
    }

    private static IssueTypeDTO resolveIssueTypeDTO() {
        return new IssueTypeDTO.Builder()
                .setName(TASK)
                .build();
    }

    private static PriorityDTO resolvePriority(TicketInfoHolder ticket) {
        return new PriorityDTO.Builder()
                .setId(ticket.getPriority())
                .build();
    }

    private ProjectDTO resolveProjectDTO(String projectKey) {
        return new ProjectDTO.Builder()
                .setKey(projectKey)
                .build();
    }
}
