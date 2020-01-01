package com.revolut.transfers.service;

import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.api.dto.rs.TransferRs;

import java.util.UUID;

public interface TransferService {

    TransferRs transfer(TransferRq request);

    TransferRs findTransferByTransId(UUID transId);
}
