package com.library.library_management.service.impl;

import com.library.library_management.dto.fine.FineResponse;
import com.library.library_management.dto.fine.PayFineRequest;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Fine;
import com.library.library_management.entity.Member;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.repository.MemberRepository;
import com.library.library_management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {


    private final FineRepository fineRepository;
    private final MemberRepository memberRepository;

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
    public FineResponse payFine(PayFineRequest request) {


        Member member = memberRepository
                .findByMembershipNumber(request.membershipNumber())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Member not found"
                        )
                );


        BigDecimal unpaidAmount =
                fineRepository.sumUnpaidFineByMemberId(
                        member.getId()
                );


        if(request.amount().compareTo(unpaidAmount) > 0){

            throw new BusinessException(
                    "Payment amount exceeds unpaid fine amount"
            );

        }


        List<Fine> fines =
                fineRepository
                        .findByLoanTransactionMemberIdAndStatusNot(
                                member.getId(),
                                Enums.FineStatus.PAID
                        );


        BigDecimal remainingPayment = request.amount();


        Fine lastUpdatedFine = null;


        for(Fine fine : fines){


            if(remainingPayment.compareTo(BigDecimal.ZERO) <= 0){
                break;
            }


            BigDecimal remainingFine =
                    fine.getAmount()
                            .subtract(
                                    fine.getPaidAmount()
                            );


            BigDecimal payment =
                    remainingPayment.min(
                            remainingFine
                    );


            fine.setPaidAmount(
                    fine.getPaidAmount()
                            .add(payment)
            );


            remainingPayment =
                    remainingPayment.subtract(payment);



            if(fine.getPaidAmount()
                    .compareTo(fine.getAmount()) >= 0){

                fine.setStatus(
                        Enums.FineStatus.PAID
                );

                fine.setPaidAt(
                        LocalDateTime.now()
                );

            } else {

                fine.setStatus(
                        Enums.FineStatus.PARTIALLY_PAID
                );

            }


            lastUpdatedFine =
                    fineRepository.save(fine);

        }

        if(lastUpdatedFine == null){

            throw new BusinessException(
                    "No unpaid fine found for this member"
            );
        }
        return mapToResponse(lastUpdatedFine);


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
    @Override
    public List<FineResponse> getAllFines(){

        return fineRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

}