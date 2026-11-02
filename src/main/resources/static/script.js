
const API = "/api";

let timerInterval = null;
let currentStatus = null;

// DISPLAY TIMER

function displayTimer(data) {

    currentStatus = data;

    let minutes = Math.floor(data.remainingSeconds / 60);
    let seconds = data.remainingSeconds % 60;

    document.getElementById("timer").textContent =
        String(minutes).padStart(2, "0") + ":" +
        String(seconds).padStart(2, "0");

    let titles = {
        FOCUS: "Focus Session",
        SHORT_BREAK: "Short Break",
        LONG_BREAK: "Long Break"
    };

    document.getElementById("modeTitle").textContent =
        titles[data.mode];

    document.getElementById("sessionInfo").textContent =
        "Completed Sessions: " + data.completedSessions;

    document.getElementById("completedCount").textContent =
        data.completedSessions;

    document.getElementById("focusCount").textContent =
        data.focusSessions;

    document.getElementById("startBtn").textContent =
        data.running ? "Running" : "Start";
}

// API REQUEST

async function request(url, method = "GET", body = null) {

    const options = {
        method: method,
        headers: {
            "Content-Type": "application/json"
        }
    };

    if (body !== null) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(API + url, options);

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || "Request failed");
    }

    return data;
}

// TIMER CONTROLS

async function refreshTimer() {

    try {
        const data = await request("/timer");
        displayTimer(data);
    } catch (error) {
        showError(error);
    }
}

async function startTimer() {

    try {
        const data = await request("/timer/start", "POST");
        displayTimer(data);
    } catch (error) {
        showError(error);
    }
}

async function pauseTimer() {

    try {
        const data = await request("/timer/pause", "POST");
        displayTimer(data);
    } catch (error) {
        showError(error);
    }
}

async function resetTimer() {

    try {
        const data = await request("/timer/reset", "POST");
        displayTimer(data);
    } catch (error) {
        showError(error);
    }
}

async function changeMode(mode) {

    try {
        const data = await request("/timer/mode", "POST", {
            mode: mode
        });

        displayTimer(data);
    } catch (error) {
        showError(error);
    }
}

// TIMER SETTINGS

document.getElementById("settingsForm")
    .addEventListener("submit", async function(event) {

    event.preventDefault();

    try {
        const data = await request("/timer/durations", "POST", {

            focusMinutes: Number(
                document.getElementById("focusMinutes").value
            ),

            shortBreakMinutes: Number(
                document.getElementById("shortMinutes").value
            ),

            longBreakMinutes: Number(
                document.getElementById("longMinutes").value
            )
        });

        displayTimer(data);
        alert("Settings saved successfully!");

    } catch (error) {
        showError(error);
    }
});

// TASK MANAGEMENT

async function loadTasks() {

    try {
        const tasks = await request("/tasks");
        const list = document.getElementById("taskList");

        list.innerHTML = "";

        tasks.forEach(task => {

            const li = document.createElement("li");

            const title = document.createElement("span");
            title.textContent = task.title;

            if (task.completed) {
                title.classList.add("completed");
            }

            const actions = document.createElement("div");

            if (!task.completed) {
                const done = document.createElement("button");
                done.textContent = "Done";

                done.onclick = async () => {
                    try {
                        await request(
                            "/tasks/" + task.id + "/complete",
                            "PATCH"
                        );
                        loadTasks();
                    } catch (error) {
                        showError(error);
                    }
                };

                actions.appendChild(done);
            }

            const remove = document.createElement("button");
            remove.textContent = "Delete";

            remove.onclick = async () => {
                try {
                    await request("/tasks/" + task.id, "DELETE");
                    loadTasks();
                } catch (error) {
                    showError(error);
                }
            };

            actions.appendChild(remove);

            li.appendChild(title);
            li.appendChild(actions);

            list.appendChild(li);
        });

    } catch (error) {
        showError(error);
    }
}

document.getElementById("taskForm")
    .addEventListener("submit", async function(event) {

    event.preventDefault();

    const input = document.getElementById("taskInput");

    try {
        await request("/tasks", "POST", {
            title: input.value
        });

        input.value = "";
        loadTasks();

    } catch (error) {
        showError(error);
    }
});

// SESSION HISTORY

// SESSION HISTORY

async function loadHistory() {

    try {
        const history = await request("/sessions");

        const list = document.getElementById("historyList");

        list.innerHTML = "";

        let totalMinutes = 0;

        history.forEach(session => {

            const parts = session.split(",");

            let date;
            let type;
            let minutes;

            // Support old history records
            if (parts.length === 2) {
                date = parts[0];
                type = "FOCUS";
                minutes = Number(parts[1]);
            } else {
                date = parts[0];
                type = parts[1];
                minutes = Number(parts[2]);
            }

            const names = {
                FOCUS: "Focus",
                SHORT_BREAK: "Short Break",
                LONG_BREAK: "Long Break"
            };

            const li = document.createElement("li");

            const formattedDate = new Date(date).toLocaleString();

            li.textContent =
                names[type] + " • " +
                minutes + " minutes • " +
                formattedDate;

            list.appendChild(li);

            // Count only Focus minutes
            if (type === "FOCUS" && !Number.isNaN(minutes)) {
                totalMinutes += minutes;
            }
        });

        document.getElementById("focusTime").textContent =
            totalMinutes;

    } catch (error) {
        showError(error);
    }
}

// ERROR DISPLAY

function showError(error) {
    alert(error.message);
}

// NOTIFICATIONS

async function checkNotificationPermission() {

    if ("Notification" in window &&
        Notification.permission === "default") {

        await Notification.requestPermission();
    }
}

// AUTOMATIC REFRESH

function updateApplication() {

    refreshTimer();
    loadTasks();
    loadHistory();

}

setInterval(() => {

    refreshTimer();

    if (currentStatus &&
        currentStatus.running &&
        currentStatus.remainingSeconds <= 1) {

        if ("Notification" in window &&
            Notification.permission === "granted") {

            new Notification("FocusFlow", {
                body: "Your timer session has ended!"
            });
        }

        loadHistory();
    }

}, 1000);

// INITIALIZE

checkNotificationPermission();
updateApplication();
