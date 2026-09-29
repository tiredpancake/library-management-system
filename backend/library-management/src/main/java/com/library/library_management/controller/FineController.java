package com.library.library_management.controller;

import com.library.library_management.dto.fine.FineResponse;
import com.library.library_management.dto.fine.PayFineRequest;
import com.library.library_management.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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



    @PutMapping("/pay")
    public FineResponse payFine(
            @Valid @RequestBody PayFineRequest request
    ) {

        return fineService.payFine(request);

    }
    @GetMapping
    public List<FineResponse> getAllFines(){

        return fineService.getAllFines();

    }
}