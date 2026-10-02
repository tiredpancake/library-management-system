package com.library.library_management.service;

import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.RenewRequest;
import com.library.library_management.dto.loan.ReturnRequest;
import com.library.library_management.entity.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.LoanTransactionRepository;
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
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanOperationIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LoanTransactionRepository loanRepository;
    private AppUser testUser;

    @Autowired
    private LoanTransactionStatusService loanTransactionStatusService;

    @BeforeEach
    void setUp() {

        String username = "test-" + UUID.randomUUID();

        testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("test-password");

        testUser = appUserRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                )
        );
    }

    @Test
    void borrowShouldFailWithoutChangingInventoryWhenNoCopiesAreAvailable() {

        Member member = createMember();

        Book book = createBook(1, 0);

        LoanResponse response = loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), book.getBookCode()));

        assertNotNull(response);

        assertEquals(Enums.LoanStatus.FAILED, response.status());

        assertNotNull(response.trackingCode());

        assertEquals("Book is not available", response.errorMessage());

        Book reloadedBook = bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(0, reloadedBook.getAvailableCopies());
    }

    @Test
    void businessFailureShouldKeepUserFacingMessage() {

        LoanTransaction pending =
                loanTransactionStatusService.createPendingTransaction(
                        Enums.LoanType.BORROW
                );

        BusinessException businessException =
                new BusinessException(
                        "No available copies"
                );

        LoanResponse failed =
                loanTransactionStatusService.markFailed(
                        pending.getId(),
                        businessException
                );

        assertEquals(
                Enums.LoanStatus.FAILED,
                failed.status()
        );

        assertEquals(
                "No available copies",
                failed.errorMessage()
        );
    }

    @Test
    void successfulBorrowShouldDecreaseAvailableCopiesByOne() {

        Member member = createMember();

        Book book = createBook(1, 1);

        LoanResponse response = loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), book.getBookCode()));

        assertEquals(Enums.LoanStatus.SUCCESS, response.status());

        assertNotNull(response.trackingCode());

        assertNotNull(response.dueDate());

        Book reloadedBook = bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(0, reloadedBook.getAvailableCopies());
    }
    @Test
    void concurrentBorrowForLastAvailableCopyShouldAllowOnlyOneSuccess() throws Exception {

        Member member1 = createMember();
        Member member2 = createMember();

        Book book = createBook(1, 1);

        String username = testUser.getUsername();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        try {

            Future<LoanResponse> future1 = executor.submit(() -> {

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        )
                );

                readyLatch.countDown();
                startLatch.await();

                try {
                    return loanService.borrowBook(
                            new BorrowRequest(
                                    member1.getMembershipNumber(),
                                    book.getBookCode()
                            )
                    );
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });

            Future<LoanResponse> future2 = executor.submit(() -> {

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        )
                );

                readyLatch.countDown();
                startLatch.await();

                try {
                    return loanService.borrowBook(
                            new BorrowRequest(
                                    member2.getMembershipNumber(),
                                    book.getBookCode()
                            )
                    );
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });

            assertEquals(true, readyLatch.await(5, TimeUnit.SECONDS));

            startLatch.countDown();

            LoanResponse response1 = future1.get(10, TimeUnit.SECONDS);
            LoanResponse response2 = future2.get(10, TimeUnit.SECONDS);

            long successCount =
                    java.util.stream.Stream.of(response1, response2)
                            .filter(response ->
                                    response.status() == Enums.LoanStatus.SUCCESS)
                            .count();

            long failedCount =
                    java.util.stream.Stream.of(response1, response2)
                            .filter(response ->
                                    response.status() == Enums.LoanStatus.FAILED)
                            .count();

            assertEquals(1, successCount);
            assertEquals(1, failedCount);

            Book reloadedBook =
                    bookRepository.findById(book.getId()).orElseThrow();

            assertEquals(0, reloadedBook.getAvailableCopies());

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void unexpectedFailureShouldStoreGenericErrorAndPreserveFailedTracking() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanTransaction pending =
                loanTransactionStatusService.createPendingTransaction(
                        Enums.LoanType.BORROW
                );

        RuntimeException internalFailure =
                new RuntimeException(
                        "SQLSTATE 23505 duplicate key violates constraint uk_secret_internal"
                );

        LoanResponse failed =
                loanTransactionStatusService.markFailed(
                        pending.getId(),
                        internalFailure
                );

        assertEquals(
                Enums.LoanStatus.FAILED,
                failed.status()
        );

        assertNotNull(
                failed.trackingCode()
        );

        assertEquals(
                "Transaction failed due to an internal error",
                failed.errorMessage()
        );

        String error =
                failed.errorMessage()
                        .toLowerCase();

        assertFalse(
                error.contains("sql")
        );

        assertFalse(
                error.contains("constraint")
        );

        assertFalse(
                error.contains("duplicate key")
        );

        LoanTransaction persisted =
                loanRepository
                        .findById(
                                pending.getId()
                        )
                        .orElseThrow();

        assertEquals(
                Enums.LoanStatus.FAILED,
                persisted.getStatus()
        );

        assertEquals(
                failed.trackingCode(),
                persisted.getTrackingCode()
        );

        assertEquals(
                "Transaction failed due to an internal error",
                persisted.getErrorMessage()
        );
    }


    @Test
    void renewShouldSucceedEvenWhenNoAvailableCopiesExist() {

        Member member = createMember();

        Book book = createBook(1, 1);

        LoanResponse borrowResponse = loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), book.getBookCode()));

        assertEquals(Enums.LoanStatus.SUCCESS, borrowResponse.status());

        Book afterBorrow = bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(0, afterBorrow.getAvailableCopies());

        LoanResponse renewResponse = loanService.renewLoan(new RenewRequest(borrowResponse.trackingCode()));

        assertEquals(Enums.LoanStatus.SUCCESS, renewResponse.status());

        assertEquals(Enums.LoanType.RENEW, renewResponse.type());

        assertEquals(1, renewResponse.renewCount());

        Book afterRenew = bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(0, afterRenew.getAvailableCopies());
    }

    @Test
    void concurrentReturnAndRenewShouldAllowOnlyOneSuccess() throws Exception {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrowResponse = loanService.borrowBook(
                new BorrowRequest(
                        member.getMembershipNumber(),
                        book.getBookCode()
                )
        );

        assertEquals(Enums.LoanStatus.SUCCESS, borrowResponse.status());

        String trackingCode = borrowResponse.trackingCode();
        String username = testUser.getUsername();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        try {

            Future<LoanResponse> returnFuture = executor.submit(() -> {

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        )
                );

                readyLatch.countDown();
                startLatch.await();

                try {
                    return loanService.returnBook(
                            new ReturnRequest(
                                    null,
                                    null,
                                    trackingCode
                            )
                    );
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });

            Future<LoanResponse> renewFuture = executor.submit(() -> {

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        )
                );

                readyLatch.countDown();
                startLatch.await();

                try {
                    return loanService.renewLoan(
                            new RenewRequest(trackingCode)
                    );
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });

            assertEquals(true, readyLatch.await(5, TimeUnit.SECONDS));

            startLatch.countDown();

            LoanResponse returnResponse =
                    returnFuture.get(10, TimeUnit.SECONDS);

            LoanResponse renewResponse =
                    renewFuture.get(10, TimeUnit.SECONDS);

            long successCount =
                    java.util.stream.Stream.of(
                                    returnResponse,
                                    renewResponse
                            )
                            .filter(response ->
                                    response.status() == Enums.LoanStatus.SUCCESS)
                            .count();

            long failedCount =
                    java.util.stream.Stream.of(
                                    returnResponse,
                                    renewResponse
                            )
                            .filter(response ->
                                    response.status() == Enums.LoanStatus.FAILED)
                            .count();

            assertEquals(1, successCount);
            assertEquals(1, failedCount);

            Book reloadedBook =
                    bookRepository.findById(book.getId()).orElseThrow();

            if (returnResponse.status() == Enums.LoanStatus.SUCCESS) {

                assertEquals(
                        Enums.LoanType.RETURN,
                        returnResponse.type()
                );

                assertEquals(
                        Enums.LoanStatus.FAILED,
                        renewResponse.status()
                );

                assertEquals(
                        1,
                        reloadedBook.getAvailableCopies()
                );

            } else {

                assertEquals(
                        Enums.LoanType.RENEW,
                        renewResponse.type()
                );

                assertEquals(
                        Enums.LoanStatus.FAILED,
                        returnResponse.status()
                );

                assertEquals(
                        0,
                        reloadedBook.getAvailableCopies()
                );
            }

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void fullLoanLifecycleShouldWorkCorrectly() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow = loanService.borrowBook(
                new BorrowRequest(
                        member.getMembershipNumber(),
                        book.getBookCode()
                )
        );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                borrow.status()
        );

        assertEquals(
                Enums.LoanType.BORROW,
                borrow.type()
        );

        assertEquals(0, borrow.renewCount());

        Book afterBorrow =
                bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(
                0,
                afterBorrow.getAvailableCopies()
        );


        LoanResponse renew1 = loanService.renewLoan(
                new RenewRequest(
                        borrow.trackingCode()
                )
        );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                renew1.status()
        );

        assertEquals(
                Enums.LoanType.RENEW,
                renew1.type()
        );

        assertEquals(
                1,
                renew1.renewCount()
        );


        LoanResponse oldBorrowRenewAttempt =
                loanService.renewLoan(
                        new RenewRequest(
                                borrow.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.FAILED,
                oldBorrowRenewAttempt.status()
        );



        LoanResponse renew2 = loanService.renewLoan(
                new RenewRequest(
                        renew1.trackingCode()
                )
        );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                renew2.status()
        );

        assertEquals(
                Enums.LoanType.RENEW,
                renew2.type()
        );

        assertEquals(
                2,
                renew2.renewCount()
        );


        LoanResponse thirdRenewAttempt =
                loanService.renewLoan(
                        new RenewRequest(
                                renew2.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.FAILED,
                thirdRenewAttempt.status()
        );

        assertEquals(
                "Maximum renew limit reached",
                thirdRenewAttempt.errorMessage()
        );


        LoanResponse returned =
                loanService.returnBook(
                        new ReturnRequest(
                                null,
                                null,
                                renew2.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.SUCCESS,
                returned.status()
        );

        assertEquals(
                Enums.LoanType.RETURN,
                returned.type()
        );

        assertNotNull(
                returned.returnDate()
        );



        Book afterReturn =
                bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(
                1,
                afterReturn.getAvailableCopies()
        );


        LoanResponse secondReturnAttempt =
                loanService.returnBook(
                        new ReturnRequest(
                                null,
                                null,
                                returned.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.FAILED,
                secondReturnAttempt.status()
        );



        LoanResponse renewAfterReturn =
                loanService.renewLoan(
                        new RenewRequest(
                                returned.trackingCode()
                        )
                );

        assertEquals(
                Enums.LoanStatus.FAILED,
                renewAfterReturn.status()
        );
    }

    @Test
    void loanParentChainShouldBeStoredCorrectly() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow = loanService.borrowBook(
                new BorrowRequest(
                        member.getMembershipNumber(),
                        book.getBookCode()
                )
        );

        LoanResponse renew1 = loanService.renewLoan(
                new RenewRequest(
                        borrow.trackingCode()
                )
        );

        LoanResponse renew2 = loanService.renewLoan(
                new RenewRequest(
                        renew1.trackingCode()
                )
        );

        LoanResponse returned = loanService.returnBook(
                new ReturnRequest(
                        null,
                        null,
                        renew2.trackingCode()
                )
        );

        LoanTransaction borrowTx =
                loanRepository.findById(borrow.id()).orElseThrow();

        LoanTransaction renew1Tx =
                loanRepository.findById(renew1.id()).orElseThrow();

        LoanTransaction renew2Tx =
                loanRepository.findById(renew2.id()).orElseThrow();

        LoanTransaction returnTx =
                loanRepository.findById(returned.id()).orElseThrow();

        assertNull(
                borrowTx.getParentTransaction()
        );

        assertNotNull(
                renew1Tx.getParentTransaction()
        );

        assertEquals(
                borrowTx.getId(),
                renew1Tx.getParentTransaction().getId()
        );

        assertNotNull(
                renew2Tx.getParentTransaction()
        );

        assertEquals(
                renew1Tx.getId(),
                renew2Tx.getParentTransaction().getId()
        );

        assertNotNull(
                returnTx.getParentTransaction()
        );

        assertEquals(
                renew2Tx.getId(),
                returnTx.getParentTransaction().getId()
        );
    }
    private Member createMember() {

        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Member member = new Member();

        member.setMembershipNumber("1" + suffix.substring(0, 9));

        member.setFullName("Integration Test Member");

        member.setNationalCode(suffix);

        member.setBirthDate(LocalDate.of(2000, 1, 1));

        member.setMembershipType(Enums.MembershipType.INDIVIDUAL);

        member.setPhone("0912" + suffix.substring(0, 7));

        member.setAddress("Integration test address");

        member.setPostalCode(suffix);

        member.setStatus(Enums.MemberStatus.ACTIVE);

        member.setCreatedBy(testUser);

        member.setCreatedAt(LocalDateTime.now());

        return memberRepository.save(member);
    }

    private Book createBook(int totalCopies, int availableCopies) {

        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Book book = new Book();

        book.setBookCode("10" + suffix);

        book.setIsbn("978" + suffix.substring(0, 10));

        book.setTitle("Integration Test Book");

        book.setAuthor("Test Author");

        book.setCategory("Test");

        book.setPublisher("Test Publisher");

        book.setPublishYear(2026);

        book.setTotalCopies(totalCopies);

        book.setAvailableCopies(availableCopies);

        book.setPrice(BigDecimal.valueOf(100000));

        book.setCreatedBy(testUser);

        book.setCreatedAt(LocalDateTime.now());

        book.setStatus(Enums.BookStatus.ACTIVE);

        return bookRepository.save(book);
    }
}