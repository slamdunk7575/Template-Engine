package com.yanggang.exception;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionFunction<T, R> {
    R apply(T t) throws Exception;

    public static <T, R> Function<T, R> wrap(ExceptionFunction<T, R> f) {
        return (T t) -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}