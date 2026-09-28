package com.library.library_management.service;

import com.library.library_management.dto.fine.FineResponse;

public interface FineService {

    FineResponse getFineByLoan(Long loanId);

    FineResponse payFine(Long fineId);

}