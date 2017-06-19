package com.sendkoin.customer.Data.Payments.Local;

import com.annimon.stream.Stream;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.sendkoin.api.QueryParameters;
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

  public PaymentEntity fromWire(Transaction transaction) {
    return new PaymentEntity(
        transaction.token,
        transaction.amount.longValue(),
        transaction.created_at,
        transaction.state.name(),
        transaction.merchant.store_name,
        transaction.merchant.store_type);
  }

  public Observable<PaymentEntity> getTime(boolean fetchHistory){
    String orderBy = (fetchHistory) ?
        PaymentTable.COLUMN_CREATED_AT + " ASC" : PaymentTable.COLUMN_CREATED_AT + " DESC";

    return storIOSQLite.get().object(PaymentEntity.class)
        .withQuery(Query.builder()
            .table(PaymentTable.TABLE)
            .orderBy(orderBy)
            .limit(1)
            .build())
        .prepare()
        .asRxObservable();
  }

  public List<PaymentEntity> getAllPayments() {
    List<PaymentEntity> paymentEntities = storIOSQLite.get()
        .listOfObjects(PaymentEntity.class)
        .withQuery(Query.builder().table(PaymentTable.TABLE).build())
        .prepare()
        .executeAsBlocking();
    return paymentEntities;

  }


  public void deleteAllPayments() {
    storIOSQLite.delete()
        .byQuery(DeleteQuery.builder().table(PaymentTable.TABLE).build())
        .prepare()
        .executeAsBlocking();
  }

}