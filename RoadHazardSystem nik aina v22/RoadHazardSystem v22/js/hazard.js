import { auth, database, HAZARD_PATH } from "./firebase-config.js";
import { ref, onValue, remove } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-database.js";
import { signOut } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";

const tableBody = document.getElementById("hazardTableBody");
const searchInput = document.getElementById("searchInput");
const hazardTypeFilter = document.getElementById("hazardTypeFilter");
const statusFilter = document.getElementById("statusFilter");
const dateFilter = document.getElementById("dateFilter");
const resetBtn = document.getElementById("resetBtn");

let allHazards = [];

// ======================================
// LOAD DATA FROM FIREBASE
// ======================================
const hazardsRef = ref(database, HAZARD_PATH);

onValue(hazardsRef, (snapshot) => {
    allHazards = [];
    if (snapshot.exists()) {
        snapshot.forEach((child) => {
            allHazards.push({
                firebaseId: child.key,
                ...child.val()
            });
        });
    }
    // Sort by latest first
    allHazards.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));
    renderTable(allHazards);
});

// ======================================
// DISPLAY TABLE
// ======================================
function renderTable(data) {
    tableBody.innerHTML = "";

    if (data.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="10">No hazard reports found.</td>
            </tr>
        `;
        return;
    }

    data.forEach((hazard, index) => {
        // Use index + 1 as ID number
        const idNumber = index + 1;

        tableBody.innerHTML += `
        <tr>
            <td>${idNumber}</td>
            <td>
                ${hazard.image 
                    ? `<img src="${hazard.image}" class="table-image">` 
                    : "No Image"}
            </td>
            <td>${hazard.username || "-"}</td>
            <td>${hazard.hazardType || "-"}</td>
            <td>${hazard.description || "-"}</td>
            <td>${hazard.location || "-"}</td>
            <td>${hazard.date || "-"}</td>
            <td>${hazard.time || "-"}</td>
            <td>
                <span class="status ${statusClass(hazard.status)}">
                    ${hazard.status || "New"}
                </span>
            </td>
            <td class="action-icons">
                <button class="view-btn" title="View report" aria-label="View report" data-id="${hazard.firebaseId}" data-index="${idNumber}">
                    <i class="fa-solid fa-eye"></i>
                </button>
                <button class="edit-btn" title="Edit report" aria-label="Edit report" data-id="${hazard.firebaseId}" data-index="${idNumber}">
                    <i class="fa-solid fa-pen"></i>
                </button>
                <button class="delete-btn" title="Delete report" aria-label="Delete report" data-id="${hazard.firebaseId}" data-index="${idNumber}">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </td>
        </tr>
        `;
    });
}

// ======================================
// STATUS COLOUR
// ======================================
function statusClass(status) {
    switch(status) {
        case "New": return "new";
        case "Under Investigation": return "investigation";
        case "Repair": return "repair";
        case "Resolved": return "resolved";
        default: return "";
    }
}

// ======================================
// LIVE SEARCH & FILTER
// ======================================
function filterData() {
    const keyword = searchInput.value.toLowerCase();
    const hazardType = hazardTypeFilter.value;
    const status = statusFilter.value;
    const date = dateFilter.value;

    const filtered = allHazards.filter((hazard) => {
        const matchKeyword = 
            (hazard.username || "").toLowerCase().includes(keyword) ||
            (hazard.location || "").toLowerCase().includes(keyword);
        
        const matchHazardType = hazardType === "" || hazard.hazardType === hazardType;
        const matchStatus = status === "" || hazard.status === status;
        const matchDate = date === "" || hazard.date === date;

        return matchKeyword && matchHazardType && matchStatus && matchDate;
    });

    renderTable(filtered);
}

searchInput.addEventListener("input", filterData);
hazardTypeFilter.addEventListener("change", filterData);
statusFilter.addEventListener("change", filterData);
dateFilter.addEventListener("change", filterData);

// ======================================
// RESET FILTER
// ======================================
resetBtn.addEventListener("click", () => {
    searchInput.value = "";
    hazardTypeFilter.value = "";
    statusFilter.value = "";
    dateFilter.value = "";
    renderTable(allHazards);
});

// ======================================
// TABLE BUTTONS (View, Edit, Delete)
// ======================================
tableBody.addEventListener("click", async (e) => {
    const button = e.target.closest("button");
    if (!button) return;

    const firebaseId = button.dataset.id;
    const indexNumber = button.dataset.index;

    if (button.classList.contains("view-btn")) {
        window.location.href = `viewHazard.html?id=${firebaseId}&index=${indexNumber}`;
    }

    if (button.classList.contains("edit-btn")) {
        window.location.href = `editHazard.html?id=${firebaseId}&index=${indexNumber}`;
    }

    if (button.classList.contains("delete-btn")) {
        const confirmDelete = confirm("Delete this hazard report?");
        if (!confirmDelete) return;

        try {
            await remove(ref(database, `${HAZARD_PATH}/${firebaseId}`));
            alert("Hazard deleted successfully.");
        } catch (error) {
            alert(error.message);
        }
    }
});

// ======================================
// LOGOUT
// ======================================
document.getElementById("logoutBtn").addEventListener("click", async () => {
    if (!confirm("Logout now?")) return;
    await signOut(auth);
    window.location.href = "login.html";
});
