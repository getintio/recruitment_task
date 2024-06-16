package io.getint.recruitment_task;

import io.getint.recruitment_task.client.JiraClient;
import io.getint.recruitment_task.ticketObject.TicketInfoHolder;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.getint.recruitment_task.ticketObject.NameHolder.*;

public class CommentsFetcher extends JiraClient {

    private final List<TicketInfoHolder> ticketsList;
    private static final String PATH = "/rest/api/2/issue/%s/comment";

    public CommentsFetcher(List<TicketInfoHolder> ticketsList) {
        this.ticketsList = ticketsList;
    }

    public List<TicketInfoHolder> fetch() throws IOException {
        List<String> ticketsKeyList = ticketsList.stream().map(TicketInfoHolder::getKey).collect(Collectors.toList());
        for (String ticket : ticketsKeyList) {
            collectCommentsFromTicket(ticket);
        }
        System.out.println("Successfully collected comments");
        return ticketsList;
    }

    private void collectCommentsFromTicket(String ticketKey) throws IOException {
        List<String> comments = new ArrayList<>();
        JSONObject response = resolveComments(ticketKey);
        if (!response.getJSONArray(COMMENTS).isEmpty()) {
            int commentsCount = response.getJSONArray(COMMENTS).length();
            for (int i = 0; i < commentsCount; i++) {
                comments.add(prepareComment((JSONObject) response.getJSONArray(COMMENTS).get(i)));
            }
        }
        saveCommentsOnObject(ticketKey, comments);
    }

    private JSONObject resolveComments(String ticketKey) throws IOException {
        HttpGet request = new HttpGet(resolveEndpoint(ticketKey));
        return executeGetRequest(request);
    }

    private void saveCommentsOnObject(String ticketKey, List<String> comments) {
        Optional<TicketInfoHolder> ticketDTO = ticketsList.stream().filter(ticket ->
                ticket.getKey().equals(ticketKey)).findFirst();
        ticketDTO.ifPresent(ticket -> ticket.setComments(comments));
    }

    private String prepareComment(JSONObject comment) {
        return QUOTE_CHAR +
                AUTHOR + PRESENTED + comment.getJSONObject(AUTHOR).getString(DISPLAY_NAME) +
                ", " + CREATED + PRESENTED + comment.getString(CREATED) + NEW_LINE_CHAR +
                QUOTE_CHAR + comment.getString(BODY);
    }

    private String resolveEndpoint(String ticketKey) {
        return String.format(JIRA_URL + PATH, ticketKey);
    }
}
