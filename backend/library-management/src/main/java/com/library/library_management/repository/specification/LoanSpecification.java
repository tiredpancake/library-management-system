package com.library.library_management.repository.specification;

import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LoanSpecification {

    public static Specification<LoanTransaction> hasMember(String membershipNumber) {
        return (root, query, cb) -> membershipNumber == null || membershipNumber.isBlank() ? null : cb.equal(root.get("member").get("membershipNumber"), membershipNumber);
    }

    public static Specification<LoanTransaction> hasBook(String bookCode) {
        return (root, query, cb) -> bookCode == null || bookCode.isBlank() ? null : cb.equal(root.get("book").get("bookCode"), bookCode);
    }

    public static Specification<LoanTransaction> hasType(Enums.LoanType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<LoanTransaction> hasStatus(Enums.LoanStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<LoanTransaction> hasState(Enums.LoanState state) {

        if (state == null) {
            return null;
        }

        return switch (state) {

            case RETURNED -> (root, query, cb) -> cb.isNotNull(root.get("returnDate"));

            case NOT_RETURNED -> (root, query, cb) -> {

                var childSubquery = query.subquery(Long.class);

                var child = childSubquery.from(LoanTransaction.class);

                childSubquery.select(child.get("id"));

                childSubquery.where(cb.equal(child.get("parentTransaction"), root));

                return cb.and(cb.equal(root.get("status"), Enums.LoanStatus.SUCCESS),

                        root.get("type").in(Enums.LoanType.BORROW, Enums.LoanType.RENEW),

                        cb.isNull(root.get("returnDate")),

                        cb.not(cb.exists(childSubquery)));
            };

            case OVERDUE -> (root, query, cb) -> {

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
        };
    }

    public static Specification<LoanTransaction> dateFrom(LocalDateTime from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("requestDate"), from);
    }

    public static Specification<LoanTransaction> dateTo(LocalDateTime to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("requestDate"), to);
    }
}