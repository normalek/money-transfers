package com.revolut.transfers.api.dto.rs;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AccountRsList {
    List<AccountRs> accounts;
}
