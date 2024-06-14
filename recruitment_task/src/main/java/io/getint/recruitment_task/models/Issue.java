package io.getint.recruitment_task.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The issue.
 */
public class Issue {
    public String id = "";
    public String key = "";
    public String issueType = "";
    public String summary = "";
    public String description = "";
    public String assignee = "";
    public String status = "";
    public String priority = "";
    public final List<String> commentsList = new ArrayList<>();

    /**
     * The constructor.
     */
    public Issue() {
    }

    /**
     * The constructor.
     *
     * @param factor the factor
     */
    public Issue(int factor) {

        this.issueType = "Task";
        this.summary = String.format("Summary Nr %d", factor);
        this.description = String.format("Description Nr %d", factor);
        this.commentsList.add(String.format("Comment Nr %d", factor));
    }
}
