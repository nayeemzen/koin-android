package com.sendkoin.customer.Data.Payments.Local;

import com.annimon.stream.Stream;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.sendkoin.api.Transaction;
import com.sendkoin.sql.entities.PaymentEntity;
import com.sendkoin.sql.tables.PaymentTable;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by warefhaque on 5/20/17.
 */

public class LocalPaymentDataStore {

  private StorIOSQLite storIOSQLite;

  @Inject
  public LocalPaymentDataStore(StorIOSQLite storIOSQLite) {

    this.storIOSQLite = storIOSQLite;
  }

  public Observable<PutResult> createTransaction(Transaction transaction) {
    return storIOSQLite.put().object(fromWire(transaction)).prepare().asRxObservable();
  }


  public Observable<List<PaymentEntity>> getAllTransactions() {
    return storIOSQLite.get()
        .listOfObjects(PaymentEntity.class)
        .withQuery(Query.builder()
            .table(PaymentTable.TABLE)
            .orderBy(PaymentTable.COLUMN_CREATED_AT + " DESC")
            .build())
        .prepare()
        .asRxObservable();
  }

  public Observable<PutResults<PaymentEntity>> saveAllTransactions(List<Transaction> transactions) {
    return storIOSQLite.put()
        .objects(Stream.of(transactions)
            .map(this::fromWire)
            .toList())
        .prepare()
        .asRxObservable();
  }

  private PaymentEntity fromWire(Transaction transaction) {
    return new PaymentEntity(
        transaction.token,
        transaction.amount.longValue(),
        transaction.created_at,
        transaction.state.name(),
        transaction.merchant.store_name,
        transaction.merchant.store_type);
  }


  public long getLastSeenTransaction() {
    PaymentEntity lastSeen = storIOSQLite.get().object(PaymentEntity.class)
        .withQuery(Query.builder()
            .table(PaymentTable.TABLE)
            .orderBy(PaymentTable.COLUMN_CREATED_AT + " DESC")
            .limit(1)
            .build())
        .prepare()
        .executeAsBlocking();
    return (lastSeen == null) ? 0 : lastSeen.getCreatedAt();
  }

  public long getEarliestSeenTransaction() {
    PaymentEntity earliestSeen = storIOSQLite.get().object(PaymentEntity.class)
        .withQuery(Query.builder()
            .table(PaymentTable.TABLE)
            .orderBy(PaymentTable.COLUMN_CREATED_AT + " ASC")
            .limit(1)
            .build())
        .prepare()
        .executeAsBlocking();
    return (earliestSeen == null) ? 0 : earliestSeen.getCreatedAt();
  }
}