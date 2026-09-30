package com.library.library_management.controller;

import com.library.library_management.dto.book.BookResponse;
import com.library.library_management.dto.book.CreateBookRequest;
import com.library.library_management.dto.book.UpdateBookRequest;
import com.library.library_management.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;


    @PostMapping
    public BookResponse createBook(@RequestBody @Valid CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping("/isbn/{isbn}")
    public BookResponse getByIsbn(@PathVariable String isbn) {
        return bookService.getByIsbn(isbn);
    }

    @GetMapping("/code/{bookCode}")
    public BookResponse getByBookCode(@PathVariable String bookCode) {
        return bookService.getByBookCode(bookCode);
    }


    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable Long id, @RequestBody @Valid UpdateBookRequest request) {
        return bookService.updateBook(id, request);
    }

    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();

    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
    }
}