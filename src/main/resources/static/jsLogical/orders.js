const ORDER_API = "/orders";

function getToken() {
  return localStorage.getItem("token") || "";
}

function authHeaders() {
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${getToken()}`,
  };
}

function checkAuth() {
  const token = getToken();
  const user = localStorage.getItem("user");
  if (!token || !user) {
    alert("Please login first!");
    window.location.href = "login.html";
    return false;
  }
  return true;
}

function money(value) {
  return `$${Number(value || 0).toFixed(2)}`;
}

function formatDate(value) {
  if (!value) return "-";
  return new Date(value).toLocaleString();
}

function showToast(message) {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.className = "toast show";
  setTimeout(() => (toast.className = "toast"), 3000);
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}

async function loadMyOrders() {
  if (!checkAuth()) return;

  const loading = document.getElementById("loadingState");
  const empty = document.getElementById("emptyState");
  const list = document.getElementById("ordersList");

  loading.style.display = "flex";
  empty.style.display = "none";
  list.innerHTML = "";

  try {
    const response = await fetch(`${ORDER_API}/my-orders`, {
      method: "GET",
      headers: authHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      throw new Error(await response.text());
    }

    const orders = await response.json();
    renderOrders(orders || []);
  } catch (error) {
    console.error("Load orders error:", error);
    showToast("Failed to load orders");
  } finally {
    loading.style.display = "none";
  }
}

function renderOrders(orders) {
  const empty = document.getElementById("emptyState");
  const list = document.getElementById("ordersList");

  if (!orders.length) {
    empty.style.display = "flex";
    list.innerHTML = "";
    return;
  }

  empty.style.display = "none";

  list.innerHTML = orders
    .map((order) => {
      const items = order.orderItems || [];
      const payment = order.payment || {};

      return `
        <article class="order-card">
          <div class="order-top">
            <div>
              <h2>Order #${order.orderId}</h2>
              <p class="muted">Created: ${formatDate(order.createdAt)}</p>
            </div>
            <span class="badge ${String(order.status || "").toLowerCase()}">${order.status}</span>
          </div>

          <div class="order-grid">
            <div>
              <h3>Shipping</h3>
              <p>${order.shippingName || "-"}</p>
              <p>${order.shippingPhone || "-"}</p>
              <p>${order.shippingAddress || "-"}</p>
              <p>${order.shippingCity || ""}</p>
            </div>

            <div>
              <h3>Payment</h3>
              <p>Method: ${payment.method || "-"}</p>
              <p>Status: ${payment.status || "-"}</p>
              <p>Amount: ${money(payment.amount || order.totalAmount)}</p>
            </div>

            <div>
              <h3>Total</h3>
              <p>Subtotal: ${money(order.subtotal)}</p>
              <p>Shipping: ${money(order.shippingFee)}</p>
              <p><strong>Total: ${money(order.totalAmount)}</strong></p>
            </div>
          </div>

          <h3>Items</h3>
          <div class="items-table">
            ${items
              .map(
                (item) => `
                <div class="item-row">
                  <span>${item.productName || item.product?.product_name || "Product"}</span>
                  <span>Qty: ${item.quantity}</span>
                  <span>${money(item.price)}</span>
                  <span>${money(item.lineTotal)}</span>
                </div>
              `
              )
              .join("")}
          </div>

          ${canCancel(order.status) ? `<button class="danger-btn" onclick="cancelOrder(${order.orderId})">Cancel Order</button>` : ""}
        </article>
      `;
    })
    .join("");
}

function canCancel(status) {
  return status === "PENDING" || status === "CONFIRMED";
}

async function cancelOrder(orderId) {
  if (!confirm("Cancel this order?")) return;

  try {
    const response = await fetch(`${ORDER_API}/${orderId}/cancel`, {
      method: "PATCH",
      headers: authHeaders(),
    });

    if (!response.ok) {
      throw new Error(await response.text());
    }

    showToast("Order cancelled");
    await loadMyOrders();
  } catch (error) {
    console.error("Cancel order error:", error);
    showToast("Cannot cancel this order");
  }
}

document.addEventListener("DOMContentLoaded", loadMyOrders);
