package io.getint.recruitment_task;

import io.getint.recruitment_task.ticketObject.TicketInfoHolder;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommentsFetcherTests {

    private CommentsFetcher commentsFetcher;

    @Before
    public void setUp() {
        List<TicketInfoHolder> ticketsList = new ArrayList<>();
        commentsFetcher = new CommentsFetcher(ticketsList);
    }

    @Test
    public void testPrepareComment() throws Exception {
        JSONObject authorObject = new JSONObject();
        authorObject.put("displayName", "ABC");

        JSONObject commentObject = new JSONObject();
        commentObject.put("body", "Comment");
        commentObject.put("author", authorObject);
        commentObject.put("created", "21.05.2024");

        Method method = CommentsFetcher.class.getDeclaredMethod("prepareComment", JSONObject.class);
        method.setAccessible(true);

        String result = (String) method.invoke(commentsFetcher, commentObject);
        String expected = "> author: ABC, created: 21.05.2024\n> Comment";

        assertEquals(expected, result);
    }
}
