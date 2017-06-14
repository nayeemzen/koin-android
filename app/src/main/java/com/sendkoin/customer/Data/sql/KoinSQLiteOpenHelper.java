package com.sendkoin.customer.Data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sendkoin.sql.tables.PaymentTable;

public class KoinSQLiteOpenHelper extends SQLiteOpenHelper {
  public static final int VERSION = 1;
  public static final String DATABASE_NAME = "koin_customer_db";

  public KoinSQLiteOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(PaymentTable.getCreateTableQuery());
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
