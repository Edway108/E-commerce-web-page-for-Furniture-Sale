async function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch(`http://127.0.0.1:8080/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    const data = await res.json();

    if (res.ok) {
      console.log("LOGIN OK:", data);
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", data.token);
      localStorage.setItem("username", username);

      window.location.href = "mainPage.html"; // Chuyển sang products
    } else {
      alert(data.message || "Login failed");
    }
  } catch (error) {
    alert("Network error: " + error.message);
  }
}
