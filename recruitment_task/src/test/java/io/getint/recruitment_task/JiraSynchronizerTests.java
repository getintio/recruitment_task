package io.getint.recruitment_task;

import io.getint.recruitment_task.ticketObject.TicketInfoHolder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JiraSynchronizerTests {

    @Before
    public void setUp() {
        JiraSynchronizer jiraSynchronizer = new JiraSynchronizer();
    }

    private static List<TicketInfoHolder> getTicketInfoHolderList() {
        TicketInfoHolder ticket1 = new TicketInfoHolder();
        ticket1.setKey("TICKET-1");
        ticket1.setSummary("summary-1");
        ticket1.setDescription("desc-1");
        ticket1.setPriority("Medium");
        ticket1.setStatus("Do zrobienia");

        TicketInfoHolder ticket2 = new TicketInfoHolder();
        ticket1.setKey("TICKET-2");
        ticket1.setSummary("summary-2");
        ticket1.setDescription("desc-2");
        ticket1.setPriority("Medium");
        ticket1.setStatus("Gotowe");
        List<TicketInfoHolder> mockTickets = new ArrayList<>();
        mockTickets.add(ticket1);
        mockTickets.add(ticket2);
        return mockTickets;
    }

    @Test
    public void testResolveTicketsListWithSampleData() throws Exception {

        JiraTicketResolver jiraTicketResolver = new JiraTicketResolver(5, "MPRO1") {
            @Override
            public List<TicketInfoHolder> resolveTicketsInfo() {
                return getTicketInfoHolderList();
            }
        };

        List<TicketInfoHolder> ticketsList = jiraTicketResolver.resolveTicketsInfo();
        assertEquals(2, ticketsList.size());
        assertEquals("TICKET-2", ticketsList.get(0).getKey());
    }
    @Test
    public void testFetchCommentsWithSampleData() throws Exception {
        List<TicketInfoHolder> ticketsList = getTicketInfoHolderList();

        List<TicketInfoHolder> fetchedTickets = getTicketInfoHolders(ticketsList);
        assertEquals(2, fetchedTickets.size());
        assertEquals(2, fetchedTickets.get(0).getComments().size());
        assertEquals("Sample comment 1", fetchedTickets.get(0).getComments().get(0));
    }

    private static List<TicketInfoHolder> getTicketInfoHolders(List<TicketInfoHolder> ticketsList) throws IOException {
        CommentsFetcher commentsFetcher = new CommentsFetcher(ticketsList) {
            @Override
            public List<TicketInfoHolder> fetch() {
                for (TicketInfoHolder ticket : ticketsList) {
                    List<String> comments = new ArrayList<>();
                    comments.add("Sample comment 1");
                    comments.add("Sample comment 2");
                    ticket.setComments(comments);
                }
                return ticketsList;
            }
        };

        return commentsFetcher.fetch();
    }
}
