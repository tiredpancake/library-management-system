package com.library.library_management.service.impl;

import com.library.library_management.dto.fine.FineResponse;
import com.library.library_management.entity.Fine;
import com.library.library_management.entity.Enums;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {


    private final FineRepository fineRepository;



    @Override
    public FineResponse getFineByLoan(Long loanId) {

        Fine fine = fineRepository
                .findByLoanTransactionId(loanId)
                .orElseThrow(
                        () -> new RuntimeException("Fine not found")
                );


        return mapToResponse(fine);
    }



    @Override
    public FineResponse payFine(Long fineId) {

        Fine fine = fineRepository
                .findById(fineId)
                .orElseThrow(
                        () -> new RuntimeException("Fine not found")
                );


        fine.setStatus(
                Enums.FineStatus.PAID
        );


        fine.setPaidAt(
                LocalDateTime.now()
        );


        Fine savedFine =
                fineRepository.save(fine);


        return mapToResponse(savedFine);
    }



    private FineResponse mapToResponse(Fine fine) {

        return new FineResponse(

                fine.getId(),

                fine.getLoanTransaction()
                        .getId(),

                fine.getAmount(),

                fine.getStatus(),

                fine.getCreatedAt(),

                fine.getPaidAt()
        );
    }

}