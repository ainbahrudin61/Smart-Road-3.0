import { auth, database, HAZARD_PATH, USER_PATH } from "./firebase-config.js";
import { ref, onValue } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-database.js";
import { signOut } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";

// ==============================
// TOTAL USERS
// ==============================
const usersRef = ref(database, USER_PATH);

onValue(usersRef, (snapshot) => {
    let totalUsers = 0;
    if (snapshot.exists()) {
        totalUsers = snapshot.size;
    }
    document.getElementById("totalUsers").textContent = totalUsers;
});

// ==============================
// TOTAL REPORTS
// ==============================
const hazardRef = ref(database, HAZARD_PATH);

onValue(hazardRef, (snapshot) => {
    let totalReports = 0;
    let openReports = 0;
    let resolvedReports = 0;
    const reportList = [];

    if (snapshot.exists()) {
        snapshot.forEach((child) => {
            const data = child.val();
            totalReports++;

            switch (data.status) {
                case "New":
                case "Under Investigation":
                case "Repair":
                    openReports++;
                    break;
                case "Resolved":
                    resolvedReports++;
                    break;
            }

            reportList.push({
                firebaseId: child.key,
                user: data.username || data.user || "-",
                hazardType: data.hazardType || "-",
                location: data.location || "-",
                status: data.status || "-",
                createdAt: data.createdAt || "",
                date: data.date || ""
            });
        });
    }

    document.getElementById("totalReports").textContent = totalReports;
    document.getElementById("openReports").textContent = openReports;
    document.getElementById("resolvedReports").textContent = resolvedReports;

    displayRecentReports(reportList);
});

// ==============================
// RECENT REPORTS
// ==============================
function displayRecentReports(reportList) {
    const table = document.getElementById("recentTable");
    table.innerHTML = "";

    // Sort by latest first
    reportList.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));

    // Take only first 5
    const recent = reportList.slice(0, 5);

    recent.forEach((report, index) => {
        const displayDate = report.date || "-";
        const idNumber = index + 1;

        table.innerHTML += `
            <tr>
                <td>${idNumber}</td>
                <td>${report.user}</td>
                <td>${report.hazardType}</td>
                <td>${report.location}</td>
                <td>${displayDate}</td>
                <td>
                    <span class="status ${formatStatus(report.status)}">
                        ${report.status}
                    </span>
                </td>
            </tr>
        `;
    });
}

// ==============================
// STATUS CLASS
// ==============================
function formatStatus(status) {
    switch (status) {
        case "New": return "new";
        case "Under Investigation": return "investigation";
        case "Repair": return "repair";
        case "Resolved": return "resolved";
        default: return "";
    }
}

// ==============================
// LOGOUT
// ==============================
document.getElementById("logoutBtn").addEventListener("click", async () => {
    const confirmLogout = confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
        await signOut(auth);
        alert("Logout successful.");
        window.location.href = "login.html";
    } catch (error) {
        alert(error.message);
    }
});