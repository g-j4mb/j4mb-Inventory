import type { CreateProductRequest, ProductPage, UpdateProductRequest } from '../types/product';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function getProducts(
  search = '',
    page = 0,
  size = 2,
  signal?: AbortSignal,
): Promise<ProductPage> {
   const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    activeOnly: 'true',
  });

  if (search.trim()) {
    params.set('search', search.trim());
   
  }
 params.set('activeOnly','true');
  const response = await fetch(
    `${API_BASE_URL}/products?${params.toString()}`,
    { signal },
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load products: HTTP ${response.status}`,
    );
  }

  return (await response.json()) as ProductPage;
}

export async function createProduct(
  product: CreateProductRequest,
): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(product),
  });

  if (!response.ok) {
    const message = await response.text();

    throw new Error(
      message || `Failed to create product: HTTP ${response.status}`,
    );
  }
}

export async function updateProduct(
  id: number,
  product: UpdateProductRequest,
): Promise<void> {
  const response = await fetch(
    `${API_BASE_URL}/products/${id}`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(product),
    },
  );

  if (!response.ok) {
    const message = await response.text();

    throw new Error(
      message || `Failed to update product: HTTP ${response.status}`,
    );
  }
}

export async function deleteProduct(id: number): Promise<void> {
  const response = await fetch(
    `${API_BASE_URL}/products/${id}`,
    {
      method: 'DELETE',
    },
  );

  if (!response.ok) {
    const message = await response.text();

    throw new Error(
      message || `Failed to delete product: HTTP ${response.status}`,
    );
  }
}