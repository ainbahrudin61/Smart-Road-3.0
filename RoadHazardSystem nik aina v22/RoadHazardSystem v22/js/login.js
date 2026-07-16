import { auth } from "./firebase-config.js";

import {
    signInWithEmailAndPassword,
    onAuthStateChanged,
    signOut
} from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";

const loginForm = document.getElementById("loginForm");

// ===============================
// Check Existing Login Session
// ===============================

onAuthStateChanged(auth, (user) => {

    if (user) {

        window.location.href = "dashboard.html";

    }

});

// ===============================
// Login
// ===============================

loginForm.addEventListener("submit", async (e) => {

    e.preventDefault();

    const email = document.getElementById("email").value.trim();

    const password = document.getElementById("password").value;

    if (email === "" || password === "") {

        alert("Please enter email and password.");

        return;

    }

    try {

        await signInWithEmailAndPassword(auth, email, password);

        alert("Login Successful!");

        window.location.href = "dashboard.html";

    }

    catch (error) {

        let message = "Login failed.";

        switch (error.code) {

            case "auth/invalid-email":
                message = "Invalid email address.";
                break;

            case "auth/user-not-found":
                message = "Account not found.";
                break;

            case "auth/wrong-password":
                message = "Incorrect password.";
                break;

            case "auth/invalid-credential":
                message = "Incorrect email or password.";
                break;

            case "auth/too-many-requests":
                message = "Too many attempts. Please try again later.";
                break;

            default:
                message = error.message;

        }

        alert(message);

    }

});

// ===============================
// Logout Function
// ===============================

export async function logoutAdmin() {

    try {

        await signOut(auth);

        window.location.href = "login.html";

    }

    catch (error) {

        alert(error.message);

    }

}