package com.library.library_management.repository.specification;

import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LoanSpecification {

    public static Specification<LoanTransaction> hasMember(String membershipNumber) {

        if (membershipNumber == null || membershipNumber.isBlank()) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.equal(root.get("member").get("membershipNumber"), membershipNumber.trim());
    }

    public static Specification<LoanTransaction> hasBook(String bookCode) {

        if (bookCode == null || bookCode.isBlank()) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.equal(root.get("book").get("bookCode"), bookCode.trim());
    }

    public static Specification<LoanTransaction> hasType(Enums.LoanType type) {

        if (type == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<LoanTransaction> hasStatus(Enums.LoanStatus status) {

        if (status == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<LoanTransaction> hasState(String state) {

        if (state == null || state.isBlank()) {
            return Specification.unrestricted();
        }

        String normalized = state.trim().toUpperCase();

        return switch (normalized) {

            case "RETURNED" ->
                    (root, query, cb) -> cb.and(cb.equal(root.get("type"), Enums.LoanType.RETURN), cb.isNotNull(root.get("returnDate")));


            case "NOT_RETURNED" -> (root, query, cb) -> {

                var childSubquery = query.subquery(Long.class);

                var child = childSubquery.from(LoanTransaction.class);

                childSubquery.select(child.get("id"));

                childSubquery.where(cb.equal(child.get("parentTransaction"), root));

                return cb.and(cb.equal(root.get("status"), Enums.LoanStatus.SUCCESS),

                        root.get("type").in(Enums.LoanType.BORROW, Enums.LoanType.RENEW),

                        cb.isNull(root.get("returnDate")),

                        cb.not(cb.exists(childSubquery)));
            };


            case "OVERDUE" -> (root, query, cb) -> {

                var childSubquery = query.subquery(Long.class);

                var child = childSubquery.from(LoanTransaction.class);

                childSubquery.select(child.get("id"));

                childSubquery.where(cb.equal(child.get("parentTransaction"), root));

                return cb.and(cb.equal(root.get("status"), Enums.LoanStatus.SUCCESS),

                        root.get("type").in(Enums.LoanType.BORROW, Enums.LoanType.RENEW),

                        cb.isNull(root.get("returnDate")),

                        cb.lessThan(root.get("dueDate"), LocalDateTime.now()),

                        cb.not(cb.exists(childSubquery)));
            };

            default ->
                    throw new IllegalArgumentException("Invalid loan history state. " + "Use RETURNED, NOT_RETURNED, or OVERDUE.");
        };
    }

    public static Specification<LoanTransaction> dateFrom(LocalDateTime from) {

        if (from == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("requestDate"), from);
    }

    public static Specification<LoanTransaction> dateTo(LocalDateTime to) {

        if (to == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("requestDate"), to);
    }
}