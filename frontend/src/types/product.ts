export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string | null;
  unitOfMeasure: string;
  unitCost: number;
  quantityOnHand: number;
  reorderLevel: number;
  active: boolean;
}

export interface ProductPage {
  products: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
export interface CreateProductRequest {
  sku: string;
  name: string;
  description: string;
  unitOfMeasure: string;
  unitCost: number;
  quantityOnHand: number;
  reorderLevel: number;
}

export interface UpdateProductRequest {
  name: string;
  description: string;
  unitOfMeasure: string;
  unitCost: number;
  reorderLevel: number;
}