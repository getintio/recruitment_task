package io.getint.recruitment_task;

import java.util.function.UnaryOperator;

/**
 * The constants
 */
public class Constants {
    public static final String SOURCE_PROJECT_KEY = "SP";
    public static final String TARGET_PROJECT_KEY = "TP";
    public static final int NUMBER_OF_MOVED_ISSUES = 5;

    public static final String API_URL = "https://eeengcs.atlassian.net/rest/api/2";
    public static final UnaryOperator<String> GET_PROJECT_URL = projectName ->
            String.format("%s/project/%s", API_URL, projectName);
    public static final String SEARCH_WITH_JQL_URL = String.format("%s/search", API_URL);
    public static final String CREATE_ISSUE_URL = String.format("%s/issue", API_URL);
    public static final UnaryOperator<String> GET_COMMENTS_URL = key ->
            String.format("%s/%s/comment", CREATE_ISSUE_URL, key);
    public static final UnaryOperator<String> ADD_COMMENTS_URL = GET_COMMENTS_URL;
    public static final UnaryOperator<String> DELETE_ISSUE_URL = key ->
            String.format("%s/%s", CREATE_ISSUE_URL, key);
}
