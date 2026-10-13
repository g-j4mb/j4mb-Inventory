
import type { FormEvent } from 'react';
import type { CreateProductRequest } from '../../types/product';

type ProductFormProps = {
  mode: 'create' | 'edit';
  formData: CreateProductRequest;
  onChange: (data: CreateProductRequest) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  onCancel: () => void;
  saving: boolean;
  error: string | null;
};

export default function ProductForm({
  mode,
  formData,
  onChange,
  onSubmit,
  onCancel,
  saving,
  error,
}: ProductFormProps) {
  const isEditing = mode === 'edit';

  function updateField<K extends keyof CreateProductRequest>(
    field: K,
    value: CreateProductRequest[K],
  ) {
    onChange({ ...formData, [field]: value });
  }

  return (
    <section className="table-card create-form-card">
      <div className="table-header">
        <div>
          <h3>{isEditing ? 'Edit Product' : 'Create Product'}</h3>
          <p>
            {isEditing
              ? 'Update the editable product details.'
              : 'Enter the details for the new inventory item.'}
          </p>
        </div>
      </div>

      <form onSubmit={onSubmit} className="product-form">
        <label>
          SKU *
          <input
            required
            maxLength={64}
            value={formData.sku}
            readOnly={isEditing}
            onChange={(e) => updateField('sku', e.target.value)}
          />
        </label>

        <label>
          Product Name *
          <input
            required
            maxLength={150}
            value={formData.name}
            onChange={(e) => updateField('name', e.target.value)}
          />
        </label>

        <label>
          Description
          <input
            value={formData.description}
            onChange={(e) => updateField('description', e.target.value)}
          />
        </label>

        <label>
          Unit of Measure *
          <input
            required
            maxLength={20}
            value={formData.unitOfMeasure}
            onChange={(e) =>
              updateField('unitOfMeasure', e.target.value)
            }
          />
        </label>

        <label>
          Unit Cost *
          <input
            required
            type="number"
            min="0"
            step="0.01"
            value={formData.unitCost}
            onChange={(e) =>
              updateField('unitCost', Number(e.target.value))
            }
          />
        </label>

        <label>
          Quantity on Hand *
          <input
            required
            type="number"
            min="0"
            step="1"
            value={formData.quantityOnHand}
            readOnly={isEditing}
            onChange={(e) =>
              updateField('quantityOnHand', Number(e.target.value))
            }
          />
        </label>

        <label>
          Reorder Level *
          <input
            required
            type="number"
            min="0"
            step="1"
            value={formData.reorderLevel}
            onChange={(e) =>
              updateField('reorderLevel', Number(e.target.value))
            }
          />
        </label>

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}

        <div className="form-actions">
          <button
            type="button"
            className="secondary-button"
            disabled={saving}
            onClick={onCancel}
          >
            Cancel
          </button>

          <button
            type="submit"
            className="primary-button"
            disabled={saving}
          >
            {saving
              ? 'Saving...'
              : isEditing
                ? 'Update Product'
                : 'Save Product'}
          </button>
        </div>
      </form>
    </section>
  );
}
