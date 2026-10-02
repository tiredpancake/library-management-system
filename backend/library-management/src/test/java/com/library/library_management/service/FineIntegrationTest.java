package com.library.library_management.service;

import com.library.library_management.config.LibraryProperties;
import com.library.library_management.dto.fine.PayFineRequest;
import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.ReturnRequest;
import com.library.library_management.entity.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.repository.*;
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
class FineIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private FineService fineService;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LoanTransactionRepository loanRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryProperties properties;

    private AppUser testUser;

    @BeforeEach
    void setUp() {

        String username = "fine-test-" + UUID.randomUUID();

        testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("test-password");

        testUser = appUserRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
    }

    @Test
    void overdueReturnShouldCreateFine() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow = loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), book.getBookCode()));

        assertEquals(Enums.LoanStatus.SUCCESS, borrow.status());

        LoanTransaction borrowTransaction = loanRepository.findById(borrow.id()).orElseThrow();

        borrowTransaction.setDueDate(LocalDateTime.now().minusMinutes(1));

        loanRepository.save(borrowTransaction);

        LoanResponse returned = loanService.returnBook(new ReturnRequest(null, null, borrow.trackingCode()));

        assertEquals(Enums.LoanStatus.SUCCESS, returned.status());

        Fine fine = fineRepository.findByLoanTransactionId(returned.id()).orElseThrow();

        assertEquals(0, BigDecimal.valueOf(properties.getFinePerDay()).compareTo(fine.getAmount()));
        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(fine.getPaidAmount())
        );
        assertEquals(Enums.FineStatus.UNPAID, fine.getStatus());

        assertNull(fine.getPaidAt());
    }

    @Test
    void fineShouldRespectMaximumCap() {

        Member member = createMember();
        Book book = createBook(1, 1);

        LoanResponse borrow = loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), book.getBookCode()));

        LoanTransaction borrowTransaction = loanRepository.findById(borrow.id()).orElseThrow();

        borrowTransaction.setDueDate(LocalDateTime.now().minusDays(100));

        loanRepository.save(borrowTransaction);

        LoanResponse returned = loanService.returnBook(new ReturnRequest(null, null, borrow.trackingCode()));

        Fine fine = fineRepository.findByLoanTransactionId(returned.id()).orElseThrow();

        assertEquals(
                0,
                BigDecimal.valueOf(properties.getMaxFine())
                        .compareTo(fine.getAmount())
        );
        assertEquals(Enums.FineStatus.UNPAID, fine.getStatus());
    }

    @Test
    void partialPaymentShouldSetFineToPartiallyPaid() {

        Member member = createMember();
        Fine fine = createOverdueFine(member);

        BigDecimal payment = fine.getAmount().divide(BigDecimal.valueOf(2));

        fineService.payFine(new PayFineRequest(member.getMembershipNumber(), payment));

        Fine updated = fineRepository.findById(fine.getId()).orElseThrow();

        assertEquals(Enums.FineStatus.PARTIALLY_PAID, updated.getStatus());

        assertEquals(0, payment.compareTo(updated.getPaidAmount()));

        assertNull(updated.getPaidAt());
    }

    @Test
    void fullPaymentShouldSetPaid() {

        Member member = createMember();
        Fine fine = createOverdueFine(member);

        fineService.payFine(
                new PayFineRequest(
                        member.getMembershipNumber(),
                        fine.getAmount()
                )
        );

        Fine updated =
                fineRepository.findById(fine.getId()).orElseThrow();

        assertEquals(
                Enums.FineStatus.PAID,
                updated.getStatus()
        );

        assertEquals(
                0,
                fine.getAmount().compareTo(updated.getPaidAmount())
        );

        assertNotNull(updated.getPaidAt());
    }

    @Test
    void overpaymentShouldBeRejected() {

        Member member = createMember();
        Fine fine = createOverdueFine(member);

        BigDecimal excessivePayment =
                fine.getAmount().add(BigDecimal.ONE);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> fineService.payFine(
                        new PayFineRequest(
                                member.getMembershipNumber(),
                                excessivePayment
                        )
                )
        );

        assertEquals(
                "Payment amount exceeds unpaid fine amount",
                exception.getMessage()
        );

        Fine unchanged =
                fineRepository.findById(fine.getId()).orElseThrow();

        assertEquals(
                Enums.FineStatus.UNPAID,
                unchanged.getStatus()
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(unchanged.getPaidAmount())
        );

        assertNull(unchanged.getPaidAt());
    }

    private Fine createOverdueFine(Member member) {

        Book book = createBook(1, 1);

        LoanResponse borrow = loanService.borrowBook(new BorrowRequest(member.getMembershipNumber(), book.getBookCode()));

        LoanTransaction transaction = loanRepository.findById(borrow.id()).orElseThrow();

        transaction.setDueDate(LocalDateTime.now().minusDays(2));

        loanRepository.save(transaction);

        LoanResponse returned = loanService.returnBook(new ReturnRequest(null, null, borrow.trackingCode()));

        return fineRepository.findByLoanTransactionId(returned.id()).orElseThrow();
    }

    private Member createMember() {

        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Member member = new Member();

        member.setMembershipNumber(suffix);
        member.setFullName("Fine Test Member");
        member.setNationalCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));

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

    private Book createBook(int totalCopies, int availableCopies) {

        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Book book = new Book();

        book.setBookCode("10" + suffix);

        book.setIsbn("978" + suffix.substring(0, 10));

        book.setTitle("Fine Test Book");
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