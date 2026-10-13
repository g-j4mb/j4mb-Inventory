
import type { Product } from '../../types/product';

type ProductTableProps = {
  products: Product[];
  loading: boolean;
  error: string | null;
  search: string;
  onSearchChange: (value: string) => void;
  onEdit: (product: Product) => void;
  onDelete: (product: Product) => void;
  page: number;
totalPages: number;
totalElements: number;
onPageChange: (page: number) => void;
};

export default function ProductTable({
  products,
  loading,
  error,
  search,
  onSearchChange,
  onEdit,
  onDelete,
  page,
  totalPages,
  totalElements,
  onPageChange,
}: ProductTableProps) {
  return (
    <section className="table-card">
      <div className="table-header">
        <div>
          <h3>Product List</h3>
          <p>View and search your registered products.</p>
        </div>

        <input
          className="search-input"
          type="search"
          placeholder="Search products..."
          value={search}
          onChange={(event) => onSearchChange(event.target.value)}
        />
      </div>

      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>SKU</th>
              <th>Product</th>
              <th>Description</th>
              <th>Unit</th>
              <th>Quantity</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {loading && (
              <tr>
                <td colSpan={7} className="empty-state">
                  Loading products...
                </td>
              </tr>
            )}

            {!loading && error && (
              <tr>
                <td colSpan={7} className="empty-state">
                  {error}
                </td>
              </tr>
            )}

            {!loading && !error && products.map((product) => {
              const lowStock =
                product.quantityOnHand <= product.reorderLevel;

              return (
                <tr key={product.id}>
                  <td className="sku">{product.sku}</td>
                  <td className="product-name">{product.name}</td>
                  <td>{product.description || '—'}</td>
                  <td>{product.unitOfMeasure}</td>
                  <td>{product.quantityOnHand}</td>
                  <td>
                    <span
                      className={`stock-badge ${
                        lowStock ? 'low-stock' : 'in-stock'
                      }`}
                    >
                      {lowStock ? 'Low stock' : 'In stock'}
                    </span>
                  </td>
                  <td>
                    <div className="product-actions">
                      <button
                        type="button"
                        className="edit-button"
                        onClick={() => onEdit(product)}
                        disabled={loading}
                      >
                        Edit
                      </button>

                      <button
                        type="button"
                        className="delete-button"
                        onClick={() => onDelete(product)}
                        disabled={loading}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}

            {!loading && !error && products.length === 0 && (
              <tr>
                <td colSpan={7} className="empty-state">
                  No products found. Add your first product to get started.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

     <div className="table-footer pagination-footer">
  <span>
    {loading
      ? 'Loading products...'
      : error
        ? 'Products could not be loaded'
        : `Showing ${products.length} of ${totalElements} products`}
  </span>

  <div className="pagination-controls">
    <button
      type="button"
      className="secondary-button"
      disabled={loading || page <= 0}
      onClick={() => onPageChange(page - 1)}
    >
      Previous
    </button>

    <span className="page-indicator">
      Page {totalPages === 0 ? 0 : page + 1} of {totalPages}
    </span>

    <button
      type="button"
      className="secondary-button"
      disabled={loading || page + 1 >= totalPages}
      onClick={() => onPageChange(page + 1)}
    >
      Next
    </button>
  </div>
</div>
    </section>
  );
}
