
import { useState } from 'react';
import type { FormEvent } from 'react';

import './App.css';

import Sidebar from './components/layout/Sidebar';
import ProductForm from './components/products/ProductForm';
import ProductTable from './components/products/ProductTable';

import { useProducts } from './hooks/useProducts';
import {   createProduct,
  updateProduct,
  deleteProduct, } from './services/productApi';

import type {   CreateProductRequest,
  Product,
  UpdateProductRequest, } from './types/product';
import LoginForm from './components/auth/LoginForm';
import type { LoginResponse } from './services/authApi';
function App() {
  const [auth, setAuth] = useState<LoginResponse | null>(() => {
  const savedToken = sessionStorage.getItem('accessToken');

  if (!savedToken) {
    return null;
  }

  return {
    accessToken: savedToken,
    tokenType: 'Bearer',
    expiresIn: 900,
  };
});
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const emptyForm: CreateProductRequest = {
    sku: '',
    name: '',
    description: '',
    unitOfMeasure: 'PCS',
    unitCost: 0,
    quantityOnHand: 0,
    reorderLevel: 0,
  };

  const [activePage, setActivePage] = useState('Products');

 const {
  products,
  search,
  setSearch,
  loading,
  error,
  refreshProducts,
  page,
  setPage,
  pageSize,
  setPageSize,
  totalPages,
  totalElements,
} = useProducts(auth?.accessToken ?? '');

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] =
    useState<CreateProductRequest>({ ...emptyForm });
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const lowStockCount = products.filter(
    (product) => product.quantityOnHand <= product.reorderLevel,
  ).length;

 async function handleCreateProduct(event: FormEvent<HTMLFormElement>) {
  event.preventDefault();
  setSaving(true);
  setFormError(null);
  setSuccessMessage(null);

  try {
    if (editingProduct) {
      const updateData: UpdateProductRequest = {
        name: formData.name,
        description: formData.description,
        unitOfMeasure: formData.unitOfMeasure,
        unitCost: formData.unitCost,
        reorderLevel: formData.reorderLevel,
      };

      await updateProduct(editingProduct.id, updateData,auth!.accessToken);
      setSuccessMessage('Product updated successfully.');
    } else {
      await createProduct(formData,auth!.accessToken);
      setSuccessMessage('Product created successfully.');
    }

    setShowCreateForm(false);
    setEditingProduct(null);
    setFormData({ ...emptyForm });
    refreshProducts();
  } catch (err) {
    setFormError(
      err instanceof Error ? err.message : 'Unable to save product.',
    );
  } finally {
    setSaving(false);
  }
}

  function openCreateForm() {
    setFormData({ ...emptyForm });
    setFormError(null);
    setSuccessMessage(null);
    setShowCreateForm(true);
  }

function handleEditProduct(product: Product) {
  setEditingProduct(product);

  setFormData({
    sku: product.sku,
    name: product.name,
    description: product.description ?? '',
    unitOfMeasure: product.unitOfMeasure,
    unitCost: product.unitCost,
    quantityOnHand: product.quantityOnHand,
    reorderLevel: product.reorderLevel,
  });

  setFormError(null);
  setSuccessMessage(null);
  setShowCreateForm(true);
}

async function handleDeleteProduct(product: Product) {
  const confirmed = window.confirm(
    `Are you sure you want to delete "${product.name}"?`,
  );

  if (!confirmed) return;

  setFormError(null);
  setSuccessMessage(null);

  try {
    await deleteProduct(product.id,auth!.accessToken);
    setSuccessMessage('Product deleted successfully.');
    refreshProducts();
  } catch (err) {
    setFormError(
      err instanceof Error ? err.message : 'Unable to delete product.',
    );
  }
}
if (!auth) {
  return (
    <LoginForm
       onLoginSuccess={(result) => {
        sessionStorage.setItem('accessToken', result.accessToken);
        setAuth(result);
      }}
    />
  );
}
  return (
    
    <div className="app-layout">
      <Sidebar
        activePage={activePage}
        onNavigate={setActivePage}
      />

      <main className="main-content">
        <header className="topbar">
          <span>Workspace / {activePage}</span>

          <span className="system-status">
            <span className="status-dot" />
            Development
          </span>
        </header>

        <div className="page-content">
          <div className="page-heading">
            <div>
              <p className="eyebrow">INVENTORY MANAGEMENT</p>
              <h2>{activePage}</h2>
              <p className="page-description">
                Manage and monitor your inventory operations.
              </p>
            </div>

            {activePage === 'Products' && !showCreateForm && (
              <button
                className="primary-button"
                onClick={openCreateForm}
              >
                <span>+</span> Add Product
              </button>
            )}
          </div>

          {successMessage && (
            <p className="success-message" role="status">
              {successMessage}
            </p>
          )}

          {activePage === 'Products' && (
            <>
              {showCreateForm && (
                <ProductForm
                  mode={editingProduct ? 'edit' : 'create'}
                  formData={formData}
                  onChange={setFormData}
                  onSubmit={handleCreateProduct}
                  onCancel={() => setShowCreateForm(false)}
                  saving={saving}
                  error={formError}
                />
              )}

              <section className="summary-grid">
                <article className="summary-card">
                  <div className="summary-label">Total Products</div>
                  <div className="summary-value">
                    {totalElements}
                  </div>
                  <div className="summary-note">
                    Products on this page
                  </div>
                </article>

                <article className="summary-card">
                  <div className="summary-label">Low Stock Items</div>
                  <div className="summary-value warning">
                    {lowStockCount}
                  </div>
                  <div className="summary-note">
                    At or below reorder level
                  </div>
                </article>

                <article className="summary-card">
                  <div className="summary-label">Inventory Status</div>
                  <div className="summary-value">
                    {loading ? 'Loading' : error ? 'Error' : 'Active'}
                  </div>
                  <div className="summary-note">
                    {error ? 'Unable to load products' : 'Backend connected'}
                  </div>
                </article>
              </section>

              <ProductTable
                 products={products}
  loading={loading}
  error={error}
  search={search}
  onSearchChange={setSearch}
  onEdit={handleEditProduct}
  onDelete={handleDeleteProduct}
  page={page}
  totalPages={totalPages}
  totalElements={totalElements}
  onPageChange={setPage}
              />
            </>
          )}

          {activePage !== 'Products' && (
            <section className="table-card">
              <div className="table-header">
                <div>
                  <h3>{activePage}</h3>
                  <p>This module has not been implemented yet.</p>
                </div>
              </div>
            </section>
          )}

          <p className="demo-notice">
            Inventory Management System — Development Environment
          </p>
        </div>
      </main>
    </div>
  );
}

export default App;
