package com.library.library_management.dto.dashboard;


public record DashboardResponse(

        long books,

        long members,

        long activeLoans,

        long unpaidFines

) {
}