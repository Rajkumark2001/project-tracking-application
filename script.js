
// function createTaskForm() {
//   let formContainer = document.getElementById("page-body");
//   formContainer.innerHTML = "";
//   let dynamicFormDiv = document.createElement("div");
//   dynamicFormDiv.innerHTML =
//     "<h2>Add Task</h2>" +
//     "<form>" +
//     '<label for="taskName">Task Name:</label>' +
//     '<input type="text" id="taskName" name="taskName" required>' +
//     '<label for="taskDescription">Task Description:</label>' +
//     ' <textarea id="taskDescription" name="taskDescription" rows="4" required></textarea>' +
//     '<label for="dueDate">Due Date:</label>' +
//     '<input type="date" id="dueDate" name="dueDate" required>' +
//     "<label>Project List:</label>" +
//     '<select id="projectList"></select>' +
//     '<button type="button" id="addTaskButton" onclick=addTask()>Add Task</button>' +
//     "</form>";

//   dynamicFormDiv.id = "tackform";
//   let projectListDropdown = dynamicFormDiv.querySelector("#projectList");
//   getprojects()
//     .then((response) => response.json())
//     .then((projects) => {
//       projects.forEach((project) => {
//         let option = document.createElement("option");
//         option.value = project.ProjectID;
//         option.text = project.ProjectName;
//         projectListDropdown.add(option);
//       });
//     }).catch((error) => {
//       console.error("Error fetching projects:", error);
//     });
//   formContainer.appendChild(dynamicFormDiv);
// }

function  getTasks(id) {
  fetch("Project", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      action: "getTasks",
      projectId: id,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => { 
      localStorage.setItem('jsonData', JSON.stringify(data));
      updateUIWithTasks(data,getUserDesign(),getUserid());
    })
    .catch((error) => {
      console.error(error);
    });
}



function updateUIWithTasks(tasks, id, designation) {
  if (tasks && tasks.length > 0) {
      var taskContainer = document.getElementById("page-body");
      var downloadPdfButton = document.createElement("button");
      downloadPdfButton.textContent = "Download PDF";
      downloadPdfButton.onclick = function () {
          generatePdf(tasks);
      };
      taskContainer.appendChild(downloadPdfButton);
  
      var downloadCsvButton = document.createElement("button");
      downloadCsvButton.textContent = "Download CSV";
      downloadCsvButton.onclick = function () {
          generateCsv(tasks);
      };
      taskContainer.appendChild(downloadCsvButton);
    
      var searchInput = document.createElement("input");
      searchInput.type = "text";
      searchInput.placeholder = "Search";
      taskContainer.appendChild(searchInput);

      var searchButton = document.createElement("button");
      searchButton.textContent = "Search";
      searchButton.onclick = function () {
          var searchTerm = searchInput.value.toLowerCase();

          var filteredTasks = tasks.filter(task => {
              return (
                  task.taskId.toString().includes(searchTerm) ||
                  task.taskName.toLowerCase().includes(searchTerm) ||
                  task.startdate.toLowerCase().includes(searchTerm) ||
                  task.dueDate.toLowerCase().includes(searchTerm) ||
                  task.status.toLowerCase().includes(searchTerm)
              );
          });
          document.getElementById('page-body').innerHTML="";
          updateUIWithTasks(filteredTasks, id, designation);
      };
    
      taskContainer.appendChild(searchButton);

      var taskTable = document.createElement("table");
      taskTable.style.marginLeft = "100px";
      taskTable.classList.add("task-table");

      var tableHeader = taskTable.createTHead();
      var headerRow = tableHeader.insertRow();
      ["ID", "Name", "Members", "Assignment Date", "Due Date", "Status", "Action"].forEach(function (headerText) {
          var th = document.createElement("th");
          th.appendChild(document.createTextNode(headerText));
          headerRow.appendChild(th);
      });

      var tableBody = taskTable.createTBody();

      tasks.forEach(function (task) {
          var row = tableBody.insertRow();
          row.innerHTML = `
              <td>${task.taskId}</td>
              <td>${task.taskName}</td>
              <td>${task.taskMembers.map(member => member.name).join(", ")}</td>
              <td>${task.startdate}</td>
              <td>${task.dueDate}</td>
              <td>
                  <select id="status-${task.taskId}" onchange="updateTask(${task.taskId}, this.value)">
                      <option value="Pending" ${task.status === "Pending" ? "selected" : ""}>Pending</option>
                      <option value="Started" ${task.status === "Started" ? "selected" : ""}>Started</option> 
                      <option value="Halfway" ${task.status === "Halfway" ? "selected" : ""}>Halfway</option>
                      <option value="Almost Done" ${task.status === "Almost Done" ? "selected" : ""}>Almost Done</option>
                      <option value="Done" ${task.status === "Done" ? "selected" : ""}>Done</option>
                  </select>
              </td>
              <td>
                  <button onclick="toggleDescription(${task.taskId})">Show/Hide Description</button>
              </td>
          `;

          var descriptionRow = tableBody.insertRow();
          var descriptionCell = descriptionRow.insertCell();

          descriptionCell.colSpan = 7; 
          descriptionCell.innerHTML = `
              <span id="description-${task.taskId}" style="display: none;">Description: ${task.taskDescription}</span>
          `;
      });

      taskContainer.appendChild(taskTable);
  } else {
      console.log("No tasks to display");
  }
}

function generatePdf(tasks) {

  tasks = tasks.map(task => ({
      taskId: task.taskId || "",
      taskName: task.taskName || "",
      taskMembers: task.taskMembers || [],
      startDate: task.startdate || "",
      dueDate: task.dueDate || "",
      status: task.status || "",
  }));

  var docDefinition = {
      content: [
          {
              table: {
                  headerRows: 1,
                  body: [
                      ["ID", "Name", "Members", "Assignment Date", "Due Date", "Status"],
                      ...tasks.map(task => [
                          task.taskId,
                          task.taskName,
                          task.taskMembers.map(member => member.name).join(", "),
                          task.startDate,
                          task.dueDate,
                          task.status
                      ])
                  ]
              }
          }
      ]
  };

  pdfMake.createPdf(docDefinition).download('tasks.pdf');
}


// Function to generate CSV
function generateCsv(tasks) {
  var csvData = Papa.unparse(tasks, {
      header: true,
      delimiter: ','
  });

  var blob = new Blob([csvData], { type: 'text/csv;charset=utf-8;' });
  var link = document.createElement("a");
  var url = URL.createObjectURL(blob);
  link.setAttribute("href", url);
  link.setAttribute("download", "tasks.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}


function updateTask(taskId, status) {
  // alert(status);
  fetch("Project", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      action: "updateTask",
      taskId: taskId,
      status: status,
    }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      alert("Task updated successfully:");
      console.log("Task updated successfully:", data);
    })
    .catch((error) => {
      console.error("Error updating task:", error);
    });
}

function refresh() {
  document.getElementById("page-body").innerHTML = "";
}
function toggleDescription(taskId) {
  var descriptionElement = document.getElementById(`description-${taskId}`);
  if (descriptionElement) {
    descriptionElement.style.display =
      descriptionElement.style.display === "none" ? "inline" : "none";
  }
}
function submitProject() {
  const projectName = document.getElementById("projectName").value;
  const projectDescription =
    document.getElementById("projectDescription").value;
    const team=document.getElementById("teamSelect").value;
  fetch("AdminHandler", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      action: "addProject",
      projectName: projectName,
      projectDescription: projectDescription,
      team:team
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        displaySuccessMessage();
      } else {
        console.error("Error:", data.message);
      }
    })
    .catch((error) => {
      console.error("Fetch error:", error);
    });
}
function getprojects() {
  return fetch("Project", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      action: "getProjectList",
    }),
  });
}
function createProjectForm() {
  //alert("call");
  var formContainer = document.getElementById("page-body");
  formContainer.innerHTML = "";
  var dynamicFormDiv = document.createElement("div");
  dynamicFormDiv.innerHTML =
    "<h2>Create Project</h2>" +
    "<form>" +
    '<label for="projectName">Project Name:</label>' +
    '<input type="text" id="projectName" class="formInput" placeholder="Enter project name">' +
    '<label for="projectDescription">Project Description:</label>' +
    '<input type="text" id="projectDescription" class="formInput" placeholder="Enter project description">' +
    '<select id="teamSelect" class="formInput">' +
    '</select>' +
    '<button type="button" onclick="submitProject()">Submit</button>' +
    "</form>";

  dynamicFormDiv.id = "projectFormContainer";
  createTeamDropdown();
  formContainer.appendChild(dynamicFormDiv);
}
function displaySuccessMessage() {
  const successDiv = document.createElement("div");
  successDiv.textContent = "Project added successfully!";
  successDiv.style.color = "green";
  successDiv.style.textAlign = "center";
  document.body.appendChild(successDiv);
}
function addTask() {
  const taskName = document.getElementById("taskName").value;
  const taskDescription = document.getElementById("taskDescription").value;
  const dueDate = document.getElementById("dueDate").value;
  const projectId = document.getElementById("projectList").value;
  fetch("Project", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      action: "addTask",
      taskName: taskName,
      taskDescription: taskDescription,
      dueDate: dueDate,
      projectId: projectId,
    }),
  }).then((response) => response.json())
    .then((data) => {
      if (data.success) {
        alert("Task added successfully!");
      } else {
        alert("Failed to add task.");
      }
    })
    .catch((error) => {
      console.error("Error adding task:", error);
    });
}

function createTeamDropdown() {

  fetch(
    "AdminHandler", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        action: "teamList",
        
      }),
    }) 
    .then(response => response.json())
    .then(data => {

      var teamSelect = document.getElementById("teamSelect");
      

      var initialOption = document.createElement("option");
      initialOption.value = ""; 
      initialOption.text = "-- Select Team --";
      teamSelect.appendChild(initialOption);

   
      data.forEach(function (team) {
        var option = document.createElement("option");
        option.value = team.TeamID;
        option.text = team.TeamName;
        teamSelect.appendChild(option);
      });

 
      
    })
    .catch(error => console.error('Error fetching team data:', error));
}
function userTasks(userId,designation){
  fetch("Project", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        action: "getUserTasks",
        userId:userId
      }),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        //alert(data);
        //console.log(data);
        localStorage.setItem('jsonData', JSON.stringify(data));
        scheduleNotifications(data);
        updateUIWithTasks(data,userId,designation);
      })
      .catch((error) => {
        console.error(error);
      });   
}
// function scheduleNotifications(tasks) {
//   tasks.forEach((task) => {
//       const dueDate = new Date(task.dueDate);
//       const now = new Date();
//       if (isSameDay(dueDate, now)) {
//           alert(`Task '${task.taskName}' is due today. `);
//       }

     
//       const tomorrow = new Date(now);
//       tomorrow.setDate(now.getDate() + 1);

//       if (isSameDay(dueDate, tomorrow)) {
//           alert(`Task '${task.taskName}' is due tomorrow.`);
         
//       }
//   });
// }

// function isSameDay(date1, date2) {
//   return (date1.getFullYear() === date2.getFullYear() && date1.getMonth() === date2.getMonth() && date1.getDate() === date2.getDate());
// }
function scheduleNotifications(tasks) {
  const currentTime = new Date();

  tasks.forEach(task => {
    const dueDate = new Date(task.dueDate); 
    const timeDiffInMinutes = (dueDate.getTime() - currentTime.getTime()) / (1000 * 60);
    console.log(timeDiffInMinutes );
    if (timeDiffInMinutes <= 10 && timeDiffInMinutes >= 0) {
      const notificationMessage = `Task "${task.taskName}" is due in ${Math.abs(timeDiffInMinutes)} minutes.`;
      showNotification(notificationMessage);
    }
  });
}

function showNotification(message) {
  alert(message);
}

function teamInsertProcess(teamMembers){
let formCon=document.getElementById('page-body');
formCo.innerHTML="";
let teamForm=document.createElement('div');
teamForm.innerHTML="<form>"+
'Team Name:<select id="teamSelect" class="formInput">' +
    '</select>'
    "</form>";

}