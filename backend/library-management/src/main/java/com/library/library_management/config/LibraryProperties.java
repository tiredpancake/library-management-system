package com.library.library_management.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "library")
public class LibraryProperties {

    private int maxActiveLoans;
    private int maxUnpaidFine;
    private int maxRenewCount;
    private int loanPeriodDays;
    private long finePerDay;
    private long maxFine;
    private int maxOverdueDays;

}