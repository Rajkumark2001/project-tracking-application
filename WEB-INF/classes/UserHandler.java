
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/UserHandler")
public class  UserHandler extends HttpServlet {
    private static final long serialVersionUID = 1L;    
 protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session=request.getSession();
   
    String action = request.getParameter("action");
    System.out.println(action);
    if ("register".equals(action)) {
        String name = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        boolean insertUser=Database.addUser(name,email,password);
                if (insertUser) {
                    Employee currentEmployee=Database.getEmployeeDetails(email);  
            request.setAttribute("EmployeeId",currentEmployee.getEmployeeId());
            request.setAttribute("currentUser",currentEmployee.getName());
            request.setAttribute("email",currentEmployee.getEmail());
            request.setAttribute("Designation",currentEmployee.getDesignation());
            System.out.println(currentEmployee.getTeamID());
            request.setAttribute("TeamId",currentEmployee.getTeamID());
                    RequestDispatcher obj=request.getRequestDispatcher("main.jsp");
                    obj.forward(request,response);

                    System.out.println("User inserted successfully!");
                } else {
                    System.out.println("User insertion failed.");
                    request.setAttribute("message","User already Exists....");
               RequestDispatcher obj=request.getRequestDispatcher("Register.jsp");
            obj.forward(request,response);
                }
    } 
    else if ("login".equals(action)) 
    {
            String email = request.getParameter("username");
            String password = request.getParameter("password");
            System.out.println(email+"  "+password);
            if (Database.validateUser(email, password)) {
            Employee currentEmployee=Database.getEmployeeDetails(email);  
            //session.setAttribute("employee",);
            request.setAttribute("EmployeeId",currentEmployee.getEmployeeId());
            request.setAttribute("currentUser",currentEmployee.getName());
            request.setAttribute("email",currentEmployee.getEmail());
            request.setAttribute("Designation",currentEmployee.getDesignation());
            request.setAttribute("TeamId",currentEmployee.getTeamID());
            RequestDispatcher obj=request.getRequestDispatcher("main.jsp");
            obj.forward(request,response);
            } else {    
                request.setAttribute("message","invalid Email or Password");
               RequestDispatcher obj=request.getRequestDispatcher("index.jsp");
            obj.forward(request,response);
            } 
    } else if("Logout".equals(action)){
         System.out.println("okey");
        if(session!=null){
            System.out.println("okey");
            session.invalidate();
            RequestDispatcher obj=request.getRequestDispatcher("index.jsp");
            obj.forward(request,response);
        }
    }
}   
private String employeeToJson(Employee employee) {   
        return "{\"success\": true, \"employee\": {" +
               "\"employeeId\":" + employee.getEmployeeId() +
               ",\"name\":\"" + employee.getName() +
               "\",\"designation\":\"" + employee.getDesignation() +
               "\",\"email\":\"" + employee.getEmail() + "\"}}";
    }
}