import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.*;

@ServerEndpoint("/alertWebSocket")
public class AlertWebSocket {

    @OnOpen
    public void onOpen(Session session) {
        try {
            String queryString = session.getRequestURI().getQuery();
            if (queryString != null) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                        int userIdValue = Integer.parseInt(keyValue[1]);
                        System.out.println("userIdValue=" + userIdValue);

                        JSONArray jsonArray = Database.getUserTaskAlert(userIdValue);
                        Timestamp alertTime = null;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String taskName = jsonObject.getString("taskName");

           
                            if (jsonObject.has("dueDate")) {
                                Object dueDateObject = jsonObject.get("dueDate");

                                if (dueDateObject instanceof String) {
                                    
                                    String dueDateString = (String) dueDateObject;
                                    Timestamp dueDate = Timestamp.valueOf(dueDateString);
                                    alertTime = dueDate;

                                    System.out.println(alertTime);

                                    long delayMillis = alertTime.getTime() - System.currentTimeMillis() - (5 * 60 * 1000);
                                    System.out.println(delayMillis);

                         if(delayMillis>0){
                                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                                    
                                    scheduler.schedule(() -> sendAlert(session, taskName), delayMillis, TimeUnit.MILLISECONDS);
                         }   
                                } else {
                                    
                                    System.err.println("Warning: The 'dueDate' value is not a string.");
                                }
                            } else {
                                
                                System.err.println("Warning: The 'dueDate' key is not present in the JSON object.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void sendAlert(Session session, String taskName) {
        try {
            String alertMessage = "Attention: Your task '" + taskName + "' deadline is approaching. Please wrap up any remaining work and prepare to complete the task on time.";
            session.getBasicRemote().sendText(alertMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        
    }
}
