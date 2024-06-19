package io.getint.recruitment_task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.getint.recruitment_task.model.Issue;
import io.getint.recruitment_task.model.Priority;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JiraService {

    private final String jiraUrl;
    private final String jiraUsername;
    private final String jiraApiToken;
    private final String sourceProject;
    private final String destinationProject;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JiraService(Properties properties) {
        this.jiraUrl = properties.getProperty("jira.url");
        this.jiraUsername = properties.getProperty("jira.username");
        this.jiraApiToken = properties.getProperty("jira.api.token");
        this.sourceProject = properties.getProperty("jira.source.project");
        this.destinationProject = properties.getProperty("jira.destination.project");
    }

    public void moveTasksToOtherProject() throws Exception {
        List<Issue> issues = getIssuesFromProject(sourceProject);
        for (Issue issue : issues) {
            createIssueInProject(issue, destinationProject);
        }
    }

    private List<Issue> getIssuesFromProject(String projectKey) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(jiraUrl + "/rest/api/3/search?jql=project=" + projectKey + "&maxResults=5");
            request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(jiraUsername, jiraApiToken), "UTF-8", false));

            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            JsonNode jsonNode = objectMapper.readTree(reader);
            JsonNode issuesNode = jsonNode.get("issues");

            for (JsonNode issueNode : issuesNode) {
                Issue issue = new Issue();
                issue.setIssueTypeId(issueNode.get("fields").get("issuetype").get("id").asText());
                issue.setKey(issueNode.get("key").asText());
                issue.setSummary(issueNode.get("fields").get("summary").asText());
                issue.setDescription(issueNode.get("fields").get("description"));

                Priority priority = new Priority();
                priority.setId(issueNode.get("fields").get("priority").get("id").asText());
                priority.setName(issueNode.get("fields").get("priority").get("name").asText());
                issue.setPriority(priority);

                issue.setStatus(issueNode.get("fields").get("status").get("name").asText());

                issues.add(issue);
            }
        }
        return issues;
    }

    private HttpResponse createIssueInProject(Issue issue, String projectKey) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(jiraUrl + "/rest/api/3/issue");
            request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(jiraUsername, jiraApiToken), "UTF-8", false));
            request.addHeader("Content-Type", "application/json");

            JsonNode issueNode = objectMapper.createObjectNode();
            JsonNode fieldsNode = objectMapper.createObjectNode();

            ((ObjectNode) fieldsNode).put("summary", issue.getSummary());
            ((ObjectNode) fieldsNode).put("description", issue.getDescription());

            JsonNode projectNode = objectMapper.createObjectNode();
            ((ObjectNode) projectNode).put("key", projectKey);
            ((ObjectNode) fieldsNode).set("project", projectNode);

            JsonNode issueTypeNode = objectMapper.createObjectNode();
            ((ObjectNode) issueTypeNode).put("id", issue.getIssueTypeId());
            ((ObjectNode) fieldsNode).set("issuetype", issueTypeNode);


            ((ObjectNode) issueNode).set("fields", fieldsNode);

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(issueNode));
            request.setEntity(entity);

            return httpClient.execute(request);
        }
    }
}
