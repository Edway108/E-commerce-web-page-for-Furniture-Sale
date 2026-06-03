const BASE_URL = "";
const USERS_API = `${BASE_URL}/users`;

function authHeaders(extra = {}) {
  return {
    Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
    ...extra,
  };
}

function setText(id, value) {
  const el = document.getElementById(id);
  if (el) {
    el.textContent = value;
  }
}

function setValue(id, value) {
  const el = document.getElementById(id);
  if (el) {
    el.value = value;
  }
}

function showMessage(message, type = "error") {
  const box = document.getElementById("profileMessage");

  if (!box) {
    alert(message);
    return;
  }

  box.textContent = message;
  box.className = `profile-message ${type}`;
  box.style.display = message ? "block" : "none";
  box.style.whiteSpace = "pre-line";
}

async function extractErrorMessage(res) {
  try {
    const data = await res.json();

    if (data.fieldErrors) {
      return Object.values(data.fieldErrors).join("\n") || data.message || "Request failed";
    }

    return data.message || "Request failed";
  } catch {
    return "Request failed";
  }
}

async function fetchJsonOrThrow(url, options = {}) {
  const res = await fetch(url, options);

  if (!res.ok) {
    throw new Error(await extractErrorMessage(res));
  }

  if (res.status === 204) return null;

  return res.json();
}

function requireLogin() {
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
    return false;
  }

  return true;
}

async function loadProfile() {
  if (!requireLogin()) return;

  try {
    const user = await fetchJsonOrThrow(`${USERS_API}/profile`, {
      method: "GET",
      headers: authHeaders(),
    });

    setText("summaryUsername", user.username || "-");
    setText("summaryRole", user.role || "user");
    setText("summaryStatus", user.active === false ? "Inactive" : "Active");

    setValue("username", user.username || "");
    setValue("phonenumber", user.phonenumber || "");
    setValue("address", user.address || "");

    showMessage("", "success");
  } catch (error) {
    showMessage(error.message || "Request failed", "error");
  }
}

async function updateProfile() {
  if (!requireLogin()) return;

  const username = document.getElementById("username").value.trim();
  const phonenumber = document.getElementById("phonenumber").value;
  const address = document.getElementById("address").value;

  const errors = [];

  if (!username) {
    errors.push("Username is required");
  }

  if (username && username.length < 3) {
    errors.push("Username must be at least 3 characters");
  }

  if (phonenumber && Number(phonenumber) < 0) {
    errors.push("Phone number must be 0 or greater");
  }

  if (errors.length > 0) {
    showMessage(errors.join("\n"), "error");
    return;
  }

  try {
    await fetchJsonOrThrow(`${USERS_API}/profile`, {
      method: "PUT",
      headers: authHeaders({
        "Content-Type": "application/json",
      }),
      body: JSON.stringify({
        username: username,
        phonenumber: phonenumber ? Number(phonenumber) : 0,
        address: address,
      }),
    });

    await loadProfile();
    showMessage("Profile updated successfully", "success");
  } catch (error) {
    showMessage(error.message || "Update profile failed", "error");
  }
}

async function changePassword() {
  if (!requireLogin()) return;

  const currentPassword = document.getElementById("currentPassword").value;
  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  const errors = [];

  if (!currentPassword) {
    errors.push("Current password is required");
  }

  if (!newPassword || newPassword.length < 6) {
    errors.push("New password must be at least 6 characters");
  }

  if (newPassword !== confirmPassword) {
    errors.push("Confirm password does not match");
  }

  if (errors.length > 0) {
    showMessage(errors.join("\n"), "error");
    return;
  }

  try {
    await fetchJsonOrThrow(`${USERS_API}/change-password`, {
      method: "PUT",
      headers: authHeaders({
        "Content-Type": "application/json",
      }),
      body: JSON.stringify({
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword,
      }),
    });

    setValue("currentPassword", "");
    setValue("newPassword", "");
    setValue("confirmPassword", "");

    showMessage("Password changed successfully", "success");
  } catch (error) {
    showMessage(error.message || "Change password failed", "error");
  }
}

function goHome() {
  const role = localStorage.getItem("role");

  if (role === "admin" || role === "manager") {
    window.location.href = "adminPage.html";
  } else {
    window.location.href = "mainPage.html";
  }
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}

window.onload = loadProfile;