package example;

import example.ExampleModel.*;

import java.util.Optional;
import java.util.function.Function;

public class ExampleRepo {

    static Optional<Category> findCategory(CategoryDTO dto) {
        return null;
    }

    static Optional<Brand> findBrand(BrandDTO dto) {
        return null;
    }

    static Optional<Product> findProduct(ProductDTO dto) {
        return null;
    }

    static Optional<ProductVariation> findVariation(ProductVariationDTO dto) {
        return null;
    }


    static Category getOrCreateCategory(CategoryDTO dto) {
        return null;
    }

    static Brand getOrCreateBrand(BrandDTO dto) {
        return null;
    }

    static Product getOrCreateProduct(ProductDTO dto, Category category, Brand brand) {
        return null;
    }

    static Function<ProductDTO, Product> getOrCreateProduct(Category category, Brand brand) {
        return null;
    }

    static ProductVariation getOrCreateVariation(ProductVariationDTO dto, Product product) {
        return null;
    }

    static Function<ProductVariationDTO, ProductVariation> getOrCreateVariation(Product product) {
        return null;
    }


}
