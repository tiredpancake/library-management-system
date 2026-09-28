package com.library.library_management.service;

import com.library.library_management.dto.fine.FineResponse;
import com.library.library_management.dto.fine.PayFineRequest;

public interface FineService {

    FineResponse getFineByLoan(Long loanId);

    FineResponse payFine(PayFineRequest request);


}