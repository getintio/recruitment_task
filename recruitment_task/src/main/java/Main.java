import io.getint.recruitment_task.JiraService;
import io.getint.recruitment_task.JiraSynchronizer;

import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("recruitment_task/src/main/resources/application.properties"));

            JiraService jiraService = new JiraService(properties);
            JiraSynchronizer jiraSynchronizer = new JiraSynchronizer(jiraService);

            jiraSynchronizer.moveTasksToOtherProject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}