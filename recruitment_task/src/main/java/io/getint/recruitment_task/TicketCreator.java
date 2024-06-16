package io.getint.recruitment_task;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.client.JiraClient;
import io.getint.recruitment_task.ticketObject.TicketInfoHolder;
import io.getint.recruitment_task.ticketObject.TransitionCreator;
import io.getint.recruitment_task.ticketObject.requestDTO.*;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.getint.recruitment_task.ticketObject.NameHolder.*;

public class TicketCreator extends JiraClient {
    private static final String PATH = "/rest/api/2/issue/";
    private static final String COMMENT_PATH = "/comment";
    private static final String TRANSITION_PATH = "/transitions";
    private final List<TicketInfoHolder> ticketsList;
    private final String projectKey;
    private final ObjectMapper objectMapper;
    private final TransitionCreator transitionCreator;

    public TicketCreator(List<TicketInfoHolder> ticketsList, String projectKey) {
        this.ticketsList = ticketsList;
        this.projectKey = projectKey;
        this.objectMapper = new ObjectMapper();
        this.transitionCreator = new TransitionCreator();
    }

    public void createTickets() throws IOException {
        for (TicketInfoHolder ticket : ticketsList) {
            createTicket(ticket);
            updateNewTicketByCommentsIfCommentsExist(ticket);
            updateTicketStatus(ticket);
        }
    }

    private void updateNewTicketByCommentsIfCommentsExist(TicketInfoHolder ticket) throws IOException {
        if (!ticket.getComments().isEmpty()) {
            updateNewTicketByComments(ticket);
        }
    }

    public void createTicket(TicketInfoHolder ticket) throws IOException {
        HttpPost request = new HttpPost(JIRA_URL + PATH);
        JiraCreateRequestDTO body = new JiraCreateRequestBuilder().build(ticket, projectKey);
        String jsonString = objectMapper.writeValueAsString(body);
        JSONObject response = executePostRequest(request, jsonString);
        if (response.has(KEY)) {
            String newKey = response.getString(KEY);
            saveNewKeyInTicketInfoHolder(ticket.getKey(), newKey);
            System.out.println("Successfully created ticket - " + newKey);
        } else {
            throw new RuntimeException("Failed to create ticket");
        }
    }

    private void saveNewKeyInTicketInfoHolder(String oldKey, String newKey) {
        Optional<TicketInfoHolder> ticketDTO = ticketsList.stream().filter(ticket ->
                ticket.getKey().equals(oldKey)).findFirst();
        ticketDTO.ifPresent(ticket -> ticket.setNewKey(newKey));
    }

    private void updateNewTicketByComments(TicketInfoHolder ticket) throws IOException {
        HttpPost request = new HttpPost(JIRA_URL + PATH + ticket.getNewKey() + COMMENT_PATH);
        for (String comment : ticket.getComments()) {
            CommentCreateDTO commentCreateDTO = new CommentCreateDTO.Builder().setBody(comment).build();
            String jsonString = objectMapper.writeValueAsString(commentCreateDTO);
            JSONObject response = executePostRequest(request, jsonString);
            if (response.has("created")) {
                System.out.println("Successfully added comment to - " + ticket.getNewKey());
            } else {
                throw new RuntimeException("Failed in adding comment in ticket -" + ticket.getNewKey());
            }
        }
    }

    private void updateTicketStatus(TicketInfoHolder ticket) throws IOException {
        Integer transition = transitionCreator.getTransitionForStatus(ticket, ticket.getStatus());
        HttpPost request = new HttpPost(JIRA_URL + PATH + ticket.getNewKey() + TRANSITION_PATH);
        TransitionDTO transitionDTO = new TransitionDTO(transition);
        String jsonString = objectMapper.writeValueAsString(transitionDTO);
        executePostRequest(request, jsonString);
    }
}