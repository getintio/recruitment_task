package io.getint.recruitment_task.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class JiraClient {
    public static final String JIRA_URL = "https://artko.atlassian.net";
    public static final String API_TOKEN = System.getenv("JIRA_API_TOKEN");
    public static final String EMAIL = "artur.kowal@smcebi.edu.pl";
    public CloseableHttpClient client = HttpClients.createDefault();

    public JSONObject executeGetRequest(HttpGet request) throws IOException {
        setHeaders(request);
        HttpResponse response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        return new JSONObject(responseBody);
    }

    public JSONObject executePostRequest(HttpPost request, String json) throws IOException {
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        request.setEntity(entity);
        setHeaders(request);
        HttpResponse response = client.execute(request);
        if (response.getEntity() != null) {
            String responseBody = EntityUtils.toString(response.getEntity());
            return new JSONObject(responseBody);
        } else {
            return new JSONObject();
        }
    }

    public void setHeaders(HttpRequestBase request) {
        request.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((EMAIL + ":" + API_TOKEN).getBytes()));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
    }
}
