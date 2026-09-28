package com.library.library_management.controller;

import com.library.library_management.dto.fine.FineResponse;
import com.library.library_management.dto.fine.PayFineRequest;
import com.library.library_management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {


    private final FineService fineService;


    @GetMapping("/loan/{loanId}")
    public FineResponse getFineByLoan(
            @PathVariable Long loanId
    ) {

        return fineService.getFineByLoan(loanId);
    }



    @PutMapping("/{fineId}/pay")
    public FineResponse payFine(

            @PathVariable Long fineId,

            @RequestBody PayFineRequest request

    ) {

        return fineService.payFine(
                fineId,
                request
        );
    }

}