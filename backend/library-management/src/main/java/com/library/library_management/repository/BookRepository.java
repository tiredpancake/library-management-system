package com.library.library_management.repository;

import com.library.library_management.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    Optional<Book> findByBookCode(String bookCode);
    boolean existsByIsbn(String isbn);
    boolean existsByBookCode(String bookCode);

}