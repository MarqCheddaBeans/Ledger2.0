package com.pluralsight.daos;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface ITransactionDao {

    ArrayList customSearch();

    ArrayList sortByVendor();

    ArrayList sortPreviousYear();

    ArrayList sortPreviousMonth();

    ArrayList sortYearToDate();

    ArrayList sortMonthToDate();

    ArrayList sortByPayments();

    ArrayList sortByDeposits();

    ArrayList userPayment();

    ArrayList userDeposit();

    ArrayList displayFullLedger();
}
