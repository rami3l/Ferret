package fr.ferret.utils;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T v) throws Exception;
}
