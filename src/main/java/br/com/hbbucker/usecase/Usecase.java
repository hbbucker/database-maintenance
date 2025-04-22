package br.com.hbbucker.usecase;

public interface Usecase<E, T> {
    T execute(E entity);
}
