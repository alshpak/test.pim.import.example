package example;

import example.ExampleModel.*;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Example1Strict {

    private static class CaseDependant<DTO, ENTITY, PARENT> extends Case<DTO, ENTITY> {

        Case<DTO, PARENT> parentCase;
        Function<PARENT, Case<DTO, ENTITY>> parentMap;

        @Override
        ENTITY apply(DTO dto) {
            PARENT parentEntity = parentCase.apply(dto);
            return parentMap.apply(parentEntity).apply(dto);
        }
    }

    private static class Case<DTO, ENTITY> {

        Function<DTO, ENTITY> convert;

        static <DTO, ENTITY> Case<DTO, ENTITY> bind(Function<DTO, ENTITY> convert) {
            Case<DTO, ENTITY> cs = new Case<>();
            cs.convert = convert;
            return cs;
        }

        <R> Case<DTO, R> flatMap(Function<ENTITY, Case<DTO, R>> map) {

            var sub = new CaseDependant<DTO, R, ENTITY>();
            sub.parentCase = this;
            sub.parentMap = map;
            return sub;
        }

        static <DTO, E1, E2, R> Case<DTO, R> concat(Case<DTO, E1> c1, Case<DTO, E2> c2, BiFunction<E1, E2, R> combine) {
            return null;
        }

        ENTITY apply(DTO dto) {
            return convert.apply(dto);
        }
    }


    public static void main(String[] args) {

        // 1. find:   DTO -> Optional[Entity] <- here is enough information
        // 2. create: DTO + something -> Entity

        // Simple case

        class BrandAndCateg {
            Brand brand;
            Category category;

            BrandAndCateg(Brand brand, Category category) {
                this.brand = brand;
                this.category = category;
            }
        }

        var caseCateg = Case.bind((DTO dto) -> ExampleRepo.getOrCreateCategory(dto.category()));
        var caseBrand = Case.bind((DTO dto) -> ExampleRepo.getOrCreateBrand(dto.brand()));
        Function<BrandAndCateg, Case<DTO, Product>> caseProduct = data ->
                Case.bind(dto -> ExampleRepo.getOrCreateProduct(dto.product(), data.category, data.brand));
        Function<Product, Case<DTO, ProductVariation>> caseVariation = prod ->
                Case.bind(dto -> ExampleRepo.getOrCreateVariation(dto.productVariation(), prod));

        Case<DTO, ?> importDto = Case.concat(
                caseCateg,
                caseBrand,
                (ca, br) -> new BrandAndCateg(br, ca))
                .flatMap(caseProduct)
                .flatMap(caseVariation);

        Case<DTO, ?> importDto2 = Case.concat(
                Case.bind(dto -> ExampleRepo.getOrCreateCategory(dto.category())),
                Case.bind((DTO dto) -> ExampleRepo.getOrCreateBrand(dto.brand())),
                (ca, br) -> new BrandAndCateg(br, ca))
                .flatMap(data -> Case.bind(dto -> ExampleRepo.getOrCreateProduct(dto.product(), data.category, data.brand)))
                .flatMap(prod -> Case.bind(dto -> ExampleRepo.getOrCreateVariation(dto.productVariation(), prod)));

        DTO dto = null;
        importDto.apply(dto);


        Case.bind(ExampleRepo.getOrCreateProduct(null, null));
    }


}
