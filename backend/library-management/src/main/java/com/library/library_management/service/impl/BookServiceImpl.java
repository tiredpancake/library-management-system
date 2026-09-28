package com.library.library_management.service.impl;

import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.library.library_management.dto.book.BookResponse;
import com.library.library_management.dto.book.CreateBookRequest;
import com.library.library_management.dto.book.UpdateBookRequest;
import com.library.library_management.entity.Book;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Enums;
import com.library.library_management.exception.DuplicateResourceException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {


    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;


    @Override
    public BookResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {

            throw new DuplicateResourceException(
                    "Book with this ISBN already exists"
            );
        }


        Book book = new Book();


        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setCategory(request.category());
        book.setPublisher(request.publisher());
        book.setPublishYear(request.publishYear());
        book.setTotalCopies(request.totalCopies());
        book.setPrice(request.price());


        book.setBookCode(
                generateBookCode()
        );


        book.setAvailableCopies(
                request.totalCopies()
        );


        book.setStatus(
                Enums.BookStatus.ACTIVE
        );


        book.setCreatedAt(
                LocalDateTime.now()
        );


        book.setCreatedBy(
                getCurrentUser()
        );


        Book savedBook = bookRepository.save(book);


        return mapToResponse(savedBook);    }


    @Override
    public BookResponse getByIsbn(String isbn) {
        Book book = bookRepository
                .findByIsbn(isbn)
                .orElseThrow(
                        () -> new RuntimeException("Book not found")
                );

        return mapToResponse(book);
    }


    @Override
    public BookResponse getByBookCode(String bookCode) {
        Book book = bookRepository
                .findByBookCode(bookCode)
                .orElseThrow(
                        () -> new RuntimeException("Book not found")
                );

        return mapToResponse(book);
    }


    @Override
    public BookResponse updateBook(Long id, UpdateBookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Book not found")
                );


        if (request.title() != null) {
            book.setTitle(request.title());
        }


        if (request.author() != null) {
            book.setAuthor(request.author());
        }


        if (request.category() != null) {
            book.setCategory(request.category());
        }


        if (request.publisher() != null) {
            book.setPublisher(request.publisher());
        }


        if (request.publishYear() != null) {
            book.setPublishYear(request.publishYear());
        }


        if (request.price() != null) {
            book.setPrice(request.price());
        }


        if (request.status() != null) {
            book.setStatus(request.status());
        }

        if (request.totalCopies() != null) {

            int oldTotalCopies = book.getTotalCopies();

            int newTotalCopies = request.totalCopies();


            if (newTotalCopies < 0) {
                throw new RuntimeException(
                        "Total copies cannot be negative"
                );
            }


            int difference = newTotalCopies - oldTotalCopies;


            int newAvailableCopies =
                    book.getAvailableCopies() + difference;


            if (newAvailableCopies < 0) {

                throw new RuntimeException(
                        "Cannot reduce copies because some books are borrowed"
                );
            }


            book.setTotalCopies(newTotalCopies);

            book.setAvailableCopies(newAvailableCopies);
        }



        book.setUpdatedAt(LocalDateTime.now());


        Book savedBook = bookRepository.save(book);


        return mapToResponse(savedBook);
    }
    private String generateBookCode() {

        String code;

        do {

            code = String.valueOf(
                    (long)(Math.random() * 90000000000000L
                            + 10000000000000L)
            );

        } while (
                bookRepository.existsByBookCode(code)
        );


        return code;
    }

    private AppUser getCurrentUser() {

        return appUserRepository
                .findByUsername("admin")
                .orElseThrow(
                        () -> new RuntimeException("Admin user not found")
                );
    }

    private BookResponse mapToResponse(Book book) {

        return new BookResponse(

                book.getId(),
                book.getBookCode(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPublisher(),
                book.getPublishYear(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getPrice(),
                book.getStatus(),
                book.getCreatedAt(),
                book.getUpdatedAt()

        );
    }

}