package io.getint.recruitment_task.ticketObject;

import io.getint.recruitment_task.client.JiraClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.getint.recruitment_task.ticketObject.NameHolder.*;

public class TransitionCreator extends JiraClient {
    private static final String PATH = "/rest/api/2/issue/";
    private static final String TRANSITION_PATH = "/transitions";
    private final Map<String, Integer> transitionsMap;
    private Integer defaultValueTransition = -1;

    public TransitionCreator() {
        this.transitionsMap = new HashMap<>();
    }

    public Integer getTransitionForStatus(TicketInfoHolder ticket, String status) throws IOException {
        fetchTransitions(ticket);
        return transitionsMap.getOrDefault(status, defaultValueTransition);
    }

    private void fetchTransitions(TicketInfoHolder ticket) throws IOException {
        HttpGet request = new HttpGet(JIRA_URL + PATH + ticket.getNewKey() + TRANSITION_PATH);
        JSONObject response = executeGetRequest(request);
        JSONArray transitions = response.getJSONArray(TRANSITIONS);
        createTransitionsMap(transitions);
    }

    private void createTransitionsMap(JSONArray transitions) {
        for (int i = 0; i < transitions.length(); i++) {
            JSONObject transition = transitions.getJSONObject(i);
            transitionsMap.put(transition.getString(NAME), transition.getInt(ID));
            if (defaultValueTransition < 0) {
                defaultValueTransition = transition.getInt(ID);
            }
        }
    }

}
