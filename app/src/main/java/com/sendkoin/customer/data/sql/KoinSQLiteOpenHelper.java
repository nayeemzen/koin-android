package com.sendkoin.customer.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sendkoin.sql.tables.CurrentOrderTable;
import com.sendkoin.sql.tables.InventoryOrderItemTable;
import com.sendkoin.sql.tables.PaymentTable;

public class KoinSQLiteOpenHelper extends SQLiteOpenHelper {
  public static final int VERSION = 2;
  public static final String DATABASE_NAME = "koin_customer_db";

  public KoinSQLiteOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(PaymentTable.getCreateTableQuery());
    db.execSQL(InventoryOrderItemTable.getCreateTableQuery());
    db.execSQL(CurrentOrderTable.getCreateTableQuery());
  }


  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(InventoryOrderItemTable.getCreateTableQuery());
    db.execSQL(CurrentOrderTable.getCreateTableQuery());
  }
}
