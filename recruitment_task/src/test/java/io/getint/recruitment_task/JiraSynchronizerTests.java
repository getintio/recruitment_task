package io.getint.recruitment_task;

import io.getint.recruitment_task.models.Issue;
import io.getint.recruitment_task.services.Service;
import io.getint.recruitment_task.utils.Helper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.getint.recruitment_task.Application.PASSWORD;
import static io.getint.recruitment_task.Constants.*;

/**
 * The Jira synchronizer tests.
 */
public class JiraSynchronizerTests {

    private Service service;
    private JiraSynchronizer jiraSynchronizer;
    private static final boolean PROLIX = false;

    /**
     * Setups the test.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {

        service = new Service(PASSWORD);
        jiraSynchronizer = new JiraSynchronizer(service);
        if (PROLIX) {
            Helper.reportProjects(service);
        }
        Helper.removeIssues(service);
    }

    /**
     * Should sync tasks.
     * The method 'jiraSynchronizer::moveTasksToOtherProject'
     * is called three times in this test.
     *
     * @throws Exception the exception
     */
    @Test
    public void shouldSyncTasks() throws Exception {

        final AtomicInteger atomic = new AtomicInteger();
        /*
        test 1: added 7, actually moved 5, remained 2
        */
        // GIVEN
        Helper.addIssuesToSourceProject(service, 7, atomic);
        checkNumberOfIssuesInProjects(service, 7, 0);
        // WHEN
        jiraSynchronizer.moveTasksToOtherProject();
        // THEN
        checkNumberOfIssuesInProjects(service, 2,
                NUMBER_OF_MOVED_ISSUES);
        /*
        test 2: added 2, actually moved 4, remained 0
        */
        // GIVEN
        Helper.addIssuesToSourceProject(service, 2, atomic);
        checkNumberOfIssuesInProjects(service, 4,
                NUMBER_OF_MOVED_ISSUES);
        // WHEN
        jiraSynchronizer.moveTasksToOtherProject();
        // THEN
        checkNumberOfIssuesInProjects(service, 0,
                2 * NUMBER_OF_MOVED_ISSUES - 1);
        /*
        test 3: added 5, actually moved 5, remained 0
        */
        // GIVEN
        Helper.addIssuesToSourceProject(service, 5, atomic);
        checkNumberOfIssuesInProjects(service, 5,
                2 * NUMBER_OF_MOVED_ISSUES - 1);
        // WHEN
        jiraSynchronizer.moveTasksToOtherProject();
        // THEN
        checkNumberOfIssuesInProjects(service, 0,
                3 * NUMBER_OF_MOVED_ISSUES - 1);
    }

    /**
     * Should sync single task.
     *
     * @throws Exception the exception
     */
    @Test
    public void shouldSyncSingleTask() throws Exception {

        // GIVEN
        final AtomicInteger atomic = new AtomicInteger();
        Helper.addIssuesToSourceProject(service, 1, atomic);
        final JSONObject sourceProjectJSON = service.searchWithJql("project = " + SOURCE_PROJECT_KEY);
        final List<Issue> sourceIssueList = Helper.loadIssueList(sourceProjectJSON);
        Assert.assertEquals("source issues number", 1, sourceIssueList.size());
        final Issue sourceIssue = sourceIssueList.get(0);
        final String sourceComment = Optional.of(service.getComments(sourceIssue.key))
                .map(arg -> arg.getJSONArray("comments"))
                .filter(Predicate.not(JSONArray::isEmpty)).map(arg -> arg.getJSONObject(0))
                .map(arg -> arg.getString("body")).orElse("");
        // WHEN
        jiraSynchronizer.moveTasksToOtherProject();
        // THEN
        checkNumberOfIssuesInProjects(service, 0, 1);
        final JSONObject targetProjectJSON = service.searchWithJql("project = " + TARGET_PROJECT_KEY);
        final Issue targetIssue = Helper.loadIssueList(targetProjectJSON).get(0);
        final String targetComment = Optional.of(service.getComments(targetIssue.key))
                .map(arg -> arg.getJSONArray("comments"))
                .filter(Predicate.not(JSONArray::isEmpty)).map(arg -> arg.getJSONObject(0))
                .map(arg -> arg.getString("body")).orElse("");
        checkIssues(sourceIssue, sourceComment, targetIssue, targetComment);
    }

    /**
     * Should execute without tasks to sync.
     *
     * @throws Exception the exception
     */
    @Test
    public void shouldExecuteWithoutTasks() throws Exception {

        // GIVEN
        checkNumberOfIssuesInProjects(service, 0, 0);
        // WHEN
        jiraSynchronizer.moveTasksToOtherProject();
        // THEN
        checkNumberOfIssuesInProjects(service, 0, 0);
    }

    /**
     * Checks number of issues in projects
     *
     * @param service              the service
     * @param sourceIssuesExpected the  expected source issues
     * @param targetIssuesExpected the  expected target issues
     * @throws Exception the exception
     */
    private void checkNumberOfIssuesInProjects(
            Service service, int sourceIssuesExpected, int targetIssuesExpected) throws Exception {

        Function<JSONObject, Integer> projectJsonFun = projectJson ->
                Optional.of(projectJson.getJSONArray("issues"))
                        .map(arg -> {
                            final AtomicInteger atomic = new AtomicInteger();
                            for (int i = 0; i < arg.length(); i++) {
                                Optional.of(arg.getJSONObject(i)).map(obj -> obj.getJSONObject("fields"))
                                        .map(obj -> obj.getJSONObject("issuetype")).map(obj -> obj.get("name"))
                                        .filter("Task"::equals).ifPresent(obj -> atomic.incrementAndGet());
                            }
                            return atomic.get();
                        }).orElse(0);

        final JSONObject sourceProjectJSON = service.searchWithJql("project = " + SOURCE_PROJECT_KEY);
        Assert.assertEquals("source issues number", sourceIssuesExpected,
                projectJsonFun.apply(sourceProjectJSON).intValue());
        final JSONObject targetProjectJSON = service.searchWithJql("project = " + TARGET_PROJECT_KEY);
        Assert.assertEquals("target issues number", targetIssuesExpected,
                projectJsonFun.apply(targetProjectJSON).intValue());
    }

    /**
     * Checks issues.
     *
     * @param sourceIssue   the source issue
     * @param sourceComment the source comment
     * @param targetIssue   the target issue
     * @param targetComment the target comment
     */
    private void checkIssues(Issue sourceIssue, String sourceComment, Issue targetIssue, String targetComment) {

        Assert.assertEquals("issueType", "Task", sourceIssue.issueType);
        Assert.assertEquals("issueType", sourceIssue.issueType, targetIssue.issueType);
        Assert.assertEquals("summary", "Summary Nr 1", sourceIssue.summary);
        Assert.assertEquals("summary", sourceIssue.summary, targetIssue.summary);
        Assert.assertEquals("description", "Description Nr 1", sourceIssue.description);
        Assert.assertEquals("description", sourceIssue.description, targetIssue.description);
        Assert.assertEquals("assignee", "", sourceIssue.assignee);
        Assert.assertEquals("assignee", sourceIssue.assignee, targetIssue.assignee);
        Assert.assertEquals("status", "To Do", sourceIssue.status);
        Assert.assertEquals("status", sourceIssue.status, targetIssue.status);
        Assert.assertEquals("priority", "Medium", sourceIssue.priority);
        Assert.assertEquals("priority", sourceIssue.priority, targetIssue.priority);
        Assert.assertEquals("comment", "Comment Nr 1", sourceComment);
        Assert.assertEquals("comment", sourceComment, targetComment);
    }

}
