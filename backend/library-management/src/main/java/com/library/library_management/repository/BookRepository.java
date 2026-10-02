package com.library.library_management.repository;

import com.library.library_management.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByBookCode(String bookCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b
            from Book b
            where b.bookCode = :bookCode
            """)
    Optional<Book> findByBookCodeForUpdate(@Param("bookCode") String bookCode);

    boolean existsByIsbn(String isbn);

    boolean existsByBookCode(String bookCode);
}