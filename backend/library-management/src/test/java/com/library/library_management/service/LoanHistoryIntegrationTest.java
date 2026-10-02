package com.library.library_management.service;

import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanHistoryResponse;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.ReturnRequest;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Book;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import com.library.library_management.entity.Member;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.LoanTransactionRepository;
import com.library.library_management.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanHistoryIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanHistoryService loanHistoryService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanTransactionRepository loanRepository;

    private AppUser testUser;

    @BeforeEach
    void setUp() {

        String username =
                "history-test-" + UUID.randomUUID();

        testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("test-password");

        testUser = appUserRepository.save(testUser);

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        )
                );
    }

    @Test
    void historyShouldFilterByMembershipNumberAndPaginate() {

        Member member = createMember();

        Book book1 = createBook(1, 1);
        Book book2 = createBook(1, 1);
        Book book3 = createBook(1, 1);

        LoanResponse loan1 =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book1.getBookCode()
                        )
                );

        LoanResponse loan2 =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book2.getBookCode()
                        )
                );

        LoanResponse loan3 =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book3.getBookCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                loan1.status()
        );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                loan2.status()
        );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                loan3.status()
        );

        Pageable firstPageRequest =
                PageRequest.of(
                        0,
                        2,
                        Sort.by("id").ascending()
                );

        Page<LoanHistoryResponse> firstPage =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        firstPageRequest
                );

        assertEquals(
                3,
                firstPage.getTotalElements()
        );

        assertEquals(
                2,
                firstPage.getContent().size()
        );

        assertEquals(
                2,
                firstPage.getTotalPages()
        );

        assertEquals(
                0,
                firstPage.getNumber()
        );

        assertTrue(
                firstPage.getContent()
                        .stream()
                        .allMatch(
                                loan ->
                                        member.getMembershipNumber()
                                                .equals(
                                                        loan.membershipNumber()
                                                )
                        )
        );

        Pageable secondPageRequest =
                PageRequest.of(
                        1,
                        2,
                        Sort.by("id").ascending()
                );

        Page<LoanHistoryResponse> secondPage =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        secondPageRequest
                );

        assertEquals(
                3,
                secondPage.getTotalElements()
        );

        assertEquals(
                1,
                secondPage.getContent().size()
        );

        assertEquals(
                1,
                secondPage.getNumber()
        );
    }

    @Test
    void historyShouldFilterByBookCode() {

        Member member1 = createMember();
        Member member2 = createMember();

        Book targetBook = createBook(2, 2);
        Book otherBook = createBook(1, 1);

        LoanResponse loan1 =
                loanService.borrowBook(
                        new BorrowRequest(
                                member1.getMembershipNumber(),
                                targetBook.getBookCode()
                        )
                );

        LoanResponse loan2 =
                loanService.borrowBook(
                        new BorrowRequest(
                                member2.getMembershipNumber(),
                                targetBook.getBookCode()
                        )
                );

        loanService.borrowBook(
                new BorrowRequest(
                        createMember().getMembershipNumber(),
                        otherBook.getBookCode()
                )
        );

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        null,
                        targetBook.getBookCode(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                20,
                                Sort.by("id").ascending()
                        )
                );

        assertEquals(
                2,
                page.getTotalElements()
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .allMatch(
                                loan ->
                                        targetBook.getBookCode()
                                                .equals(
                                                        loan.bookCode()
                                                )
                        )
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .anyMatch(
                                loan ->
                                        loan.id().equals(
                                                loan1.id()
                                        )
                        )
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .anyMatch(
                                loan ->
                                        loan.id().equals(
                                                loan2.id()
                                        )
                        )
        );
    }

    @Test
    void historyShouldFilterByTypeAndStatus() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book.getBookCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                borrow.status()
        );

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        Enums.LoanType.BORROW,
                        Enums.LoanStatus.SUCCESS,
                        null,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                20
                        )
                );

        assertFalse(
                page.isEmpty()
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .allMatch(
                                loan ->
                                        loan.type()
                                                == Enums.LoanType.BORROW
                                                &&
                                                loan.status()
                                                        == Enums.LoanStatus.SUCCESS
                        )
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .anyMatch(
                                loan ->
                                        loan.id().equals(
                                                borrow.id()
                                        )
                        )
        );
    }

    @Test
    void returnedStateShouldReturnReturnedTransactions() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book.getBookCode()
                        )
                );

        LoanResponse returned =
                loanService.returnBook(
                        new ReturnRequest(
                                null,
                                null,
                                borrow.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                returned.status()
        );

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        Enums.LoanState.RETURNED,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                20
                        )
                );

        assertFalse(
                page.isEmpty()
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .allMatch(
                                loan ->
                                        loan.returnDate() != null
                        )
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .anyMatch(
                                loan ->
                                        loan.id().equals(
                                                returned.id()
                                        )
                        )
        );
    }

    @Test
    void notReturnedStateShouldReturnOnlyCurrentActiveTransactions() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book.getBookCode()
                        )
                );

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        Enums.LoanState.NOT_RETURNED,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                20
                        )
                );

        assertEquals(
                1,
                page.getTotalElements()
        );

        LoanHistoryResponse result =
                page.getContent().get(0);

        assertEquals(
                borrow.id(),
                result.id()
        );

        assertTrue(
                result.current()
        );

        assertTrue(
                result.canReturn()
        );

        assertTrue(
                result.canRenew()
        );

        assertNull(
                result.returnDate()
        );
    }

    @Test
    void overdueStateShouldReturnOnlyOverdueCurrentTransactions() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book.getBookCode()
                        )
                );

        LoanTransaction transaction =
                loanRepository
                        .findById(
                                borrow.id()
                        )
                        .orElseThrow();

        transaction.setDueDate(
                LocalDateTime.now()
                        .minusDays(1)
        );

        loanRepository.save(
                transaction
        );

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        Enums.LoanState.OVERDUE,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                20
                        )
                );

        assertEquals(
                1,
                page.getTotalElements()
        );

        LoanHistoryResponse result =
                page.getContent().get(0);

        assertEquals(
                borrow.id(),
                result.id()
        );

        assertTrue(
                result.current()
        );

        assertTrue(
                result.canReturn()
        );

        assertTrue(
                result.dueDate()
                        .isBefore(
                                LocalDateTime.now()
                        )
        );
    }

    @Test
    void oldBorrowShouldNotAppearAsNotReturnedAfterRenewal() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                book.getBookCode()
                        )
                );

        LoanResponse renew =
                loanService.renewLoan(
                        new com.library.library_management.dto.loan.RenewRequest(
                                borrow.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                renew.status()
        );

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        Enums.LoanState.NOT_RETURNED,
                        null,
                        null,
                        PageRequest.of(
                                0,
                                20
                        )
                );

        assertEquals(
                1,
                page.getTotalElements()
        );

        LoanHistoryResponse current =
                page.getContent().get(0);

        assertEquals(
                renew.id(),
                current.id()
        );

        assertNotEquals(
                borrow.id(),
                current.id()
        );

        assertEquals(
                Enums.LoanType.RENEW,
                current.type()
        );

        assertTrue(
                current.current()
        );
    }

    @Test
    void historyShouldFilterByDateRange() {

        Member member = createMember();

        Book oldBook = createBook(1, 1);
        Book recentBook = createBook(1, 1);

        LoanResponse oldLoan =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                oldBook.getBookCode()
                        )
                );

        LoanTransaction oldTransaction =
                loanRepository
                        .findById(
                                oldLoan.id()
                        )
                        .orElseThrow();

        oldTransaction.setRequestDate(
                LocalDateTime.now()
                        .minusDays(10)
        );

        loanRepository.save(
                oldTransaction
        );

        LoanResponse recentLoan =
                loanService.borrowBook(
                        new BorrowRequest(
                                member.getMembershipNumber(),
                                recentBook.getBookCode()
                        )
                );

        LocalDateTime from =
                LocalDateTime.now()
                        .minusDays(1);

        LocalDateTime to =
                LocalDateTime.now()
                        .plusDays(1);

        Page<LoanHistoryResponse> page =
                loanHistoryService.searchHistory(
                        member.getMembershipNumber(),
                        null,
                        null,
                        null,
                        null,
                        from,
                        to,
                        PageRequest.of(
                                0,
                                20
                        )
                );

        assertTrue(
                page.getContent()
                        .stream()
                        .anyMatch(
                                loan ->
                                        loan.id().equals(
                                                recentLoan.id()
                                        )
                        )
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .noneMatch(
                                loan ->
                                        loan.id().equals(
                                                oldLoan.id()
                                        )
                        )
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .allMatch(
                                loan ->
                                        !loan.requestDate()
                                                .isBefore(from)
                                                &&
                                                !loan.requestDate()
                                                        .isAfter(to)
                        )
        );
    }

    private Member createMember() {

        String membershipNumber =
                generateNumericString(10);

        String nationalCode =
                generateNumericString(10);

        Member member = new Member();

        member.setMembershipNumber(
                membershipNumber
        );

        member.setFullName(
                "History Test Member"
        );

        member.setNationalCode(
                nationalCode
        );

        member.setBirthDate(
                LocalDate.of(
                        2000,
                        1,
                        1
                )
        );

        member.setMembershipType(
                Enums.MembershipType.INDIVIDUAL
        );

        member.setPhone(
                "09120000000"
        );

        member.setAddress(
                "Test Address"
        );

        member.setPostalCode(
                "1234567890"
        );

        member.setStatus(
                Enums.MemberStatus.ACTIVE
        );

        member.setCreatedBy(
                testUser
        );

        member.setCreatedAt(
                LocalDateTime.now()
        );

        return memberRepository.save(
                member
        );
    }

    private Book createBook(
            int totalCopies,
            int availableCopies
    ) {

        String bookCode =
                generateNumericString(14);

        String isbn =
                generateNumericString(13);

        Book book = new Book();

        book.setBookCode(
                bookCode
        );

        book.setIsbn(
                isbn
        );

        book.setTitle(
                "History Test Book"
        );

        book.setAuthor(
                "Test Author"
        );

        book.setCategory(
                "Test"
        );

        book.setPublisher(
                "Test Publisher"
        );

        book.setPublishYear(
                2026
        );

        book.setTotalCopies(
                totalCopies
        );

        book.setAvailableCopies(
                availableCopies
        );

        book.setPrice(
                BigDecimal.valueOf(100000)
        );

        book.setStatus(
                Enums.BookStatus.ACTIVE
        );

        book.setCreatedBy(
                testUser
        );

        book.setCreatedAt(
                LocalDateTime.now()
        );

        return bookRepository.save(
                book
        );
    }

    private String generateNumericString(
            int length
    ) {

        String digits =
                UUID.randomUUID()
                        .toString()
                        .replaceAll("\\D", "");

        while (digits.length() < length) {

            digits +=
                    UUID.randomUUID()
                            .toString()
                            .replaceAll("\\D", "");
        }

        return digits.substring(
                0,
                length
        );
    }
}