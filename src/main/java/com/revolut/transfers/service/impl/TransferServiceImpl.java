package com.revolut.transfers.service.impl;

import com.google.inject.Inject;
import com.revolut.transfers.api.dto.rq.TransferRq;
import com.revolut.transfers.api.dto.rs.TransferRs;
import com.revolut.transfers.dao.TransfersDAO;
import com.revolut.transfers.entity.Transfer;
import com.revolut.transfers.service.TransferService;
import com.revolut.transfers.validator.TransferValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransfersDAO transfersDAO;
    private final TransferValidator validator;

    @Inject
    public TransferServiceImpl(TransfersDAO transfersDAO, TransferValidator validator) {
        this.transfersDAO = transfersDAO;
        this.validator = validator;
    }

    @Override
    public TransferRs transfer(TransferRq request) {
        validator.validateTransferRequest(request);
        Transfer transfer = transfersDAO.transfer(Integer.valueOf(request.getFromAccount()),
                Integer.valueOf(request.getToAccount()), request.getAmount(),
                request.getCurrency());
        return mapObject(transfer);
    }

    @Override
    public TransferRs findTransferByTransId(UUID transId) {
        Transfer transfer = transfersDAO.findTransferById(transId);
        return mapObject(transfer);
    }

    private TransferRs mapObject(Transfer transfer) {
        return TransferRs.builder()
                .status(transfer.getStatus())
                .transactionId(transfer.getTransId())
                .transactionDate(transfer.getTransDate())
                .amount(transfer.getAmount())
                .currency(transfer.getCurrency())
                .fromAccount(transfer.getFromAcc())
                .toAccount(transfer.getToAcc())
                .build();

    }
}
