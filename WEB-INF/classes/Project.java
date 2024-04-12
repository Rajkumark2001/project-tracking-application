import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.*;


@WebServlet("/Project")
public class Project extends HttpServlet {

  private static final long serialVersionUID = 1L;

  protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
    String action = request.getParameter("action");

     if ("getProjectList".equals(action)) {
     System.out.println(Integer.parseInt(request.getParameter("teamID")));

      String jsonProjects = Database.getProjectsAsJsonArray(Integer.parseInt(request.getParameter("teamID")));
      response.setContentType("application/json");
      try (PrintWriter out = response.getWriter()) {
        out.println(jsonProjects);
      } catch (IOException e) {
        e.printStackTrace();
      }
    
    } else if ("getTasks".equals(action)) {
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();

      try {
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        JSONArray jsonArray = Database.getTaskList(projectId);

        if (jsonArray != null) {
          out.println(jsonArray.toString());
        } else {
          out.println(
            "{\"status\": \"error\", \"message\": \"Error retrieving tasks\"}"
          );
        }
      } catch (NumberFormatException e) {
        e.printStackTrace();
        out.println(
          "{\"status\": \"error\", \"message\": \"Invalid project ID\"}"
        );
      }
    }else if("updateTask".equals(action)) {
                int taskId = Integer.parseInt(request.getParameter("taskId"));
                String status = request.getParameter("status");
                response.setContentType("application/json");
                
                PrintWriter out = response.getWriter();


                JSONObject jsonResponse = new JSONObject();

                if(Database.updateTaskInDatabase(taskId,status)){
            jsonResponse.put("status", "success");
        jsonResponse.put("message", "Task updated successfully");
            }else{
    jsonResponse.put("status", "error");
        jsonResponse.put("message", "Task update failed");
    }
     out.print(jsonResponse.toString());
  }else if ("getUserTasks".equals(action)||"getTeamMembers".equals(action)){
    response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      try {
        JSONArray jsonArray = null;
        if("getUserTasks".equals(action)){
          int userId = Integer.parseInt(request.getParameter("userId"));
        jsonArray=Database.getUserTask(userId);
        }
        if("getTeamMembers".equals(action)){
          int teamId = Integer.parseInt(request.getParameter("team"));
        jsonArray=Database.getTeamMembers(teamId);
        }
        if (jsonArray != null) {
          out.println(jsonArray.toString());
        } else {
          out.println(
            "{\"status\": \"error\", \"message\": \"Error retrieving tasks\"}"
          );
        }
      } catch (NumberFormatException e) {
        e.printStackTrace();
        out.println("{\"status\": \"error\", \"message\": \"Invalid project ID\"}");
      }
    }else if("getTeamMembers".equals(action)){
       int userId = Integer.parseInt(request.getParameter("userId"));
       
    }

  }
}