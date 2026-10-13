
import { useCallback, useEffect, useState } from 'react';
import { getProducts } from '../services/productApi';
import type { Product } from '../types/product';

export function useProducts() {
  const [products, setProducts] = useState<Product[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [pageSize, setPageSize] = useState(2);

  const refreshProducts = useCallback(() => {
    setRefreshKey((current) => current + 1);
  }, []);
useEffect(() => {
  setPage(0);
}, [search]);
  useEffect(() => {
    const controller = new AbortController();

    async function loadProducts() {
      setLoading(true);
      setError(null);

      try {
        const result = await getProducts(
            search,
            page,
            pageSize,
            controller.signal,
            );
        setProducts(result.products);
        setTotalPages(result.totalPages);
        setTotalElements(result.totalElements);
      } catch (err) {
        if (err instanceof Error && err.name === 'AbortError') {
          return;
        }

        setError(
          err instanceof Error
            ? err.message
            : 'Unable to load products.',
        );
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    const timeout = window.setTimeout(() => {
      void loadProducts();
    }, 300);

    return () => {
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, [search,page, pageSize, refreshKey]);

  return {
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
};
}
