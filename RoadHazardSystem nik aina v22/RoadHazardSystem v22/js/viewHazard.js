import { auth, database, HAZARD_PATH } from "./firebase-config.js";
import { ref, get } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-database.js";
import { signOut } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";

// =======================================
// LOGOUT
// =======================================
document.getElementById("logoutBtn").addEventListener("click", async () => {
    const confirmLogout = confirm("Logout now?");
    if (!confirmLogout) return;

    try {
        await signOut(auth);
        window.location.href = "login.html";
    } catch (error) {
        alert(error.message);
    }
});

// =======================================
// GET HAZARD ID & INDEX
// =======================================
const params = new URLSearchParams(window.location.search);
const hazardId = params.get("id"); // Firebase ID
const indexNumber = params.get("index"); // Display ID number

if (!hazardId) {
    alert("Invalid Hazard ID.");
    window.location.href = "hazards.html";
}

// =======================================
// LOAD DATA
// =======================================
const hazardRef = ref(database, `${HAZARD_PATH}/${hazardId}`);

get(hazardRef)
    .then((snapshot) => {
        if (!snapshot.exists()) {
            alert("Hazard not found.");
            window.location.href = "hazards.html";
            return;
        }

        const data = snapshot.val();

        // Use index number as ID
        document.getElementById("hazardId").textContent = indexNumber || "1";
        document.getElementById("username").textContent = data.username || "-";
        document.getElementById("hazardType").textContent = data.hazardType || "-";
        document.getElementById("description").textContent = data.description || "-";
        document.getElementById("location").textContent = data.location || "-";
        document.getElementById("latitude").textContent = data.latitude || "-";
        document.getElementById("longitude").textContent = data.longitude || "-";
        document.getElementById("date").textContent = data.date || "-";
        document.getElementById("time").textContent = data.time || "-";
        document.getElementById("userAgent").textContent = data.userAgent || "-";

        // ======================
        // IMAGE
        // ======================
        const photo = document.getElementById("hazardPhoto");
        if (data.image && data.image !== "") {
            photo.src = data.image;
        } else {
            photo.src = "../images/hazard_bg.jpg";
            photo.classList.add("fallback-photo");
        }

        // ======================
        // STATUS
        // ======================
        const badge = document.getElementById("statusBadge");
        badge.textContent = data.status;
        badge.className = "";

        switch (data.status) {
            case "New":
                badge.classList.add("status", "new");
                break;
            case "Under Investigation":
                badge.classList.add("status", "investigation");
                break;
            case "Repair":
                badge.classList.add("status", "repair");
                break;
            case "Resolved":
                badge.classList.add("status", "resolved");
                break;
            default:
                badge.classList.add("status");
        }
    })
    .catch((error) => {
        console.error(error);
        alert("Failed to load hazard.");
    });
