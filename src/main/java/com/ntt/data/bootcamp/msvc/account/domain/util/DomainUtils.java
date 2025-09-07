package com.ntt.data.bootcamp.msvc.account.domain.util;

import java.util.List;
import java.util.UUID;

public final class DomainUtils {

  public static void reformat(List<String> values) {
    values.forEach(value -> value.trim().toLowerCase());
  }

  public static boolean isValidUUID(String uuid) {
    try {
      UUID.fromString(uuid);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
