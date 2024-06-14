package io.getint.recruitment_task;

import io.getint.recruitment_task.models.Issue;
import io.getint.recruitment_task.services.Service;
import io.getint.recruitment_task.utils.Helper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static io.getint.recruitment_task.Constants.*;

/**
 * The Jira synchronizer.
 */
public class JiraSynchronizer {
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Service service;

    /**
     * The constructor.
     *
     * @param service the service
     */
    public JiraSynchronizer(Service service) {
        this.service = service;
    }

    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     *
     * @throws Exception the exception
     */
    public void moveTasksToOtherProject() throws Exception {

        final JSONObject sourceProjectJSON = service.searchWithJql("project = " + SOURCE_PROJECT_KEY);
        final List<Issue> issueList = Optional.of(Helper.loadIssueList(sourceProjectJSON))
                .map(list -> list.subList(0, Math.min(list.size(), NUMBER_OF_MOVED_ISSUES)))
                .orElse(List.of());
        // add issues to target project
        for (Issue issue : issueList) {
            Optional.of(service.getComments(issue.key)).map(arg -> arg.getJSONArray("comments"))
                    .filter(Predicate.not(JSONArray::isEmpty)).map(arg -> arg.getJSONObject(0))
                    .map(arg -> arg.getString("body")).ifPresent(issue.commentsList::add);
            service.createIssue(TARGET_PROJECT_KEY, issue);
        }
        // delete issues from source project
        for (Issue issue : issueList) {
            service.deleteIssue(issue.key);
        }
        logger.info(String.format("issue list size[%s]", issueList.size()));
    }
}
