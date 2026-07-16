import { auth, database, HAZARD_PATH } from "./firebase-config.js";
import { ref, push, set } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-database.js";
import { signOut } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";

// =========================================
// LOGOUT
// =========================================
document.getElementById("logoutBtn").addEventListener("click", async () => {
    if (!confirm("Logout now?")) return;
    await signOut(auth);
    window.location.href = "login.html";
});

// =========================================
// SET DEFAULT DATE
// =========================================
const dateInput = document.getElementById("reportDate");
const now = new Date();
const localDate = now.toISOString().split("T")[0];
dateInput.value = localDate;

// =========================================
// IMAGE PREVIEW
// =========================================
const imageInput = document.getElementById("image");
const previewImage = document.getElementById("previewImage");
let imageBase64 = "";

imageInput.addEventListener("change", function () {
    const file = this.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function (e) {
        imageBase64 = e.target.result;
        previewImage.src = imageBase64;
        previewImage.style.display = "block";
    };
    reader.readAsDataURL(file);
});

// =========================================
// SAVE HAZARD
// =========================================
const form = document.getElementById("hazardForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const hazardType = document.getElementById("hazardType").value;
    const description = document.getElementById("description").value.trim();
    const location = document.getElementById("location").value.trim();
    const latitude = document.getElementById("latitude").value;
    const longitude = document.getElementById("longitude").value;
    const reportDate = document.getElementById("reportDate").value;
    const userAgent = document.getElementById("userAgent").value.trim();
    const status = document.getElementById("status").value;

    // ======================
    // Validation
    // ======================
    if (hazardType === "" || description === "" || location === "" || 
        latitude === "" || longitude === "" || reportDate === "" || userAgent === "") {
        alert("Please complete all required fields.");
        return;
    }

    // ======================
    // Date & Time
    // ======================
    const now = new Date();
    const time = now.toLocaleTimeString();
    const createdAt = now.getTime();

    // ======================
    // CONFIRM POPUP
    // ======================
    const confirmAdd = confirm("Add this new hazard report?");
    if (!confirmAdd) return;

    // ======================
    // Firebase
    // ======================
    const newHazardRef = push(ref(database, HAZARD_PATH));

    const hazardData = {
        hazardId: newHazardRef.key,
        username: auth.currentUser?.email || "Admin",
        hazardType,
        description,
        location,
        latitude,
        longitude,
        date: reportDate,
        time: time,
        userAgent: userAgent,
        status: status,
        image: imageBase64,
        createdAt: createdAt
    };

    try {
        await set(newHazardRef, hazardData);
        alert("Hazard added successfully.");
        form.reset();
        previewImage.style.display = "none";
        imageBase64 = "";
        dateInput.value = new Date().toISOString().split("T")[0];
        window.location.href = "hazards.html";
    } catch (error) {
        console.error(error);
        alert("Failed to save hazard.");
    }
});