package com.ntt.data.bootcamp.msvc.account.infrastructure.persistence.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizedSignerCollection {
  private String documentType;
  private String documentNumber;
}
