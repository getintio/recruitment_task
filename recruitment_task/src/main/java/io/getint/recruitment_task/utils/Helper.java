package io.getint.recruitment_task.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.JsonNode;
import io.getint.recruitment_task.models.Issue;
import io.getint.recruitment_task.services.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static io.getint.recruitment_task.Constants.SOURCE_PROJECT_KEY;
import static io.getint.recruitment_task.Constants.TARGET_PROJECT_KEY;

/**
 * The helper.
 */
public class Helper {

    private static final boolean PROLIX = false;

    /**
     * Creates the payload.
     *
     * @param projectKey the project key
     * @param issue the issue
     * @return the payload
     */
    public static ObjectNode createPayload(String projectKey, Issue issue) {

        final JsonNodeFactory jnf = JsonNodeFactory.instance;
        final ObjectNode payload = jnf.objectNode();
        final ObjectNode fields = payload.putObject("fields");
        final ObjectNode project = fields.putObject("project");
        project.put("key", projectKey);
        final ObjectNode issueType = fields.putObject("issuetype");
        issueType.put("name", issue.issueType);
        fields.put("summary", issue.summary);
        fields.put("description", issue.description);
        return payload;
    }

    /**
     * Creates the {@link ObjectMapper}.
     *
     * @return the {@link ObjectMapper}
     */
    public static com.mashape.unirest.http.ObjectMapper createObjectMapper() {

        return new com.mashape.unirest.http.ObjectMapper() {
            private final ObjectMapper jacksonObjectMapper = new ObjectMapper();

            /**
             * {@inheritDoc}
             */
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * {@inheritDoc}
             */
            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Shows the JSON
     *
     * @param body the HTTP response body
     * @throws JsonProcessingException the exception
     */
    public static void showJson(String body) throws JsonProcessingException {

        if (!PROLIX) {
            return;
        }
        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.enable(SerializationFeature.INDENT_OUTPUT)
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(mapper.readValue(body, Object.class));
        System.out.printf("showJson():%n%s%n", json);
    }

    /**
     * Adds the comment.
     *
     * @param body    the body
     * @param issue   the issue
     * @param keyList the list for sending the key
     * @return the map with project key and payload
     */
    public static Optional<ObjectNode> addComment(JsonNode body, Issue issue, List<String> keyList) {

        final JsonNodeFactory jnf = JsonNodeFactory.instance;
        final Optional<String> keyOpt = Optional.ofNullable(body)
                .map(com.mashape.unirest.http.JsonNode::getObject).map(arg -> arg.getString("key"));
        if (keyOpt.isEmpty()) {
            return Optional.empty();
        }
        keyList.add(keyOpt.get());
        ObjectNode payload = jnf.objectNode();
        Optional.of(issue.commentsList).filter(Predicate.not(List::isEmpty)).map(list -> list.get(0))
                .ifPresent(comment -> payload.put("body", comment));
        return Optional.of(payload);
    }

    /**
     * Loads the issue list.
     *
     * @param projectJSON the project
     * @return the issue list.
     */
    public static List<Issue> loadIssueList(JSONObject projectJSON) {

        final JSONArray issuesJSON = projectJSON.getJSONArray("issues");
        final List<Issue> issueList = new ArrayList<>();
        for (int i = 0; i < issuesJSON.length(); i++) {
            final JSONObject issueJSON = issuesJSON.getJSONObject(i);
            final boolean flag = Optional.of(issueJSON.getJSONObject("fields"))
                    .map(arg -> arg.getJSONObject("issuetype")).map(arg -> arg.get("name"))
                    .filter("Task"::equals).isEmpty();
            if (flag) {
                continue;
            }
            final Issue issue = new Issue();
            issueList.add(issue);
            issue.id = issueJSON.get("id").toString();
            issue.key = issueJSON.get("key").toString();
            final JSONObject fieldsJSON = issueJSON.getJSONObject("fields");
            try {
                issue.commentsList.add(fieldsJSON.get("comment").toString());
            } catch (Exception e) {
                if (PROLIX) {
                    System.out.println(e.getMessage());
                }
            }
            Optional.of(fieldsJSON.getJSONObject("issuetype"))
                    .map(arg -> arg.get("name"))
                    .ifPresent(issueType -> issue.issueType = issueType.toString());
            Optional.of(fieldsJSON.get("summary"))
                    .ifPresent(summary -> issue.summary = summary.toString());
            Optional.of(fieldsJSON.get("description"))
                    .ifPresent(status -> issue.description = status.toString());
            Optional.of(fieldsJSON.getJSONObject("status"))
                    .map(arg -> arg.get("name"))
                    .ifPresent(status -> issue.status = status.toString());
            Optional.of(fieldsJSON.getJSONObject("priority"))
                    .map(arg -> arg.get("name"))
                    .ifPresent(status -> issue.priority = status.toString());
        }
        for (Issue issue : issueList) {
            System.out.printf("loadIssueList(): key[%s]%n", issue.key);
        }
        return issueList;
    }

    /**
     * Removes the issues from source project and from target project.
     *
     * @param service the service
     * @throws Exception the exception
     */
    public static void removeIssues(Service service) throws Exception {

        final JSONObject sourceProjectJSON = service.searchWithJql("project = " + SOURCE_PROJECT_KEY);
        final List<Issue> sourceIssueList = Helper.loadIssueList(sourceProjectJSON);
        for (Issue issue : sourceIssueList) {
            service.deleteIssue(issue.key);
        }
        final JSONObject targetProjectJSON = service.searchWithJql("project = " + TARGET_PROJECT_KEY);
        final List<Issue> targetIssueList = Helper.loadIssueList(targetProjectJSON);
        for (Issue issue : targetIssueList) {
            service.deleteIssue(issue.key);
        }
        System.out.printf("removeIssues(): issue list sizes, source[%d], target[%d]%n",
                sourceIssueList.size(), targetIssueList.size());

    }

    /**
     * Add issues to source project.
     *
     * @param service the service
     * @param number  the number
     * @param atomic  the atomic for issue creation
     */
    public static void addIssuesToSourceProject(
            Service service, int number, AtomicInteger atomic) {

        IntStream.rangeClosed(1, number).forEach(arg -> {
            final Issue issue = new Issue(atomic.incrementAndGet());
            try {
                service.createIssue(SOURCE_PROJECT_KEY, issue);
            } catch (Exception e) {
                System.out.printf("addIssuesToSourceProject(): exception[%s]%n", e.getMessage());
            }
        });
        System.out.printf("addIssuesToSourceProject(): number[%d]%n", number);
    }

    /**
     * Reports the projects.
     *
     * @param service the service
     * @throws Exception the exception
     */
    public static void reportProjects(Service service) throws Exception {

        final JSONObject srcProjectJSON = service.getProject(SOURCE_PROJECT_KEY);
        System.out.println(srcProjectJSON);
        System.out.println(srcProjectJSON.get("id"));
        System.out.println(srcProjectJSON.get("key"));

        final JSONObject trgProjectJSON = service.getProject(TARGET_PROJECT_KEY);
        System.out.println(trgProjectJSON);
        System.out.println(trgProjectJSON.get("id"));
        System.out.println(trgProjectJSON.get("key"));
        System.out.println("- " .repeat(20));
    }
}
