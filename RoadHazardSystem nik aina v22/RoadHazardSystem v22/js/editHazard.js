import { auth, database, HAZARD_PATH } from "./firebase-config.js";
import { ref, get, update } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-database.js";
import { signOut } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";

// LOGOUT
document.getElementById("logoutBtn").addEventListener("click", async () => {
    if (!confirm("Logout now?")) return;
    await signOut(auth);
    window.location.href = "login.html";
});

// GET HAZARD ID & INDEX
const params = new URLSearchParams(window.location.search);
const hazardId = params.get("id"); // Firebase ID
const indexNumber = params.get("index"); // Display ID number

if (!hazardId) {
    alert("Invalid Hazard ID.");
    window.location.href = "hazards.html";
}

// FORM
const form = document.getElementById("editHazardForm");
const previewImage = document.getElementById("previewImage");
const photoInput = document.getElementById("photo");
let imageBase64 = "";

// LOAD DATA
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
        document.getElementById("hazardId").value = indexNumber || "1";
        document.getElementById("username").value = data.username || "-";
        document.getElementById("hazardType").value = data.hazardType || "";
        document.getElementById("description").value = data.description || "";
        document.getElementById("location").value = data.location || "";
        document.getElementById("latitude").value = data.latitude || "";
        document.getElementById("longitude").value = data.longitude || "";
        document.getElementById("reportDate").value = data.date || "";
        document.getElementById("userAgent").value = data.userAgent || "";
        document.getElementById("status").value = data.status || "New";

        imageBase64 = data.image || "";
        if (imageBase64 !== "") {
            previewImage.src = imageBase64;
            previewImage.style.display = "block";
        } else {
            previewImage.style.display = "none";
        }
    })
    .catch((error) => {
        console.error(error);
        alert("Failed to load hazard data.");
    });

// CHANGE IMAGE
photoInput.addEventListener("change", function () {
    const file = this.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function (e) {
        imageBase64 = e.target.result;
        previewImage.src = imageBase64;
    };
    reader.readAsDataURL(file);
});

// UPDATE DATA
form.addEventListener("submit", async (e) => {
    e.preventDefault();

    // ======================
    // CONFIRM POPUP
    // ======================
    const confirmUpdate = confirm("Update this hazard report?");
    if (!confirmUpdate) return;

    const updatedData = {
        username: document.getElementById("username").value,
        hazardType: document.getElementById("hazardType").value,
        description: document.getElementById("description").value.trim(),
        location: document.getElementById("location").value.trim(),
        latitude: document.getElementById("latitude").value,
        longitude: document.getElementById("longitude").value,
        date: document.getElementById("reportDate").value,
        userAgent: document.getElementById("userAgent").value.trim(),
        status: document.getElementById("status").value,
        image: imageBase64,
        updatedAt: new Date().getTime()
    };

    try {
        await update(hazardRef, updatedData);
        alert("Hazard updated successfully.");
        window.location.href = "hazards.html";
    } catch (error) {
        console.error(error);
        alert("Failed to update hazard.");
    }
});
