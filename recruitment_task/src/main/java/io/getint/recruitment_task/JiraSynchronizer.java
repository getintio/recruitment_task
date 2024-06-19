package io.getint.recruitment_task;

public class JiraSynchronizer {
    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */

    private final JiraService jiraService;

    public JiraSynchronizer(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    public void moveTasksToOtherProject() throws Exception {
        jiraService.moveTasksToOtherProject();
    }
}
