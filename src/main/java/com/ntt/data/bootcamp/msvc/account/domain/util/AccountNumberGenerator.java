package com.ntt.data.bootcamp.msvc.account.domain.util;

public final class AccountNumberGenerator {

  private static final String BANK_CODE = "002";
  private static final String BRANCH_CODE = "001";

  public static String generateInternal() {
    long sequence = getNextSequenceNumber();
    return String.format("%014d", sequence);
  }

  public static String generateExternal(String internalNumber) {
    return BANK_CODE + internalNumber + BRANCH_CODE;
  }

  private static long getNextSequenceNumber() {
    long timestamp = System.currentTimeMillis();
    int random = (int) (Math.random() * 1000);
    return (timestamp + random) % 100000000000000L;
  }
}
