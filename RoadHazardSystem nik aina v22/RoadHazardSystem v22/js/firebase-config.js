// firebase-config.js

import { initializeApp } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-app.js";
import { getAuth } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-auth.js";
import { getDatabase } from "https://www.gstatic.com/firebasejs/12.0.0/firebase-database.js";

const firebaseConfig = {

    apiKey: "AIzaSyB_pzxQWE4mhzbm1hQiXg-GV573jYdh6NA",
    authDomain: "road-hazard-management-s-8fa63.firebaseapp.com",
    databaseURL: "https://road-hazard-management-s-8fa63-default-rtdb.asia-southeast1.firebasedatabase.app",
    projectId: "road-hazard-management-s-8fa63",
    storageBucket: "road-hazard-management-s-8fa63.firebasestorage.app",
    messagingSenderId: "639353178978",
    appId: "1:639353178978:web:9ea25674ae63f5e7593a3b"

};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
export const database = getDatabase(app);

// Firebase Paths
export const HAZARD_PATH = "Hazards";
export const USER_PATH = "Users";