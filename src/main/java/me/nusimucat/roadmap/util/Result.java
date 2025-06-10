package me.nusimucat.roadmap.util;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the <code>Result</code> type in rust. First value is the Ok value, second is the error value. 
 */
public class Result<T, E> {

    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    /**
     * Returns with Ok value
     * @param <T> Class of ok value
     * @param <E> Class of error value
     * @param value Value to return 
     * @return Result type
     */
    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(Objects.requireNonNull(value), null);
    }

    /**
     * Returns with Error value
     * @param <T> Class of ok value
     * @param <E> Class of error value
     * @param value Value to return 
     * @return Result type
     */
    public static <T, E> Result<T, E> err(E error) {
        return new Result<>(null, Objects.requireNonNull(error));
    }

    public boolean isOk() {
        return value != null;
    }

    public boolean isErr() {
        return error != null;
    }

    /**
     * Gets the Ok value. If it is not Ok, throws Exception containing the Error value
     * @return The Ok value 
     * @throws Exception If Result is not Ok
     */
    public T getValueOrThrow () throws Exception {
        if (this.isOk()) return value; 
        else {
            String exceptionMessage = this.isErr() ? this.getError().toString() : "Something went wrong"; 
            throw new Exception(exceptionMessage); 
        }
    }

    /**
     * Gets the ok value as optional
     * @return Optional
     */
    public Optional<T> getValueAsOptional () {
        return Optional.ofNullable(value); 
    }

    /**
     * Gets the Ok value. If it is not Ok, the default value would be returned
     * @param default_value Value to return if Err
     * @return
     */
    public T getValueOrDefault (T default_value) {
        return this.isOk() ? value : default_value; 
    }

    /**
     * Returns the error value. If there is no error, returns null
     * @return
     */
    public E getError() {
        return error;
    }
}
