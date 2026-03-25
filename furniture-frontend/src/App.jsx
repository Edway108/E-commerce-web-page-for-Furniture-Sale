import { useState, useEffect } from "react";

const API = "http://localhost:8080/products";

const initialForm = { product_name: "", price: "", description: "", img: "", quantity: "" };

export default function FurnitureStore() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null); // null | 'add' | 'edit' | 'view'
  const [form, setForm] = useState(initialForm);
  const [editId, setEditId] = useState(null);
  const [toast, setToast] = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);

  const showToast = (msg, type = "success") => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  };

  const fetchAll = async () => {
    try {
      const res = await fetch(`${API}/findall`);
      const data = await res.json();
      setProducts(data);
    } catch {
      showToast("Failed to load products", "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  const openAdd = () => { setForm(initialForm); setModal("add"); };
  const openEdit = (p) => {
    setForm({ product_name: p.product_name, price: p.price, description: p.description, img: p.img || "", quantity: p.quantity });
    setEditId(p.id);
    setModal("edit");
  };
  const openView = (p) => { setEditId(p.id); setForm(p); setModal("view"); };
  const closeModal = () => { setModal(null); setEditId(null); setForm(initialForm); };

  const handleSave = async () => {
    const payload = { ...form, price: parseFloat(form.price), quantity: parseInt(form.quantity) };
    try {
      if (modal === "add") {
        await fetch(`${API}/addproduct`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
        showToast("Product added successfully");
      } else {
        await fetch(`${API}/update/${editId}`, { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
        showToast("Product updated");
      }
      closeModal();
      fetchAll();
    } catch {
      showToast("Operation failed", "error");
    }
  };

  const handleDelete = async (id) => {
    try {
      await fetch(`${API}/${id}`, { method: "DELETE" });
      showToast("Product removed");
      setDeleteConfirm(null);
      fetchAll();
    } catch {
      showToast("Delete failed", "error");
    }
  };

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,300;0,400;0,600;1,300;1,400&family=DM+Sans:wght@300;400;500&display=swap');

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
          --cream: #f5f0e8;
          --parchment: #ede6d6;
          --walnut: #3d2b1f;
          --walnut-light: #6b4c3b;
          --gold: #c9a84c;
          --gold-light: #e8c97a;
          --clay: #b07050;
          --ink: #1a1208;
          --mist: #8a7e72;
          --white: #fdfaf5;
        }

        body { background: var(--cream); }

        .app { min-height: 100vh; font-family: 'DM Sans', sans-serif; color: var(--ink); }

        /* HEADER */
        .header {
          background: var(--walnut);
          padding: 0 48px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 80px;
          position: sticky;
          top: 0;
          z-index: 100;
        }
        .header-logo {
          font-family: 'Cormorant Garamond', serif;
          font-size: 28px;
          font-weight: 300;
          color: var(--cream);
          letter-spacing: 0.08em;
          display: flex;
          align-items: center;
          gap: 12px;
        }
        .header-logo span { color: var(--gold); font-style: italic; }
        .header-line { width: 1px; height: 32px; background: var(--walnut-light); }
        .header-sub { font-size: 11px; color: var(--mist); letter-spacing: 0.2em; text-transform: uppercase; }
        .btn-add {
          background: var(--gold);
          color: var(--walnut);
          border: none;
          padding: 10px 24px;
          font-family: 'DM Sans', sans-serif;
          font-size: 12px;
          font-weight: 500;
          letter-spacing: 0.12em;
          text-transform: uppercase;
          cursor: pointer;
          transition: background 0.2s, transform 0.1s;
        }
        .btn-add:hover { background: var(--gold-light); transform: translateY(-1px); }
        .btn-add:active { transform: translateY(0); }

        /* HERO STRIP */
        .hero {
          background: var(--parchment);
          border-bottom: 1px solid #d9cfc0;
          padding: 40px 48px;
          display: flex;
          align-items: flex-end;
          justify-content: space-between;
        }
        .hero h1 {
          font-family: 'Cormorant Garamond', serif;
          font-size: 56px;
          font-weight: 300;
          line-height: 1.05;
          color: var(--walnut);
        }
        .hero h1 em { font-style: italic; color: var(--clay); }
        .hero-count {
          font-size: 11px;
          letter-spacing: 0.2em;
          text-transform: uppercase;
          color: var(--mist);
          padding-bottom: 8px;
        }
        .hero-count strong { color: var(--walnut); font-size: 28px; font-family: 'Cormorant Garamond', serif; font-weight: 400; }

        /* GRID */
        .grid-container { padding: 48px; }
        .grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
          gap: 32px;
        }

        /* CARD */
        .card {
          background: var(--white);
          border: 1px solid #e0d8cc;
          overflow: hidden;
          transition: transform 0.25s, box-shadow 0.25s;
          cursor: pointer;
        }
        .card:hover { transform: translateY(-4px); box-shadow: 0 16px 48px rgba(61,43,31,0.12); }
        .card-img {
          width: 100%;
          height: 220px;
          object-fit: cover;
          display: block;
          background: var(--parchment);
        }
        .card-img-placeholder {
          width: 100%;
          height: 220px;
          background: linear-gradient(135deg, var(--parchment) 0%, #e8ddd0 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          flex-direction: column;
          gap: 8px;
          color: var(--mist);
        }
        .card-img-placeholder svg { opacity: 0.35; }
        .card-body { padding: 20px 24px 24px; }
        .card-tag {
          font-size: 10px;
          letter-spacing: 0.2em;
          text-transform: uppercase;
          color: var(--gold);
          margin-bottom: 6px;
        }
        .card-name {
          font-family: 'Cormorant Garamond', serif;
          font-size: 22px;
          font-weight: 400;
          color: var(--walnut);
          line-height: 1.2;
          margin-bottom: 8px;
        }
        .card-desc {
          font-size: 13px;
          color: var(--mist);
          line-height: 1.6;
          margin-bottom: 16px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        .card-footer {
          display: flex;
          align-items: center;
          justify-content: space-between;
          border-top: 1px solid var(--parchment);
          padding-top: 14px;
          margin-top: 4px;
        }
        .card-price {
          font-family: 'Cormorant Garamond', serif;
          font-size: 24px;
          font-weight: 600;
          color: var(--walnut);
        }
        .card-price span { font-size: 14px; font-weight: 300; }
        .card-qty {
          font-size: 11px;
          letter-spacing: 0.1em;
          color: var(--mist);
          background: var(--parchment);
          padding: 4px 10px;
        }
        .card-actions {
          display: flex;
          gap: 8px;
          margin-top: 14px;
        }
        .btn-icon {
          flex: 1;
          border: 1px solid;
          background: transparent;
          padding: 8px;
          font-size: 11px;
          letter-spacing: 0.1em;
          text-transform: uppercase;
          cursor: pointer;
          transition: all 0.15s;
          font-family: 'DM Sans', sans-serif;
          font-weight: 500;
        }
        .btn-edit { border-color: var(--walnut-light); color: var(--walnut-light); }
        .btn-edit:hover { background: var(--walnut); color: var(--cream); border-color: var(--walnut); }
        .btn-delete { border-color: #d4937a; color: var(--clay); }
        .btn-delete:hover { background: #c0392b; color: white; border-color: #c0392b; }

        /* EMPTY */
        .empty {
          text-align: center;
          padding: 80px 20px;
          color: var(--mist);
        }
        .empty h2 {
          font-family: 'Cormorant Garamond', serif;
          font-size: 36px;
          font-weight: 300;
          color: var(--walnut);
          margin-bottom: 12px;
        }

        /* LOADING */
        .loading {
          display: flex;
          align-items: center;
          justify-content: center;
          height: 300px;
          gap: 12px;
          color: var(--mist);
          font-size: 13px;
          letter-spacing: 0.1em;
        }
        .spinner {
          width: 24px; height: 24px;
          border: 2px solid var(--parchment);
          border-top-color: var(--gold);
          border-radius: 50%;
          animation: spin 0.8s linear infinite;
        }
        @keyframes spin { to { transform: rotate(360deg); } }

        /* OVERLAY */
        .overlay {
          position: fixed; inset: 0;
          background: rgba(26,18,8,0.65);
          backdrop-filter: blur(4px);
          z-index: 200;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 20px;
          animation: fadeIn 0.2s ease;
        }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

        /* MODAL */
        .modal {
          background: var(--white);
          width: 100%;
          max-width: 520px;
          border-top: 4px solid var(--gold);
          animation: slideUp 0.25s ease;
          max-height: 90vh;
          overflow-y: auto;
        }
        @keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

        .modal-header {
          padding: 28px 32px 20px;
          border-bottom: 1px solid var(--parchment);
          display: flex;
          align-items: flex-start;
          justify-content: space-between;
        }
        .modal-title {
          font-family: 'Cormorant Garamond', serif;
          font-size: 28px;
          font-weight: 400;
          color: var(--walnut);
        }
        .modal-subtitle { font-size: 12px; color: var(--mist); letter-spacing: 0.1em; text-transform: uppercase; margin-top: 2px; }
        .modal-close {
          background: none; border: none; cursor: pointer;
          color: var(--mist); font-size: 22px; line-height: 1;
          padding: 2px 6px;
          transition: color 0.15s;
        }
        .modal-close:hover { color: var(--walnut); }

        .modal-body { padding: 24px 32px 32px; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .field { margin-bottom: 18px; }
        .field label {
          display: block;
          font-size: 11px;
          letter-spacing: 0.15em;
          text-transform: uppercase;
          color: var(--mist);
          margin-bottom: 6px;
        }
        .field input, .field textarea {
          width: 100%;
          border: 1px solid #d9cfc0;
          background: var(--cream);
          padding: 10px 14px;
          font-family: 'DM Sans', sans-serif;
          font-size: 14px;
          color: var(--ink);
          outline: none;
          transition: border-color 0.15s;
        }
        .field input:focus, .field textarea:focus { border-color: var(--gold); background: var(--white); }
        .field textarea { resize: vertical; min-height: 80px; }
        .modal-footer {
          padding: 0 32px 28px;
          display: flex;
          gap: 12px;
          justify-content: flex-end;
        }
        .btn-cancel {
          background: none; border: 1px solid #d9cfc0;
          color: var(--mist); padding: 10px 24px;
          font-family: 'DM Sans', sans-serif; font-size: 12px;
          letter-spacing: 0.1em; text-transform: uppercase;
          cursor: pointer; transition: all 0.15s;
        }
        .btn-cancel:hover { border-color: var(--walnut); color: var(--walnut); }
        .btn-save {
          background: var(--walnut); color: var(--cream);
          border: none; padding: 10px 28px;
          font-family: 'DM Sans', sans-serif; font-size: 12px;
          letter-spacing: 0.12em; text-transform: uppercase;
          cursor: pointer; transition: background 0.15s;
          font-weight: 500;
        }
        .btn-save:hover { background: var(--walnut-light); }

        /* VIEW MODAL */
        .view-img { width: 100%; height: 260px; object-fit: cover; display: block; }
        .view-img-placeholder {
          width: 100%; height: 200px;
          background: linear-gradient(135deg, var(--parchment) 0%, #e8ddd0 100%);
          display: flex; align-items: center; justify-content: center;
          color: var(--mist);
        }
        .view-body { padding: 24px 32px 32px; }
        .view-price {
          font-family: 'Cormorant Garamond', serif;
          font-size: 38px; font-weight: 600; color: var(--walnut);
          margin-bottom: 4px;
        }
        .view-name {
          font-family: 'Cormorant Garamond', serif;
          font-size: 32px; font-weight: 300; color: var(--walnut);
          margin-bottom: 16px;
        }
        .view-desc { font-size: 14px; line-height: 1.7; color: var(--mist); margin-bottom: 20px; }
        .view-meta { display: flex; gap: 24px; }
        .view-meta-item label { font-size: 10px; letter-spacing: 0.2em; text-transform: uppercase; color: var(--mist); display: block; margin-bottom: 4px; }
        .view-meta-item span { font-size: 16px; color: var(--walnut); font-family: 'Cormorant Garamond', serif; }

        /* DELETE CONFIRM */
        .confirm-modal { max-width: 400px; padding: 32px; text-align: center; }
        .confirm-modal h3 { font-family: 'Cormorant Garamond', serif; font-size: 26px; color: var(--walnut); margin-bottom: 10px; }
        .confirm-modal p { font-size: 14px; color: var(--mist); margin-bottom: 24px; line-height: 1.6; }
        .confirm-modal .btn-confirm-del {
          background: #c0392b; color: white; border: none;
          padding: 10px 24px; cursor: pointer;
          font-family: 'DM Sans', sans-serif; font-size: 12px;
          letter-spacing: 0.1em; text-transform: uppercase;
          transition: background 0.15s;
        }
        .confirm-modal .btn-confirm-del:hover { background: #a93226; }
        .confirm-actions { display: flex; gap: 12px; justify-content: center; }

        /* TOAST */
        .toast {
          position: fixed; bottom: 32px; right: 32px;
          padding: 14px 20px; z-index: 999;
          font-size: 13px; letter-spacing: 0.05em;
          display: flex; align-items: center; gap: 10px;
          animation: slideInRight 0.3s ease;
          box-shadow: 0 8px 32px rgba(0,0,0,0.15);
        }
        .toast.success { background: var(--walnut); color: var(--cream); }
        .toast.error { background: #c0392b; color: white; }
        @keyframes slideInRight { from { transform: translateX(40px); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
      `}</style>

      <div className="app">
        {/* HEADER */}
        <header className="header">
          <div style={{ display: "flex", alignItems: "center", gap: 20 }}>
            <div className="header-logo">
              <span>Furnituree</span>
            </div>
            <div className="header-line" />
            <div className="header-sub">Product Catalog</div>
          </div>
          <button className="btn-add" onClick={openAdd}>+ Add Product</button>
        </header>

        {/* HERO */}
        <div className="hero">
          <h1>Curated <em>Pieces</em><br />for Every Space</h1>
          <div className="hero-count">
            <strong>{products.length}</strong><br />Items in Catalog
          </div>
        </div>

        {/* CONTENT */}
        <div className="grid-container">
          {loading ? (
            <div className="loading">
              <div className="spinner" />
              Loading catalog…
            </div>
          ) : products.length === 0 ? (
            <div className="empty">
              <h2>No pieces yet</h2>
              <p>Start by adding your first furniture item to the catalog.</p>
            </div>
          ) : (
            <div className="grid">
              {products.map(p => (
                <div className="card" key={p.id}>
                  {p.img
                    ? <img src={p.img} alt={p.product_name} className="card-img" onClick={() => openView(p)} />
                    : (
                      <div className="card-img-placeholder" onClick={() => openView(p)}>
                        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                          <rect x="3" y="10" width="18" height="8" rx="1" /><rect x="6" y="6" width="12" height="4" rx="1" />
                          <line x1="7" y1="18" x2="7" y2="21" /><line x1="17" y1="18" x2="17" y2="21" />
                        </svg>
                        <span style={{ fontSize: 11, letterSpacing: "0.1em" }}>No Image</span>
                      </div>
                    )
                  }
                  <div className="card-body">
                    <div className="card-tag">Furniture</div>
                    <div className="card-name" onClick={() => openView(p)}>{p.product_name}</div>
                    <div className="card-desc">{p.description || "No description available."}</div>
                    <div className="card-footer">
                      <div className="card-price"><span>$</span>{Number(p.price).toLocaleString("en-US", { minimumFractionDigits: 2 })}</div>
                      <div className="card-qty">Qty: {p.quantity}</div>
                    </div>
                    <div className="card-actions">
                      <button className="btn-icon btn-edit" onClick={() => openEdit(p)}>Edit</button>
                      <button className="btn-icon btn-delete" onClick={() => setDeleteConfirm(p)}>Remove</button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* ADD / EDIT MODAL */}
        {(modal === "add" || modal === "edit") && (
          <div className="overlay" onClick={closeModal}>
            <div className="modal" onClick={e => e.stopPropagation()}>
              <div className="modal-header">
                <div>
                  <div className="modal-title">{modal === "add" ? "New Piece" : "Edit Piece"}</div>
                  <div className="modal-subtitle">{modal === "add" ? "Add to catalog" : "Update details"}</div>
                </div>
                <button className="modal-close" onClick={closeModal}>×</button>
              </div>
              <div className="modal-body">
                <div className="field">
                  <label>Product Name</label>
                  <input value={form.product_name} onChange={e => setForm({ ...form, product_name: e.target.value })} placeholder="e.g. Walnut Coffee Table" />
                </div>
                <div className="form-row">
                  <div className="field">
                    <label>Price (USD)</label>
                    <input type="number" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} placeholder="0.00" />
                  </div>
                  <div className="field">
                    <label>Quantity</label>
                    <input type="number" value={form.quantity} onChange={e => setForm({ ...form, quantity: e.target.value })} placeholder="0" />
                  </div>
                </div>
                <div className="field">
                  <label>Image URL</label>
                  <input value={form.img} onChange={e => setForm({ ...form, img: e.target.value })} placeholder="https://..." />
                </div>
                <div className="field">
                  <label>Description</label>
                  <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Describe this piece…" />
                </div>
              </div>
              <div className="modal-footer">
                <button className="btn-cancel" onClick={closeModal}>Cancel</button>
                <button className="btn-save" onClick={handleSave}>{modal === "add" ? "Add to Catalog" : "Save Changes"}</button>
              </div>
            </div>
          </div>
        )}

        {/* VIEW MODAL */}
        {modal === "view" && (
          <div className="overlay" onClick={closeModal}>
            <div className="modal" onClick={e => e.stopPropagation()}>
              <div className="modal-header">
                <div>
                  <div className="modal-title">{form.product_name}</div>
                  <div className="modal-subtitle">Product Details</div>
                </div>
                <button className="modal-close" onClick={closeModal}>×</button>
              </div>
              {form.img
                ? <img src={form.img} alt={form.product_name} className="view-img" />
                : <div className="view-img-placeholder"><svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1"><rect x="3" y="10" width="18" height="8" rx="1" /><rect x="6" y="6" width="12" height="4" rx="1" /></svg></div>
              }
              <div className="view-body">
                <div className="view-price">${Number(form.price).toLocaleString("en-US", { minimumFractionDigits: 2 })}</div>
                <div className="view-desc">{form.description || "No description."}</div>
                <div className="view-meta">
                  <div className="view-meta-item"><label>Stock</label><span>{form.quantity} units</span></div>
                  <div className="view-meta-item"><label>ID</label><span>#{form.id}</span></div>
                </div>
              </div>
              <div className="modal-footer">
                <button className="btn-cancel" onClick={closeModal}>Close</button>
                <button className="btn-save" onClick={() => { closeModal(); setTimeout(() => openEdit(form), 50); }}>Edit This Piece</button>
              </div>
            </div>
          </div>
        )}

        {/* DELETE CONFIRM */}
        {deleteConfirm && (
          <div className="overlay" onClick={() => setDeleteConfirm(null)}>
            <div className="modal confirm-modal" onClick={e => e.stopPropagation()}>
              <h3>Remove this piece?</h3>
              <p>
                <strong style={{ color: "var(--walnut)" }}>{deleteConfirm.product_name}</strong> will be permanently removed from the catalog. This action cannot be undone.
              </p>
              <div className="confirm-actions">
                <button className="btn-cancel" onClick={() => setDeleteConfirm(null)}>Keep It</button>
                <button className="btn-confirm-del" onClick={() => handleDelete(deleteConfirm.id)}>Yes, Remove</button>
              </div>
            </div>
          </div>
        )}

        {/* TOAST */}
        {toast && (
          <div className={`toast ${toast.type}`}>
            {toast.type === "success" ? "✓" : "✕"} {toast.msg}
          </div>
        )}
      </div>
    </>
  );
}
