package com.library.library_management.service.impl;

import com.library.library_management.dto.book.BookResponse;
import com.library.library_management.dto.book.CreateBookRequest;
import com.library.library_management.dto.book.UpdateBookRequest;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Book;
import com.library.library_management.entity.Enums;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.DuplicateResourceException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {

            throw new DuplicateResourceException("isbn", "ISBN already exists");
        }
        if (request.publishYear() < 1000 || request.publishYear() > LocalDateTime.now().getYear()) {

            throw new BusinessException("Invalid publish year");
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
        book.setBookCode(generateBookCode());
        book.setAvailableCopies(request.totalCopies());
        book.setStatus(Enums.BookStatus.ACTIVE);
        book.setCreatedAt(LocalDateTime.now());
        book.setCreatedBy(getCurrentUser());
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }
    @Override
    public BookResponse getById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Book not found")
                );

        return mapToResponse(book);
    }


    @Override
    public BookResponse getByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return mapToResponse(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {

        return bookRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        bookRepository.delete(book);

    }

    @Override
    public BookResponse getByBookCode(String bookCode) {
        Book book = bookRepository.findByBookCode(bookCode).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return mapToResponse(book);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {

        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

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
            if (request.publishYear() < 1000 || request.publishYear() > LocalDateTime.now().getYear()) {

                throw new BusinessException("Invalid publish year");
            }
            book.setPublishYear(request.publishYear());
        }

        if (request.price() != null) {
            book.setPrice(request.price());
        }

        if (request.status() != null) {
            book.setStatus(request.status());
        }

        if (request.totalCopies() != null) {

            int newTotalCopies = request.totalCopies();
            int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

            if (newTotalCopies < borrowedCopies) {
                throw new BusinessException("Cannot reduce total copies below borrowed copies");

            }

            int difference = newTotalCopies - book.getTotalCopies();
            int newAvailableCopies = book.getAvailableCopies() + difference;

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

            code = String.valueOf((long) (Math.random() * 90000000000000L + 10000000000000L));

        } while (bookRepository.existsByBookCode(code));

        return code;
    }

    private AppUser getCurrentUser() {

        return appUserRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(() -> new BusinessException("User not found"));
    }

    private BookResponse mapToResponse(Book book) {

        return new BookResponse(book.getId(), book.getBookCode(), book.getIsbn(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getPublisher(), book.getPublishYear(), book.getTotalCopies(), book.getAvailableCopies(),
                book.getTotalCopies() - book.getAvailableCopies(),
                book.getPrice(), book.getStatus(), book.getCreatedAt(), book.getUpdatedAt());
    }

}