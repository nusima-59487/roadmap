package me.nusimucat.roadmap.util;

public class Result<T, E> {
    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T, E> Result<T, E> err(E error) {
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return value != null;
    }

    public boolean isErr() {
        return error != null;
    }

    public T getValue() {
        return value;
    }

    public T getValueOrDefault (T default_value) {
        return this.isOk() ? this.getValue() : default_value; 
    }

    public E getError() {
        return error;
    }
}
