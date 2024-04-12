import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;

@WebServlet("/AdminHandler")
public class AdminHandler extends HttpServlet {

  private static final long serialVersionUID = 1L;

  protected void doPost(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    String action = request.getParameter("action");
    if("teamList".equals(action)){
         response.setContentType("application/json");
         PrintWriter out = response.getWriter();
         JSONArray teamDataArray=Database.getTeamList();
         if(teamDataArray!=null)
            out.print(teamDataArray.toString());
         else
           response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database Error");
        
    }else if ("addProject".equals(action)) {
      String projectName = request.getParameter("projectName");
      String projectDescription = request.getParameter("projectDescription");
 
      int team=Integer.parseInt(request.getParameter("team"));
      response.setContentType("application/json");
      if (Database.addProject(projectName, projectDescription,team)) {
        response.getWriter().write("{\"success\": true}");
      } else {
        response
          .getWriter()
          .write(
            "{\"success\": false, \"message\": \"Failed to add project\"}"
          );
      }
    } 
}
}