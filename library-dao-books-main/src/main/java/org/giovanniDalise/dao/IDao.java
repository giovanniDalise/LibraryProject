package org.giovanniDalise.dao;

import org.giovanniDalise.dto.Book;
import org.giovanniDalise.exception.DaoException;

import java.util.List;

public interface IDao<T,I>{
    I create(T t) throws DaoException;
    List<T> read()throws DaoException;
    List<T> findByText(String searchText)throws DaoException;
    I delete(I idElement)throws DaoException;
    I update(I idElement, T t)throws DaoException;
    T getById(Long index) throws DaoException;
    List<T> findByObject(Book searchBook) throws DaoException;


    }
