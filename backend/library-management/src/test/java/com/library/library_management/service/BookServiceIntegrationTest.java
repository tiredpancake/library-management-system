package com.library.library_management.service;

import com.library.library_management.dto.book.BookResponse;
import com.library.library_management.dto.book.CreateBookRequest;
import com.library.library_management.dto.book.UpdateBookRequest;
import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Book;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Member;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.DuplicateResourceException;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testUser;

    @BeforeEach
    void setUp() {

        String username = "book-test-" + UUID.randomUUID();

        testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("test-password");

        testUser = appUserRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
    }

    @Test
    void createBookShouldGenerateFourteenDigitBookCode() {

        CreateBookRequest request = createBookRequest(generateNumericString(14), 5);

        BookResponse response = bookService.createBook(request);

        assertNotNull(response.id());

        assertNotNull(response.bookCode());

        assertTrue(response.bookCode().matches("\\d{14}"));

        assertEquals(5, response.totalCopies());

        assertEquals(5, response.availableCopies());

        assertEquals(0, response.borrowedCopies());

        assertEquals(Enums.BookStatus.ACTIVE, response.status());
    }

    @Test
    void generatedBookCodesShouldBeUnique() {

        BookResponse first = bookService.createBook(createBookRequest(generateNumericString(14), 1));

        BookResponse second = bookService.createBook(createBookRequest(generateNumericString(14), 1));

        assertNotEquals(first.bookCode(), second.bookCode());

        assertTrue(first.bookCode().matches("\\d{14}"));

        assertTrue(second.bookCode().matches("\\d{14}"));
    }

    @Test
    void duplicateIsbnShouldBeRejected() {

        String isbn = generateNumericString(14);

        bookService.createBook(createBookRequest(isbn, 2));

        assertThrows(DuplicateResourceException.class, () -> bookService.createBook(createBookRequest(isbn, 3)));

        long count = bookRepository.findAll().stream().filter(book -> isbn.equals(book.getIsbn())).count();

        assertEquals(1, count);
    }

    @Test
    void invalidFuturePublishYearShouldBeRejected() {

        CreateBookRequest request = new CreateBookRequest(generateNumericString(14), "Future Book", "Author", "Test", "Publisher", LocalDateTime.now().getYear() + 1, 1, BigDecimal.valueOf(100000));

        BusinessException exception = assertThrows(BusinessException.class, () -> bookService.createBook(request));

        assertEquals("Invalid publish year", exception.getMessage());
    }

    @Test
    void increasingTotalCopiesShouldIncreaseAvailableCopies() {

        BookResponse created = bookService.createBook(createBookRequest(generateNumericString(14), 2));

        UpdateBookRequest update = new UpdateBookRequest(null, null, null, null, null, 5, null, null);

        BookResponse updated = bookService.updateBook(created.id(), update);

        assertEquals(5, updated.totalCopies());

        assertEquals(5, updated.availableCopies());

        assertEquals(0, updated.borrowedCopies());
    }

    @Test
    void reducingTotalCopiesShouldPreserveBorrowedCopies() {

        BookResponse created = bookService.createBook(createBookRequest(generateNumericString(14), 5));

        Member member = createMember();

        loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), created.bookCode()));

        Book beforeUpdate = bookRepository.findById(created.id()).orElseThrow();

        assertEquals(4, beforeUpdate.getAvailableCopies());

        UpdateBookRequest update = new UpdateBookRequest(null, null, null, null, null, 3, null, null);

        BookResponse updated = bookService.updateBook(created.id(), update);

        assertEquals(3, updated.totalCopies());

        assertEquals(2, updated.availableCopies());

        assertEquals(1, updated.borrowedCopies());
    }

    @Test
    void reducingTotalCopiesBelowBorrowedCopiesShouldBeRejected() {

        BookResponse created = bookService.createBook(createBookRequest(generateNumericString(14), 2));

        Member member1 = createMember();

        Member member2 = createMember();

        loanService.borrowBook(new BorrowRequest(member1.getMembershipNumber(), created.bookCode()));

        loanService.borrowBook(new BorrowRequest(member2.getMembershipNumber(), created.bookCode()));

        UpdateBookRequest update = new UpdateBookRequest(null, null, null, null, null, 1, null, null);

        BusinessException exception = assertThrows(BusinessException.class, () -> bookService.updateBook(created.id(), update));

        assertEquals("Cannot reduce total copies below borrowed copies", exception.getMessage());

        Book unchanged = bookRepository.findById(created.id()).orElseThrow();

        assertEquals(2, unchanged.getTotalCopies());

        assertEquals(0, unchanged.getAvailableCopies());
    }

    @Test
    void deleteBookShouldMarkBookAsDeleted() {

        BookResponse created = bookService.createBook(createBookRequest(generateNumericString(14), 1));

        bookService.deleteBook(created.id());

        Book deleted = bookRepository.findById(created.id()).orElseThrow();

        assertEquals(Enums.BookStatus.DELETED, deleted.getStatus());

        assertNotNull(deleted.getUpdatedAt());
    }

    @Test
    void bookShouldBeRetrievableByIsbnAndBookCode() {

        String isbn = generateNumericString(14);

        BookResponse created = bookService.createBook(createBookRequest(isbn, 2));

        BookResponse byIsbn = bookService.getByIsbn(isbn);

        BookResponse byCode = bookService.getByBookCode(created.bookCode());

        assertEquals(created.id(), byIsbn.id());

        assertEquals(created.id(), byCode.id());
    }

    private CreateBookRequest createBookRequest(String isbn, int totalCopies) {

        return new CreateBookRequest(isbn, "Test Book", "Test Author", "Test Category", "Test Publisher", 2026, totalCopies, BigDecimal.valueOf(100000));
    }

    private Member createMember() {

        Member member = new Member();

        member.setMembershipNumber(generateNumericString(10));

        member.setFullName("Book Test Member");

        member.setNationalCode(generateNumericString(10));

        member.setBirthDate(LocalDate.of(2000, 1, 1));

        member.setMembershipType(Enums.MembershipType.INDIVIDUAL);

        member.setPhone("09120000000");

        member.setAddress("Test Address");

        member.setPostalCode("1234567890");

        member.setStatus(Enums.MemberStatus.ACTIVE);

        member.setCreatedBy(testUser);

        member.setCreatedAt(LocalDateTime.now());

        return memberRepository.save(member);
    }

    private String generateNumericString(int length) {

        String digits = UUID.randomUUID().toString().replaceAll("\\D", "");

        while (digits.length() < length) {

            digits += UUID.randomUUID().toString().replaceAll("\\D", "");
        }

        return digits.substring(0, length);
    }
}