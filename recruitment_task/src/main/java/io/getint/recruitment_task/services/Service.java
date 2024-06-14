package io.getint.recruitment_task.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.getint.recruitment_task.models.Issue;
import io.getint.recruitment_task.utils.Helper;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.getint.recruitment_task.Application.USERNAME;
import static io.getint.recruitment_task.Constants.*;

/**
 * The service.
 */
public class Service {

    private final String password;

    /**
     * The constructor.
     *
     * @param password the password
     */
    public Service(String password) {
        this.password = password;
        Unirest.setObjectMapper(Helper.createObjectMapper());
    }

    /**
     * Gets the project.
     *
     * @param projectName the project name
     * @return the result
     * @throws Exception the exception
     */
    public JSONObject getProject(String projectName) throws Exception {

        JSONObject jsonObject;
        try {
            final HttpResponse<JsonNode> httpResponse = Unirest.get(GET_PROJECT_URL.apply(projectName))
                    .basicAuth(USERNAME, password).header("Accept", "application/json").asJson();
            Helper.showJson(httpResponse.getBody().toString());
            jsonObject = Optional.ofNullable(httpResponse.getBody())
                    .map(com.mashape.unirest.http.JsonNode::getObject).orElse(new JSONObject());
        } catch (UnirestException e) {
            System.out.printf("getProject(): UnirestException[%s]%n", e.getMessage());
            throw new Exception(e);
        }
        return jsonObject;
    }

    /**
     * Searches with the JQL.
     *
     * @param query the query
     * @return the result
     * @throws Exception the exception
     */
    public JSONObject searchWithJql(String query) throws Exception {

        JSONObject jsonObject;
        String responseStatus;
        try {
            final HttpResponse<JsonNode> httpResponse = Unirest.get(SEARCH_WITH_JQL_URL)
                    .basicAuth(USERNAME, password).header("Accept", "application/json")
                    .queryString("jql", query).asJson();
            responseStatus = httpResponse.getStatusText();
            Helper.showJson(httpResponse.getBody().toString());
            jsonObject = Optional.ofNullable(httpResponse.getBody())
                    .map(com.mashape.unirest.http.JsonNode::getObject).orElse(new JSONObject());
        } catch (UnirestException e) {
            System.out.printf("searchWithJql(): UnirestException[%s]%n", e.getMessage());
            throw new Exception(e);
        }
        System.out.printf("searchWithJql(): query[%s], response status[%s]%n", query, responseStatus);
        return jsonObject;
    }

    /**
     * Gets the issue comment.
     *
     * @param key the issue key
     * @return the result
     * @throws Exception the exception
     */
    public JSONObject getComments(String key) throws Exception {

        JSONObject jsonObject;
        String responseStatus;
        try {
            final HttpResponse<JsonNode> httpResponse = Unirest.get(GET_COMMENTS_URL.apply(key))
                    .basicAuth(USERNAME, password).header("Accept", "application/json").asJson();
            responseStatus = httpResponse.getStatusText();
            Helper.showJson(httpResponse.getBody().toString());
            jsonObject = Optional.ofNullable(httpResponse.getBody())
                    .map(com.mashape.unirest.http.JsonNode::getObject).orElse(new JSONObject());
        } catch (UnirestException e) {
            System.out.printf("getComments(): UnirestException[%s]%n", e.getMessage());
            throw new Exception(e);
        }
        System.out.printf("getComments(): key[%s], response status[%s]%n", key, responseStatus);
        return jsonObject;
    }

    /**
     * Creates the issue.
     *
     * @param projectName the project name
     * @param issue       the issue
     * @throws Exception the exception
     */
    public void createIssue(String projectName, Issue issue) throws Exception {

        final ObjectNode payload = Helper.createPayload(projectName, issue);
        JsonNode body;
        String responseStatus;
        try {
            final HttpResponse<JsonNode> httpResponse = Unirest.post(CREATE_ISSUE_URL)
                    .basicAuth(USERNAME, password).header("Accept", "application/json")
                    .header("Content-Type", "application/json").body(payload).asJson();
            responseStatus = httpResponse.getStatusText();
            body = httpResponse.getBody();
        } catch (UnirestException e) {
            System.out.printf("createIssue(): UnirestException[%s]%n", e.getMessage());
            throw new Exception(e);
        }
        final String key = addComment(issue, body);
        System.out.printf("createIssue(): key[%s], response status[%s]%n", key, responseStatus);
    }

    /**
     * Adds the comment.
     *
     * @param issue the issue
     * @param body  the body
     * @throws Exception the exception
     */
    private String addComment(Issue issue, JsonNode body) throws Exception {

        String responseStatus;
        String key;
        try {
            final List<String> keyList = new ArrayList<>();
            Optional<ObjectNode> payloadOpt = Helper.addComment(body, issue, keyList);
            if (payloadOpt.isEmpty() || keyList.isEmpty()) {
                System.out.println("addComment(): comments not added to payload");
                return "";
            }
            key = keyList.get(0);
            final HttpResponse<JsonNode> httpResponse = Unirest.post(ADD_COMMENTS_URL.apply(key))
                    .basicAuth(USERNAME, password).header("Accept", "application/json")
                    .header("Content-Type", "application/json").body(payloadOpt.get()).asJson();
            responseStatus = httpResponse.getStatusText();
        } catch (UnirestException e) {
            System.out.printf("addComment(): UnirestException[%s]%n", e.getMessage());
            throw new Exception(e);
        }
        System.out.printf("addComment(): key[%s], response status[%s]%n", key, responseStatus);
        return key;
    }

    /**
     * Deletes the issue.
     *
     * @param key the key
     * @throws Exception the exception
     */
    public void deleteIssue(String key) throws Exception {

        String responseStatus;
        try {
            final HttpResponse<String> httpResponse = Unirest.delete(DELETE_ISSUE_URL.apply(key))
                    .basicAuth(USERNAME, password).asString();
            responseStatus = httpResponse.getStatusText();
        } catch (UnirestException e) {
            System.out.printf("deleteIssue(): UnirestException[%s]%n", e.getMessage());
            throw new Exception(e);
        }
        System.out.printf("deleteIssue(): key[%s], response status[%s]%n", key, responseStatus);
    }
}