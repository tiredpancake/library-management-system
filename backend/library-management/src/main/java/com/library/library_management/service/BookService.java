package com.library.library_management.service;

import com.library.library_management.dto.book.BookResponse;
import com.library.library_management.dto.book.CreateBookRequest;
import com.library.library_management.dto.book.UpdateBookRequest;

public interface BookService {

    BookResponse createBook(CreateBookRequest request);

    BookResponse getByIsbn(String isbn);

    BookResponse getByBookCode(String bookCode);

    BookResponse updateBook(Long id, UpdateBookRequest request);

}