package io.getint.recruitment_task;

import io.getint.recruitment_task.ticketObject.TicketInfoHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JiraSynchronizer {

    private static final String PROJECT_FROM = "MPRO1";
    private static final String PROJECT_TO = "PRO2";
    private static List<TicketInfoHolder> ticketsList;

    public JiraSynchronizer() {
        ticketsList = new ArrayList<>();
    }

    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */
    public static void moveTasksToOtherProject() throws Exception {
        resolveTicketsList();
        fetchComments();
        moveTickets();
    }

    private static void moveTickets() throws IOException {
        new TicketCreator(ticketsList, PROJECT_TO).createTickets();
    }

    private static void fetchComments() throws IOException {
        ticketsList = new CommentsFetcher(ticketsList).fetch();
    }

    private static void resolveTicketsList() throws Exception {
        JiraTicketResolver jiraTicketResolver = new JiraTicketResolver(5, PROJECT_FROM);
        List<TicketInfoHolder> ticketsInfoList = jiraTicketResolver.resolveTicketsInfo();
        List<String> ticketsKeyList = ticketsInfoList.stream().map(TicketInfoHolder::getKey).collect(Collectors.toList());
        System.out.printf("Successfully found tickets: %s, list size: %d%n", ticketsKeyList, ticketsKeyList.size());
        ticketsList = ticketsInfoList;
    }
    public static void main(String[] args) throws Exception {
        moveTasksToOtherProject();
    }
}
