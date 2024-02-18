package com.fahad.mybills;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bills.db";
    private static final int DATABASE_VERSION = 2;

    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_COMPANY = "company";
    private static final String TABLE_NAME = "bills";
    private static final String COLUMN_BILL_REF = "ref";
    private static final String COLUMN_BILL_NAME = "bill_name";
    private static final String COLUMN_UNITS = "units";
    private static final String COLUMN_BILL_MONTH = "bill_month";
    private static final String COLUMN_READING_DATE = "reading_date";
    private static final String COLUMN_CURRENT_BILL = "current_bill";
    private static final String COLUMN_AFTER_DUE_BILL = "after_due_bill";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_REMAINING_DAYS = "remaining_days";
    private static final String COLUMN_BILL_DATA = "bill_data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Existing table creation
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_BILL_REF + " TEXT PRIMARY KEY, " +
                COLUMN_BILL_NAME + " TEXT, " +
                COLUMN_UNITS + " TEXT, " +
                COLUMN_BILL_MONTH + " TEXT, " +
                COLUMN_READING_DATE + " TEXT, " +
                COLUMN_CURRENT_BILL + " TEXT, " +
                COLUMN_AFTER_DUE_BILL + " TEXT, " +
                COLUMN_DUE_DATE + " TEXT, " +
                COLUMN_REMAINING_DAYS + " INTEGER, " +
                // New columns
                COLUMN_TYPE + " TEXT DEFAULT '', " +
                COLUMN_COMPANY + " TEXT DEFAULT '', " +
                COLUMN_BILL_DATA + " TEXT DEFAULT '')";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TYPE + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_COMPANY + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_BILL_DATA + " TEXT DEFAULT ''");
        }
    }

    public long insertBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, bill.getType());
        values.put(COLUMN_COMPANY, bill.getCompany());
        values.put(COLUMN_BILL_REF, bill.getRef());
        values.put(COLUMN_BILL_NAME, bill.getPersonName());
        values.put(COLUMN_UNITS, bill.getUnits());
        values.put(COLUMN_BILL_MONTH, bill.getMonth());
        values.put(COLUMN_READING_DATE, bill.getReadingDate());
        values.put(COLUMN_CURRENT_BILL, bill.getCurrentBill());
        values.put(COLUMN_AFTER_DUE_BILL, bill.getAfterBill());
        values.put(COLUMN_DUE_DATE, bill.getDueDate());
        values.put(COLUMN_REMAINING_DAYS, bill.getRemainingDays());
        values.put(COLUMN_BILL_DATA, bill.getBillData());

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    @SuppressLint("Range")
    public List<Bill> getAllBills() {
        List<Bill> billList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_TYPE,
                COLUMN_COMPANY,
                COLUMN_BILL_REF,
                COLUMN_BILL_NAME,
                COLUMN_UNITS,
                COLUMN_BILL_MONTH,
                COLUMN_READING_DATE,
                COLUMN_CURRENT_BILL,
                COLUMN_AFTER_DUE_BILL,
                COLUMN_DUE_DATE,
                COLUMN_REMAINING_DAYS,
                COLUMN_BILL_DATA
        };

        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                String company = cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY));
                String ref = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_REF));
                String personName = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_NAME));
                String units = cursor.getString(cursor.getColumnIndex(COLUMN_UNITS));
                String month = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_MONTH));
                String readingDate = cursor.getString(cursor.getColumnIndex(COLUMN_READING_DATE));
                String currentBill = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_BILL));
                String afterBill = cursor.getString(cursor.getColumnIndex(COLUMN_AFTER_DUE_BILL));
                String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));
                int remainingDays = cursor.getInt(cursor.getColumnIndex(COLUMN_REMAINING_DAYS));
                String billData = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_DATA));

                Bill bill = new Bill(type, company, ref, personName, month, readingDate, units, currentBill, afterBill, dueDate, remainingDays, billData);
                billList.add(bill);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return billList;
    }

    public int updateBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Existing columns
        values.put(COLUMN_TYPE, bill.getType());
        values.put(COLUMN_COMPANY, bill.getCompany());
        values.put(COLUMN_BILL_NAME, bill.getPersonName());
        values.put(COLUMN_UNITS, bill.getUnits());
        values.put(COLUMN_BILL_MONTH, bill.getMonth());
        values.put(COLUMN_READING_DATE, bill.getReadingDate());
        values.put(COLUMN_CURRENT_BILL, bill.getCurrentBill());
        values.put(COLUMN_AFTER_DUE_BILL, bill.getAfterBill());
        values.put(COLUMN_DUE_DATE, bill.getDueDate());
        values.put(COLUMN_REMAINING_DAYS, bill.getRemainingDays());
        values.put(COLUMN_BILL_DATA, bill.getBillData());

        return db.update(TABLE_NAME, values, COLUMN_BILL_REF + " = ?", new String[]{bill.getRef()});
    }

    @SuppressLint("Range")
    public Bill getBillByRef(String ref) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_TYPE,
                COLUMN_COMPANY,
                COLUMN_BILL_REF,
                COLUMN_BILL_NAME,
                COLUMN_UNITS,
                COLUMN_BILL_MONTH,
                COLUMN_READING_DATE,
                COLUMN_CURRENT_BILL,
                COLUMN_AFTER_DUE_BILL,
                COLUMN_DUE_DATE,
                COLUMN_REMAINING_DAYS,
                COLUMN_BILL_DATA
        };

        Cursor cursor = db.query(TABLE_NAME, projection, COLUMN_BILL_REF + " = ?", new String[]{ref}, null, null, null);

        Bill bill = null;
        if (cursor != null && cursor.moveToFirst()) {
            String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
            String company = cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY));
            String personName = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_NAME));
            String units = cursor.getString(cursor.getColumnIndex(COLUMN_UNITS));
            String month = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_MONTH));
            String readingDate = cursor.getString(cursor.getColumnIndex(COLUMN_READING_DATE));
            String currentBill = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_BILL));
            String afterBill = cursor.getString(cursor.getColumnIndex(COLUMN_AFTER_DUE_BILL));
            String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));
            int remainingDays = cursor.getInt(cursor.getColumnIndex(COLUMN_REMAINING_DAYS));
            String billData = cursor.getString(cursor.getColumnIndex(COLUMN_BILL_DATA));

            bill = new Bill(type, company, ref, personName, month, readingDate, units, currentBill, afterBill, dueDate, remainingDays, billData);

            cursor.close();
        }

        db.close();
        return bill;
    }

    public int deleteBill(String ref) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_BILL_REF + " = ?", new String[]{ref});
    }
}
