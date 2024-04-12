import java.sql.*;


import org.json.*;

public class Database {

  public static Employee getEmployeeDetails(String email) {
              System.out.println("reached");

    String selectUserQuery = "SELECT * FROM employee WHERE email = ?";
    Employee employee = null;
    try (
      Connection connection = DbConnection.getInstance().getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(
        selectUserQuery
      )
    ) {
      preparedStatement.setString(1, email);
          System.out.println(email);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int employeeId = resultSet.getInt("employeeid");
          String name = resultSet.getString("name");
          System.out.println(resultSet.getInt("designation"));
          int designation = resultSet.getInt("designation");
          String userEmail = resultSet.getString("email");
          String password = resultSet.getString("password");
          int teamId=resultSet.getInt("teamid");
        
          employee =   new Employee(employeeId, name, designation, userEmail, password);
          employee.setTeamID(teamId);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return employee;
  }

  public static boolean updateTaskInDatabase(int taskId, String status) {
    try {
      Connection connection = DbConnection.getInstance().getConnection();

      String sql = "UPDATE task SET status= ? WHERE taskid = ?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, status);
        statement.setInt(2, taskId);
        statement.executeUpdate();
      }
      connection.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean validateUser(String username, String password) {
//System.out.println("reaed valid user");
    String selectUserQuery =
      "SELECT * FROM employee WHERE email = ? AND password = ?";
    try (
      
      Connection connection = DbConnection.getInstance().getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(
        selectUserQuery
      )
    ) {
      //System.out.println("reaed valid user2");
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
       // System.out.println("reaed valid user3");
        return resultSet.next();
      }
    } catch (SQLException e) {
      System.out.println("reaed valid user false");
      e.printStackTrace();
      return false;
    }
  }

  public static boolean addUser(String name, String email, String password) {
    Connection connection = null;
    try {
      connection = DbConnection.getInstance().getConnection();
      String insertUserQuery =
        "INSERT INTO employee (name, designation, email, password) VALUES (?, ?, ?, ?)";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(
          insertUserQuery
        )
      ) {
        preparedStatement.setString(1, name);
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, email);
        preparedStatement.setString(4, password);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
          return true;
        } else {
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return false;
  }

  public static boolean addProject(
    String projectName,
    String projectDescription,
    int team
  ) {
    try (Connection connection = DbConnection.getInstance().getConnection();) {
      String sql =
        "INSERT INTO ProjectList( projectName,projectDescription,teamid) VALUES (?, ?,?)";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(sql)
      ) {
        preparedStatement.setString(1, projectName);
        preparedStatement.setString(2, projectDescription);
         preparedStatement.setInt(3, team);
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
          return true;
        } else {
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static String getProjectsAsJsonArray(int teamId) {
    StringBuilder jsonArray = new StringBuilder("[");

    try (Connection connection = DbConnection.getInstance().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM projectlist WHERE teamid=?")) {
        preparedStatement.setInt(1, teamId);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int projectId = resultSet.getInt("projectid");
                String projectName = resultSet.getString("projectname");
                String projectDescription = resultSet.getString("projectdescription");
                Date startDate = resultSet.getDate("startdate");
                String status = resultSet.getString("status");

                jsonArray.append("{\"ProjectID\":")
                        .append(projectId)
                        .append(", \"ProjectName\":\"")
                        .append(projectName)
                        .append("\", \"ProjectDescription\":\"")
                        .append(projectDescription)
                        .append("\", \"startdate\":\"")
                        .append(startDate)
                        .append("\", \"status\":\"")
                        .append(status)
                        .append("\"},");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    if (jsonArray.length() > 1) {
        jsonArray.delete(jsonArray.length() - 1, jsonArray.length());
    }

    jsonArray.append("]");
    System.out.println(jsonArray.toString());
    return jsonArray.toString();
}

  
public static int insertTask(String taskName, String taskDescription, Timestamp dueDateTime, JSONArray employeeIds, int projectId) {
    try (Connection connection = DbConnection.getInstance().getConnection()) {
        String selectStmt = "SELECT employee.teamid, projectid FROM employee LEFT JOIN projectlist ON projectlist.teamid=employee.teamid WHERE employeeid=? AND projectid=?";

        try (PreparedStatement selectPrepared = connection.prepareStatement(selectStmt, Statement.RETURN_GENERATED_KEYS)) {
          
            int firstEmployeeId = employeeIds.getInt(0);
            selectPrepared.setInt(1, firstEmployeeId);
            selectPrepared.setInt(2, projectId);

            ResultSet rs = selectPrepared.executeQuery();

            if (rs.next()) {
                String insertStmt = "INSERT INTO Task (taskname, taskdescription, duedate, projectid, teamid) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement insertPrepared = connection.prepareStatement(insertStmt, Statement.RETURN_GENERATED_KEYS)) {
                    insertPrepared.setString(1, taskName);
                    insertPrepared.setString(2, taskDescription);
                    insertPrepared.setTimestamp(3, dueDateTime);
                    insertPrepared.setInt(4, rs.getInt("projectid"));
                    insertPrepared.setInt(5, rs.getInt("teamid"));

                    int rowsAffected = insertPrepared.executeUpdate();

                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = insertPrepared.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int taskId = generatedKeys.getInt(1);
                            String taskAssignmentStmt = "INSERT INTO TaskAssignment (taskid, employeeid) VALUES (?, ?)";

                            try (PreparedStatement taskAssignmentPrepared = connection.prepareStatement(taskAssignmentStmt)) {
                                for (int i = 0; i < employeeIds.length(); i++) {
                                    int employeeId = employeeIds.getInt(i);
                                    taskAssignmentPrepared.setInt(1, taskId);
                                    taskAssignmentPrepared.setInt(2, employeeId);
                                    taskAssignmentPrepared.executeUpdate();
                                }
                            }

                            return taskId;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return -1;
}


public static JSONArray getTaskList(int projectId) {
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    JSONArray jsonArray = new JSONArray();
   try (Connection connection = DbConnection.getInstance().getConnection()) {
    String selectSql = "SELECT taskid, taskname, taskdescription, startdate, duedate, status FROM Task WHERE ProjectID = ?";
    preparedStatement = connection.prepareStatement(selectSql);
    preparedStatement.setInt(1, projectId);
    resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
        JSONObject jsonObject = new JSONObject();
        int taskId = resultSet.getInt("taskid");
        jsonObject.put("taskId", taskId);
        jsonObject.put("taskName", resultSet.getString("taskname"));
        jsonObject.put("taskDescription", resultSet.getString("taskdescription"));
        jsonObject.put("startdate", resultSet.getDate("startdate"));
        jsonObject.put("dueDate", resultSet.getTimestamp("duedate"));
        jsonObject.put("status", resultSet.getString("status"));

        String taskMembersSql = "SELECT employee.employeeid, employee.name FROM taskassignment INNER JOIN employee ON taskassignment.employeeid = employee.employeeid WHERE taskassignment.taskid = ?";
        try (PreparedStatement taskMembersStmt = connection.prepareStatement(taskMembersSql)) {
            taskMembersStmt.setInt(1, taskId);
            ResultSet taskMembersResultSet = taskMembersStmt.executeQuery();

            JSONArray taskMembersArray = new JSONArray();
            while (taskMembersResultSet.next()) {
                JSONObject memberObject = new JSONObject();
                memberObject.put("employeeId", taskMembersResultSet.getInt("employeeid"));
                memberObject.put("name", taskMembersResultSet.getString("name"));
                taskMembersArray.put(memberObject);
            }

            jsonObject.put("taskMembers", taskMembersArray);
        }

        jsonArray.put(jsonObject);
    }
} catch (SQLException e) {
    e.printStackTrace();
    
} finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return jsonArray;
  }
public static JSONArray getTeamList(){
   Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
  try{
    
          connection= DbConnection.getInstance().getConnection(); 
            String sql = "SELECT teamid, teamname FROM Team";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

          
            JSONArray teamDataArray = new JSONArray();
            while (rs.next()) {
                JSONObject teamData = new JSONObject();
                teamData.put("TeamID", rs.getInt("teamid"));
                teamData.put("TeamName", rs.getString("teamname"));
                teamDataArray.put(teamData);
            }
           return teamDataArray;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }  
        }
        return null;
    }
  
public static JSONArray getUserTask(int userId){
PreparedStatement preparedStatement = null;
ResultSet resultSet = null;
JSONArray jsonArray = new JSONArray();

try (Connection connection = DbConnection.getInstance().getConnection()) {
    String selectSql = "SELECT t.taskid,t.taskname,t.taskdescription,t.startdate,t.duedate,t.status FROM task t INNER JOIN taskassignment a ON t.taskid = a.taskid WHERE a.employeeid = ?";
    preparedStatement = connection.prepareStatement(selectSql);
    preparedStatement.setInt(1,userId);
    resultSet = preparedStatement.executeQuery();

    while (resultSet.next()) {
        JSONObject jsonObject = new JSONObject();
        int taskId = resultSet.getInt("taskid");
        jsonObject.put("taskId", taskId);
        jsonObject.put("taskName", resultSet.getString("taskname"));
        jsonObject.put("taskDescription", resultSet.getString("taskdescription"));
        jsonObject.put("startdate", resultSet.getDate("startdate"));
        jsonObject.put("dueDate", resultSet.getTimestamp("duedate"));
        jsonObject.put("status", resultSet.getString("status"));

        String taskMembersSql = "SELECT employee.employeeid, employee.name FROM taskassignment INNER JOIN employee ON taskassignment.employeeid = employee.employeeid WHERE taskassignment.taskid = ?";

        try (PreparedStatement taskMembersStmt = connection.prepareStatement(taskMembersSql)) {
            taskMembersStmt.setInt(1, taskId);

            try (ResultSet taskMembersResultSet = taskMembersStmt.executeQuery()) {
                JSONArray taskMembersArray = new JSONArray();
                while (taskMembersResultSet.next()) {
                    JSONObject memberObject = new JSONObject();
                    memberObject.put("employeeId", taskMembersResultSet.getInt("employeeid"));
                    memberObject.put("name", taskMembersResultSet.getString("name"));
                    taskMembersArray.put(memberObject);
                }

                jsonObject.put("taskMembers", taskMembersArray);
            }
        }

        jsonArray.put(jsonObject);
    }
} catch (SQLException e) {

} finally {
    try {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    } catch (SQLException e) {
        
    }
}

return jsonArray;

}
public static JSONArray getTeamMembers(int teamId) {
  JSONArray teamMembersArray = new JSONArray();
  try{
    Connection connection = DbConnection.getInstance().getConnection();
    String teamMembersSql = "SELECT employeeid, name, designation FROM employee WHERE teamid=?";
    

    try (
      PreparedStatement teamMembersStmt = connection.prepareStatement(teamMembersSql)) {
        teamMembersStmt.setInt(1, teamId);

        try (ResultSet teamMembersResultSet = teamMembersStmt.executeQuery()) {
            while (teamMembersResultSet.next()) {
                JSONObject memberObject = new JSONObject();
                memberObject.put("employeeId", teamMembersResultSet.getInt("employeeid"));
                memberObject.put("name", teamMembersResultSet.getString("name"));
                memberObject.put("designation", teamMembersResultSet.getString("designation"));
                teamMembersArray.put(memberObject);
            }
        }
    } catch (SQLException e) {
       
        e.printStackTrace(); 
    }

 
}catch(Exception e){
}

   return teamMembersArray;
}






public static JSONArray getUserTaskAlert(int userId) {
  PreparedStatement preparedStatement = null;
  ResultSet resultSet = null;
  JSONArray jsonArray = new JSONArray();

  try (Connection connection = DbConnection.getInstance().getConnection()) {
      String selectSql = "SELECT t.taskname, t.duedate FROM task t INNER JOIN taskassignment a ON t.taskid = a.taskid WHERE a.employeeid = ?";
      preparedStatement = connection.prepareStatement(selectSql);
      preparedStatement.setInt(1, userId);
      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
          JSONObject jsonObject = new JSONObject();
          jsonObject.put("taskName", resultSet.getString("taskname"));
          jsonObject.put("dueDate",""+ resultSet.getTimestamp("duedate"));

          jsonArray.put(jsonObject);
      }
  } catch (SQLException e) {
      
  } finally {
      try {
          if (resultSet != null) {
              resultSet.close();
          }
          if (preparedStatement != null) {
              preparedStatement.close();
          }
      } catch (SQLException e) {
         
      }
  }

  return jsonArray;
}
}