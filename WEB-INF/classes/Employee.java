public class Employee {
    private int employeeId;
    private String name;
    private int designation;
    private String email;
    private String password;
    private int teamID;
    public Employee() {
  
    }

    public Employee(int employeeId, String name, int designation, String email, String password) {
        this.employeeId = employeeId;
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.password = password;
    }
    public void setTeamID(int teamID){
        this.teamID=teamID;
    }
    public int getTeamID(){
        return teamID;
    }
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDesignation() {
        return designation;
    }

    public void setDesignation(int designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", designation=" + designation +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
