package io.getint.recruitment_task;

import io.getint.recruitment_task.client.JiraClient;
import io.getint.recruitment_task.ticketObject.TicketInfoHolder;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;

import static io.getint.recruitment_task.ticketObject.NameHolder.*;

public class JiraTicketResolver extends JiraClient {
    private final Integer count;
    private final String projectKey;
    private static final String JQL_QUERY = "project = %s ORDER BY created DESC";
    private static final String PATH = "/rest/api/2/search?jql=";
    private static final String RESULT_LIMIT = "&maxResults=";
    public JiraTicketResolver(int count, String projectKey) {
        this.count = count;
        this.projectKey = projectKey;
    }

    public List<TicketInfoHolder> resolveTicketsInfo() throws Exception {
        HttpGet request = new HttpGet(resolveEndpoint());
        JSONObject response = executeGetRequest(request);
        JSONArray issues = resolveIssuesList(response);

        return collectTicketsInfo(issues);
    }

    protected String resolveEndpoint() {
        String jql = String.format(JQL_QUERY, projectKey);
        String encodedJql = encodeValue(jql);
        return JIRA_URL + PATH + encodedJql + RESULT_LIMIT + count;
    }

    private static JSONArray resolveIssuesList(JSONObject response) {
        JSONArray issues;
        try {
            issues = response.getJSONArray(ISSUES);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to fetch tickets" + resolveErrorMessage(response), e);
        }
        return issues;
    }

    private static String resolveErrorMessage(JSONObject response) {
        if (response.has(ERROR_MESSAGES) && !response.getJSONArray(ERROR_MESSAGES).isEmpty()) {
            return ", message: " + response.getJSONArray(ERROR_MESSAGES).get(0);
        } else {
            return EMPTY_STRING;
        }
    }

    private List<TicketInfoHolder> collectTicketsInfo(JSONArray issues) throws Exception {
        throwExceptionIfNoTicketFound(issues);
        List<TicketInfoHolder> ticketsDtoList = new ArrayList<>();
        for (int i = 0; i < issues.length(); i++) {
            JSONObject issue = issues.getJSONObject(i);
            JSONObject fields = issue.getJSONObject(FIELDS);

            TicketInfoHolder ticket = collectTicketInfo(issue, fields);
            ticketsDtoList.add(ticket);
        }
        return ticketsDtoList;
    }

    private static TicketInfoHolder collectTicketInfo(JSONObject issue, JSONObject fields) {
        TicketInfoHolder ticket = new TicketInfoHolder();
        ticket.setKey(issue.getString(KEY));
        ticket.setSummary(resolveAttribute(fields, SUMMARY));
        ticket.setDescription(resolveAttribute(fields, DESCRIPTION));
        ticket.setPriority(fields.getJSONObject(PRIORITY).getString(ID));
        ticket.setStatus(fields.getJSONObject(STATUS).getString(NAME));
        return ticket;
    }

    private static String resolveAttribute(JSONObject fields, String attribute) {
        if (fields.has(attribute) && !fields.isNull(attribute)) {
            return fields.getString(attribute);
        } else {
            return EMPTY_STRING;
        }
    }

    private static void throwExceptionIfNoTicketFound(JSONArray issues) throws Exception {
        if (issues.isEmpty()) {
            throw new Exception("No tickets found");
        }
    }

    private static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}