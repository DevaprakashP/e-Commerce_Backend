package com.deva.dream_shops.service.product;

import com.deva.dream_shops.dto.ProductDto;
import com.deva.dream_shops.model.Product;
import com.deva.dream_shops.request.AddProductRequest;
import com.deva.dream_shops.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productid);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand,String name);
    Long countProductsByBrandAndName(String brand,String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    public ProductDto convertToDto(Product product);

}
