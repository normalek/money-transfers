package com.revolut.transfers.dao.repository;

import com.revolut.transfers.entity.Account;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    @SqlQuery("select * from account where id = :id")
    @RegisterBeanMapper(Account.class)
    Optional<Account> getById(@Bind("id") Integer id);

    @SqlQuery("select * from account order by opened_date")
    @RegisterBeanMapper(Account.class)
    List<Account> getAll();

    @SqlUpdate("update account set balance = :balance where id = :account")
    boolean updateAccount(@Bind("account") Integer account, @Bind("balance") BigDecimal balance);

    @SqlUpdate("insert into account (alias,type,balance,opened_date,currency) VALUES (:alias, :type, :balance, :now, :currency)")
    @GetGeneratedKeys("id")
    @Timestamped
    Integer create(@Bind("alias") String alias, @Bind("type") String type, @Bind("balance") BigDecimal balance,
                   @Bind("currency") String currency);

    @SqlUpdate("update account set status = :status where id = :account")
    boolean changeStatusAccount(@Bind("account") Integer account, @Bind("status") String status);
}
