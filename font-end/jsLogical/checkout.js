const CART_API = "/cart";
const ORDER_API = "/orders";

let currentCartItems = [];

function getToken() {
  return localStorage.getItem("token") || "";
}

function authHeaders() {
  return {
    "Content-Type": "application/json",
    Authorization: "Bearer " + getToken(),
  };
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}

function formatPrice(value) {
  return "$" + Number(value || 0).toFixed(2);
}

function getProductName(item) {
  return item.product?.product_name || item.product?.productName || "Product";
}

function showMessage(message, isError = false) {
  const el = document.getElementById("checkoutMessage");
  el.textContent = message;
  el.style.color = isError ? "#d93636" : "#1f7a4d";
}

async function loadCartSummary() {
  const token = getToken();

  if (!token) {
    alert("Please login first.");
    window.location.href = "login.html";
    return;
  }

  try {
    const response = await fetch(`${CART_API}/getcart`, {
      method: "GET",
      headers: {
        Authorization: "Bearer " + token,
      },
    });

    if (response.status === 401 || response.status === 403) {
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      throw new Error("Failed to load cart.");
    }

    const cart = await response.json();
    currentCartItems = cart?.cartItems || [];

    renderCartSummary(currentCartItems);
  } catch (error) {
    console.error(error);
    document.getElementById("cartItems").textContent = "Failed to load cart.";
  }
}

function renderCartSummary(items) {
  const cartItemsEl = document.getElementById("cartItems");

  if (!items.length) {
    cartItemsEl.innerHTML = "<p>Your cart is empty.</p>";
    updateTotals(0);
    document.getElementById("placeOrderBtn").disabled = true;
    return;
  }

  cartItemsEl.innerHTML = items
    .map((item) => {
      const lineTotal = Number(item.price || 0) * Number(item.quantity || 0);

      return `
        <div class="cart-item">
          <div>
            <strong>${getProductName(item)}</strong><br />
            <small>Qty: ${item.quantity}</small>
          </div>
          <div>${formatPrice(lineTotal)}</div>
        </div>
      `;
    })
    .join("");

  const subtotal = items.reduce(
    (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0),
    0
  );

  updateTotals(subtotal);
}

function updateTotals(subtotal) {
  const shippingFee = subtotal > 0 ? 50 : 0;
  const total = subtotal + shippingFee;

  document.getElementById("subtotal").textContent = formatPrice(subtotal);
  document.getElementById("shipping").textContent = formatPrice(shippingFee);
  document.getElementById("total").textContent = formatPrice(total);
}

async function submitCheckout(event) {
  event.preventDefault();

  if (!currentCartItems.length) {
    alert("Your cart is empty.");
    return;
  }

  const placeOrderBtn = document.getElementById("placeOrderBtn");

  const checkoutData = {
    shippingName: document.getElementById("shippingName").value.trim(),
    shippingPhone: document.getElementById("shippingPhone").value.trim(),
    shippingAddress: document.getElementById("shippingAddress").value.trim(),
    shippingCity: document.getElementById("shippingCity").value.trim(),
    paymentMethod: document.getElementById("paymentMethod").value,
  };
 if (!isValidName(checkoutData.shippingName)) {
  showMessage("Full name must be 2-50 letters and cannot contain numbers or special characters.", true);
  return;
 }

 if (!isValidPhone(checkoutData.shippingPhone)) {
  showMessage("Phone number must start with 0 and contain exactly 10 digits.", true);
  return;
 }

 if (!isValidAddress(checkoutData.shippingAddress)) {
  showMessage("Address must be 5-120 characters and cannot contain invalid symbols.", true);
  return;
 }

 if (!isValidCity(checkoutData.shippingCity)) {
  showMessage("City must be 2-50 letters and cannot contain numbers or special characters.", true);
  return;
 }

const validPaymentMethods = ["COD", "BANK_TRANSFER", "MOCK_CARD"];
if (!validPaymentMethods.includes(checkoutData.paymentMethod)) {
  showMessage("Invalid payment method.", true);
  return;
}
  if (
    !checkoutData.shippingName ||
    !checkoutData.shippingPhone ||
    !checkoutData.shippingAddress ||
    !checkoutData.shippingCity ||
    !checkoutData.paymentMethod
  ) {
    showMessage("Please fill in all checkout information.", true);
    return;
  }

  try {
    placeOrderBtn.disabled = true;
    placeOrderBtn.textContent = "PLACING ORDER...";
    showMessage("");

    const response = await fetch(`${ORDER_API}/checkout`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(checkoutData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Checkout failed.");
    }

    const order = await response.json();

    alert("Checkout successful! Order ID: " + order.orderId);
    window.location.href = "orders.html";
  } catch (error) {
    console.error("Checkout error:", error);
    showMessage("Checkout failed: " + error.message, true);
    placeOrderBtn.disabled = false;
    placeOrderBtn.textContent = "PLACE ORDER";
  }
}

document.addEventListener("DOMContentLoaded", () => {
  loadCartSummary();

  const form = document.getElementById("checkoutForm");
  form.addEventListener("submit", submitCheckout);
});
function isValidName(name) {
  // Cho phép chữ, khoảng trắng, dấu tiếng Việt. Không cho số/ký tự đặc biệt.
  return /^[A-Za-zÀ-ỹ\s]{2,50}$/.test(name.trim());
}

function isValidPhone(phone) {
  // SĐT Việt Nam cơ bản: bắt đầu bằng 0, 10 chữ số
  return /^0\d{9}$/.test(phone.trim());
}

function isValidAddress(address) {
  // Cho phép chữ, số, khoảng trắng, dấu phẩy, chấm, gạch ngang, slash
  return /^[A-Za-zÀ-ỹ0-9\s,./-]{5,120}$/.test(address.trim());
}

function isValidCity(city) {
  return /^[A-Za-zÀ-ỹ\s]{2,50}$/.test(city.trim());
}