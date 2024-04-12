<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Project Manager</title>
    <script src="script.js"></script>
    <link rel="stylesheet" href="styles.css" />
    <link rel="icon" href="favicon.png" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/pdfmake.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/vfs_fonts.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/PapaParse/5.3.0/papaparse.min.js"></script>
    </head>
  <body>
    <div class="sidebar">
      <a href="javascript:void(0);" onclick="refresh()" class="header"
        ><img src="favicon.png" alt="Project Manager Logo" /> Project Manager</a
      >
      <hr />
      <a href="javascript:void(0)" onclick="userTask()">Home</a>
      <hr />
      <a href="javascript:void(0);" onclick="viewTeamMembers()">Team</a>
      <%if((int)request.getAttribute("Designation")!=4) {%>
      <a href="javascript:void(0);" onclick="showProject()">Portfolio</a>
      <% }%>
      <hr />
      <form action="UserHandler" method="post">
        <a href="javascript:void(0);" onclick="displayUser()">View Profile</a>
        
        <%if((int)request.getAttribute("Designation")==4) {%>
        <a href="javascript:void(0);" onclick="createProjectForm()">New Project</a>
      
        <% }%>
      </form>
    </div>
    <div id="right-sidebar"></div>
    <div class="navbar">
      <div style="color: #f1f1f1; position: absolute">
        <p>Welcome <%= (String)request.getAttribute("currentUser")%></p>
      </div> 
        <form action="UserHandler" method="post" style=" margin-left: 730px;margin-top: 10px">
          <input
            type="submit"
            name="action"
            value="Logout"
            style="height:30px;width:100px;font-size: larger;"
          />
        </form>
      </div>
    </div>
    <hr />
    <%-- <div id="alertContainer"></div> --%>
    <div id="page-body"></div>
    <div id="task"></div>
    <script>
      
      let json="";
      let id = '<%= request.getAttribute("EmployeeId")%>';
      let name = '<%= request.getAttribute("currentUser")%>';
      let email = '<%= request.getAttribute("email")%>';
      let designation = '<%= request.getAttribute("Designation")%>';
      function userTask(){  
        document.getElementById('page-body').innerHTML="";  
        userTasks(id,designation);
      }
      userTaskAlert(id);
      userTask();
    function userTaskAlert(id){
      console.log("Alert");
      const userId = id; 
const socket = new WebSocket("ws://localhost:8080/Project Tracker/alertWebSocket?userId="+userId);
const alertContainer = document.getElementById('alertContainer');
socket.onopen = (event) => {
    console.log("WebSocket connection opened:", event);
};
console.log("Alert2");
socket.onmessage = (event) => {
    const alertMessage = event.data;
   alert("Received alert:", alertMessage);
   showAlert(alertMessage);
 
};

socket.onclose = (event) => {
    console.log("WebSocket connection closed:", event);
};

    }
    function showAlert(message) {
        alertContainer.textContent = "Received alert: " + message;
        alertContainer.style.display = 'block';


        setTimeout(() => {
            alertContainer.style.display = 'none';
        }, 5000);
    }
    function displayUser() {
        if (designation == 4) {
          designation = "Admin";
        } else if (designation == 2) {
          designation = "Manager";
        } else {
          designation = "Member";
        }
        var userDetailsDiv = document.createElement("div");
        userDetailsDiv.id = "centeredDiv";

        userDetailsDiv.innerHTML =
          "<h2>User Details</h2>" +
          "<p>Employee Id:" +
          id +
          "</p>" +
          "<p>Name:" +
          name +
          "</p>" +
          "<p>Email:      " +
          email +
          "</p>" +
          "<p>Designation:" +
          designation +
          "</p>";

        var container = document.getElementById("page-body");
        container.innerHTML = "";
        container.appendChild(userDetailsDiv);
      }

  let teamId = '<%= request.getAttribute("TeamId") %>';
  function viewTeamMembers(){
  fetch("Project", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      action: "getTeamMembers",
      team:teamId
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    }).then((data) => {
      console.log(data);
      localStorage.setItem('jsonData', JSON.stringify(data));
      displayEmployeeData(data);
    }).catch((error) => {
      console.error(error);
    });   

}
  function showProject() {  
    let containerDiv = document.getElementById("page-body");
    containerDiv.innerHTML="";
    fetch("Project", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams({
            action: "getProjectList",
            teamID: teamId
        }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Network response was not ok (${response.status} - ${response.statusText})`);
        }
        return response.json();
    })
    .then(projects => {
        if (projects) {
            projects.forEach(project => {
               
                let projectDiv = document.createElement("div");
                projectDiv.classList.add("project-item");
                
                projectDiv.innerHTML = "<h3>"+project.ProjectName+"</h3>"+
                    "<p>"+project.ProjectDescription+"</p>"+
                    "<p>Start Date:"+project.startdate+"</p>"+
                    "<p>Status:"+project.status+"</p>";

                projectDiv.onclick = function () {
            
                    taskPage(project.ProjectID);
                };

                containerDiv.appendChild(projectDiv);
            });
        } else {
            console.error("Projects array is undefined or null");
        }
    })
    .catch(error => {
        console.error("Error fetching projects:", error);
    });
}
  let employeeid=new Array(id);
  function taskPage(ProjectID) {
    let taskContainer=document.getElementById('page-body');
    taskContainer.innerHTML="";
    let pI=ProjectID;
    
    let taskCreate=document.createElement('div');
     taskCreate.innerHTML = "<button type='submit' onclick='createTask("+pI+")'>Add Task</button>";
    taskContainer.appendChild(taskCreate);
    getTasks(pI);
  }
  // function createTask(ProjectID)
  // {
  //   let pI=ProjectID;
  //   let taskform=document.getElementById("page-body");
  //   taskform.innerHTML="";
  //   let form=document.createElement("div");
  //   form.id="ProjectDiv"
  //   form.innerHTML="<h2>Create New Task</h2>"+
  //   "<form id='taskForm'>"+
  //       "<label for='taskName'>Task Name:</label>"+
  //       "<input type='text' id='taskName' placeholder='Enter task name' required>"+
  //       "<br>"+
  //      " <label for='taskDescription'>Task Description:</label>"+
  //       "<textarea id='taskDescription'placeholder='Enter task description' required></textarea>"+
  //       " <br>"+
  //       "<label for='dueDate'>Due Date:</label>"+
  //       "<input type='date' id='dueDate' required>"+
  //       "<br>";
  //      if(designation!=1){
  //      form.innerHTML+="<label for='teamMembers'>Assign Team Members:</label>"+
  //   "<select id='teamMembers' multiple='multiple'></select>";
  //   fetchTeamMembers(teamId);
  //      }

  //      form.innerHTML+="<button type='button' onclick='submitTask("+pI+")'>Create Task</button>"+
  //   "</form>";

  //   taskform.appendChild(form);
  // }
  function createTask(ProjectID) {
    let pI = ProjectID;
    let taskform = document.getElementById("page-body");
    taskform.innerHTML = "";
    let form = document.createElement("div");
    form.id = "TaskCreateDiv";
    form.innerHTML = "<h2>Create New Task</h2>" +
        "<form id='taskForm'>" +
        "<label for='taskName'>Task Name:</label>" +
        "<input type='text' id='taskName' placeholder='Enter task name' required>" +
        "<br><br>" +
        "<label for='taskDescription'>Task Description:</label>" +
        "<textarea id='taskDescription' placeholder='Enter task description' required></textarea>" +
        "<br><br>" +
        "<label for='dueDateTime'>Due Date and Time:</label>" +
        "<input type='datetime-local' id='dueDateTime' required>" +
        "<br><br>";

    if (designation != 1) {
        form.innerHTML += "<label for='teamMembers'>Assign Team Members:</label>" +
            "<select id='teamMembers' multiple='multiple'></select><br><br>";
        fetchTeamMembers(teamId);
    }

    form.innerHTML += "<button type='button' onclick='submitTask(" + pI + ")'>Create Task</button>" +
        "</form>";
    taskform.appendChild(form);
}

  
function submitTask(ProjectID) {
    let taskName = document.getElementById("taskName").value;
    let taskDescription = document.getElementById("taskDescription").value;
    let dueDateTime = document.getElementById("dueDateTime").value; 
    let teamId = '<%= request.getAttribute("TeamId") %>';
alert(dueDateTime);
    if (!taskName || !taskDescription || !dueDateTime) {
        alert('Please fill in all task details.');
        return;
    }

    let task = {
        action: "insertTask",
        projectId: ProjectID,
        taskName: taskName,
        taskDescription: taskDescription,
        dueDateTime: dueDateTime, 
        employeeIds: employeeid
    };

    fetch('ProjectServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(task),
    })
    .then(response => response.json())
    .then(data => {
        alert('Task created successfully:', data);
    })
    .catch(error => {
        console.error('Error creating task:', error);
    });
}



function fetchTeamMembers(teamNumber) {
    fetch("TeamMembersServlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams({
            teamId: teamNumber,
        }),
    })
        .then(response => response.json())
        .then(teamMembers => {
          if(teamNumber!=0){
            populateTeamMembersDropdown(teamMembers);
          }else{
            teamInsertProcess(teamMembers);
          }
        })
        .catch(error => {
            console.error("Error fetching team members:", error);
        });
}

function populateTeamMembersDropdown(teamMembers) {
    let selectElement = document.getElementById("teamMembers");
    selectElement.innerHTML="";
    teamMembers.forEach(member => {
      if(member.id!=id){
        let optionElement = document.createElement("option");
        optionElement.value = member.id;
        optionElement.textContent = member.name;
        selectElement.appendChild(optionElement);
    }
    });

 
    selectElement.addEventListener("change", function () {
        let selectedValues = Array.from(selectElement.selectedOptions).map(option => option.value);
        employeeid.length=0;
        employeeid.push(id);
        employeeid=selectedValues.concat(employeeid);
    });
  }
function getUserDesign(){
return designation;
}
function getUserid(){
return id;
}
 
  function displayEmployeeData(employeeData) {
    const container = document.getElementById('page-body');
    container.innerHTML = "";
    employeeData.forEach((employee, index) => {
        let designation = "";
        if (employee.designation == 4) {
            designation += "Admin";
        } else if (employee.designation == 2) {
            designation += "Manager";
        } else {
            designation += "Member";
        }

        const employeeCard = document.createElement('div');
        employeeCard.classList.add('employee-card');
        employeeCard.innerHTML = "<strong>Name:</strong>" + employee.name + "<br>" +
            "<strong>Employee ID:</strong> " + employee.employeeId + "<br>" +
            "<strong>Designation:</strong>" + designation;
        container.appendChild(employeeCard);
    });
}


function insertTeam(){

}
    </script>
  </body>
</html>
