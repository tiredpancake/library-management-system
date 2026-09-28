package com.library.library_management.repository.specification;


import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import org.springframework.data.jpa.domain.Specification;


import java.time.LocalDateTime;


public class LoanSpecification {


    public static Specification<LoanTransaction> hasMember(
            String membershipNumber
    ) {

        return (root, query, cb) ->

                membershipNumber == null
                        ? null
                        :
                        cb.equal(
                                root.get("member")
                                        .get("membershipNumber"),
                                membershipNumber
                        );
    }



    public static Specification<LoanTransaction> hasBook(
            String bookCode
    ) {

        return (root, query, cb) ->

                bookCode == null
                        ? null
                        :
                        cb.equal(
                                root.get("book")
                                        .get("bookCode"),
                                bookCode
                        );
    }



    public static Specification<LoanTransaction> hasType(
            Enums.LoanType type
    ) {

        return (root, query, cb) ->

                type == null
                        ? null
                        :
                        cb.equal(
                                root.get("type"),
                                type
                        );
    }



    public static Specification<LoanTransaction> hasStatus(
            Enums.LoanStatus status
    ) {

        return (root, query, cb) ->

                status == null
                        ? null
                        :
                        cb.equal(
                                root.get("status"),
                                status
                        );
    }



    public static Specification<LoanTransaction> dateFrom(
            LocalDateTime from
    ) {

        return (root, query, cb) ->

                from == null
                        ? null
                        :
                        cb.greaterThanOrEqualTo(
                                root.get("requestDate"),
                                from
                        );
    }



    public static Specification<LoanTransaction> dateTo(
            LocalDateTime to
    ) {

        return (root, query, cb) ->

                to == null
                        ? null
                        :
                        cb.lessThanOrEqualTo(
                                root.get("requestDate"),
                                to
                        );
    }

}