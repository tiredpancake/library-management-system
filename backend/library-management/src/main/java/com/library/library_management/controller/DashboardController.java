package com.library.library_management.controller;


import com.library.library_management.entity.Enums;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.repository.LoanTransactionRepository;
import com.library.library_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {


    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanTransactionRepository loanRepository;
    private final FineRepository fineRepository;



    @GetMapping("/summary")
    public Map<String,Object> summary(){


        return Map.of(

                "members",
                memberRepository.count(),


                "books",
                bookRepository.count(),


                "activeLoans",
                loanRepository.countByReturnDateIsNull(),


                "unpaidFines",
                fineRepository.countByStatusNot(
                        Enums.FineStatus.PAID
                )

        );


    }

}