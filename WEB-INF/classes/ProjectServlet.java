import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/ProjectServlet")
public class ProjectServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        JSONObject json = new JSONObject(stringBuilder.toString());

        String action = json.optString("action");

        if ("insertTask".equals(action)) {
            int projectId = json.getInt("projectId");
            String taskName = json.getString("taskName");
            String taskDescription = json.getString("taskDescription");
            String dateTimeString = json.getString("dueDateTime"); 
            JSONArray employeeIds = json.getJSONArray("employeeIds");
        
            System.out.println("..." + projectId + "-" + taskName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Timestamp dueDateTime=null;
            try {
               
                java.util.Date utilDate = dateFormat.parse(dateTimeString);
                
                
                dueDateTime = new Timestamp(utilDate.getTime());
                

            } catch (ParseException e) {
                e.printStackTrace();
                // Handle the parsing exception
            }
            System.out.println(dueDateTime);
            JSONObject resultJson = new JSONObject();
            
            int taskId = Database.insertTask(taskName, taskDescription, dueDateTime, employeeIds, projectId); 
            if (-1 != taskId) {
                resultJson.put("status", "success");
                resultJson.put("message", "Task created successfully");
                System.out.print("Task created successfully");
            } else {
                resultJson.put("status", "error");
                resultJson.put("message", "Failed to create task");
                System.out.println("false task creation");
            }
        
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        
            try (PrintWriter out = response.getWriter()) {
                out.print(resultJson.toString());
            }
        }
        
        }
    }