package com.library.library_management.entity;

public class Enums {

    public enum MemberStatus {
        ACTIVE,
        INACTIVE,
        BLOCKED
    }

    public enum BookStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }
    public enum LoanType {
        BORROW,
        RETURN,
        RENEW
    }

    public enum MembershipType {
        INDIVIDUAL,
        ORGANIZATIONAL
    }

    public enum LoanStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    public enum FineStatus {
        UNPAID,
        PAID
    }
}