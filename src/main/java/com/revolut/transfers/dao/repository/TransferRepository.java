package com.revolut.transfers.dao.repository;

import com.revolut.transfers.entity.Transfer;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferRepository {
    @SqlUpdate("insert into transfer(from_acc,to_acc,amount,currency,trans_date, status)" +
            " values (:from_acc, :to_acc, :amount, :currency, :now, :status)")
    @GetGeneratedKeys("trans_id")
    @Timestamped
    UUID insert(@Bind("from_acc") Integer fromAccount, @Bind("to_acc") Integer toAccount,
                @Bind("amount") BigDecimal amount, @Bind("currency") String currency,
                @Bind("status") String status);

    @SqlQuery("select * from transfer where trans_id = :id")
    @RegisterBeanMapper(Transfer.class)
    Transfer getTransferByTransId(@Bind("id") UUID id);
}
