import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
@WebServlet("/TeamMembersServlet")
public class TeamMembersServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String teamIdParam = request.getParameter("teamId");

        if (teamIdParam == null || teamIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int teamId = Integer.parseInt(teamIdParam);

        try (Connection connection = DbConnection.getInstance().getConnection()){
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT employeeid, name FROM employee WHERE teamId = ?");
             preparedStatement.setInt(1, teamId);
             ResultSet resultSet = preparedStatement.executeQuery(); 

            JSONArray teamDataArray = new JSONArray();

            while (resultSet.next()) {
                JSONObject teamMember = new JSONObject();
                teamMember.put("id", resultSet.getInt("employeeid"));
                teamMember.put("name", resultSet.getString("name"));
                teamDataArray.put(teamMember);
            }
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
                out.write(teamDataArray.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
