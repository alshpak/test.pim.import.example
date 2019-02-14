package example;

import example.ExampleModel.Brand;
import example.ExampleModel.Category;
import example.ExampleModel.DTO;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Example2Definitive {

    /*
    private static class RootDef<E, D, A> implements Def<E, D, A> {
        Function<D, A> access;
        Function<A, Optional<E>> search;
        Function<A, E> create;

        public E apply(D dto) {
            A data = access.apply(dto);
            return search.apply(data).orElseGet(() -> create.apply(data));
        }
    }

    private static class NestedDef<E, D, A, PE, PA> implements Def<E, D, A> {
        Function<D, A> access;
        Function<A, Optional<E>> search;
        BiFunction<A, PE, E> create;
        Def<PE, D, PA> parent;

        @Override
        public E apply(D dto) {
            A data = access.apply(dto);
            return search.apply(data).orElseGet(() -> create.apply(data, parent.apply(dto)));
        }
    }
    */

    private interface Def<E, D, A> {

        E apply(D dto);

        static <E, D, A> Def<E, D, A> define(Function<D, A> access, Function<A, Optional<E>> search, Function<A, E> create) {
            return dto -> {
                A data = access.apply(dto);
                return search.apply(data).orElseGet(() -> create.apply(data));
            };
        }


        static <E1, E2, D, RE> Def<RE, D, ?> concat(Def<E1, D, ?> def1, Def<E2, D, ?> def2, BiFunction<E1, E2, RE> combine) {
            return dto -> combine.apply(def1.apply(dto), def2.apply(dto));
        }

        default <ER, AR> Def<ER, D, AR> map(Function<D, AR> access, Function<AR, Optional<ER>> search, BiFunction<AR, E, ER> create) {
            return dto -> {
                AR data = access.apply(dto);
                return search.apply(data).orElseGet(() -> create.apply(data, this.apply(dto)));
            };
        }

                /*
        static <E, D, A> Def<E, D, A> define(Function<D, A> access, Function<A, Optional<E>> search, Function<A, E> create) {
            RootDef<E, D, A> def = new RootDef<>();
            def.access = access;
            def.search = search;
            def.create = create;
            return def;
        }


        default <ER, AR> Def<ER, D, AR> map(Function<D, AR> access, Function<AR, Optional<ER>> search, BiFunction<AR, E, ER> create) {
            NestedDef<ER, D, AR, E, A> nestedDef = new NestedDef<>();
            nestedDef.access = access;
            nestedDef.search = search;
            nestedDef.create = create;
            nestedDef.parent = this;
            return nestedDef;
        }
        */

    }


    public static void main(String... args) {


        Def<?, DTO, ?> importDef = Def
                .concat(
                        Def.define(DTO::brand, ExampleRepo::findBrand, ExampleRepo::getOrCreateBrand),
                        Def.define(DTO::category, ExampleRepo::findCategory, ExampleRepo::getOrCreateCategory),
                        BrandAndCateg::new)
                .map(DTO::product, ExampleRepo::findProduct,
                        (dto, brandAndCateg) -> ExampleRepo.getOrCreateProduct(dto, brandAndCateg.category(), brandAndCateg.brand()))
                .map(DTO::productVariation, ExampleRepo::findVariation,
                        ExampleRepo::getOrCreateVariation);

        DTO dto = null;
        importDef.apply(dto);
    }

    static class BrandAndCateg {
        BrandAndCateg(Brand brand, Category category) {

        }

        Brand brand() {
            return null;
        }

        Category category() {
            return null;
        }
    }
}
