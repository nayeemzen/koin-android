package com.sendkoin.customer;

import com.sendkoin.api.Merchant;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionState;
import com.sendkoin.customer.data.FakePaymentService;

import java.util.Collections;
import java.util.List;

import static com.annimon.stream.Collectors.groupingBy;

/**
 * Miscellaneous Generator Class
 * - Generates Fake Transactions to test:
 *  1) LoadPaymentTest and how payments are ordered when loaded
 *  2) Static QR Payment Generation in QrPaymentTest
 * - Generates SaleItems and QrCodes Required for ANY FAKE TRANSACTION to be process
 */

public class MiscGenerator {

  /**
   * Function saves fake transactions in a LinkedHashMap to maintain order.
   * Purpose is to compare to transactions are loaded frm the local DB and whether they are loaded
   * in the correct order
   */
  public void createTransactionHash(){
    long timeStamp = System.currentTimeMillis() /1000L;
    String transactionToken = "1";
    Transaction transaction = new Transaction.Builder()
        .token(transactionToken)
        .created_at(timeStamp)
        .amount(300)
        .merchant(new Merchant.Builder()
            .store_name("Gloria Jeans")
            .store_type("Coffee Shop")
            .build())
        .state(TransactionState.COMPLETE)
        .build();
    FakePaymentService.paymentEntityLinkedHashMap.put(transactionToken, transaction);

    timeStamp = System.currentTimeMillis() /1000L;
    String transactionToken2 = "2";
    Transaction transaction2 = new Transaction.Builder()
        .token(transactionToken2)
        .created_at(timeStamp)
        .amount(400)
        .merchant(new Merchant.Builder()
            .store_name("Pizza Hut")
            .store_type("Restaurant")
            .build())
        .state(TransactionState.COMPLETE)
        .build();
    FakePaymentService.paymentEntityLinkedHashMap.put(transactionToken2,transaction2);
  }

  /**
   * Generates sale items required to make ANY transaction
   * @return Singleton list of sale items according to SaleItem proto buff.
   */
  public List<SaleItem> generateSaleItems() {
    return Collections.singletonList(
        new SaleItem.Builder()
            .name("One Time Payment")
            .quantity(1)
            .sale_type(SaleItem.SaleType.QUICK_SALE)
            .price(250).build());
  }

  /**
   * Generates a QrCode required to make any transaction
   * @param qrType enum{
   *               DYNAMIC,
   *               STATIC,
   *               INVENTORY_STATIC
   * }
   * @param qrToke any String
   * @return
   */
  public QrCode generateQRCode(QrType qrType, String qrToke) {
    return new QrCode.Builder()
        .amount(250)
        .merchant_name("Tester Merchant")
        .qr_token(qrToke)
        .qr_type(qrType)
        .build();
  }
}
