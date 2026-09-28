package com.library.library_management.service.impl;

import com.library.library_management.dto.fine.FineResponse;
import com.library.library_management.dto.fine.PayFineRequest;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Fine;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
                        () -> new ResourceNotFoundException(
                                "Fine not found"
                        )
                );


        return mapToResponse(fine);
    }


    @Override
    @Transactional
    public FineResponse payFine(
            Long fineId,
            PayFineRequest request
    ) {


        Fine fine = fineRepository
                .findById(fineId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Fine not found"
                        )
                );


        BigDecimal currentPaidAmount =
                fine.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : fine.getPaidAmount();


        BigDecimal newPaidAmount =
                currentPaidAmount.add(
                        request.amount()
                );


        if (newPaidAmount.compareTo(
                fine.getAmount()
        ) >= 0) {


            fine.setPaidAmount(
                    fine.getAmount()
            );


            fine.setStatus(
                    Enums.FineStatus.PAID
            );


            fine.setPaidAt(
                    LocalDateTime.now()
            );


        } else {


            fine.setPaidAmount(
                    newPaidAmount
            );


            fine.setStatus(
                    Enums.FineStatus.PARTIALLY_PAID
            );

        }


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