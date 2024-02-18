package com.fahad.mybills.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fahad.mybills.API.ApiClient;
import com.fahad.mybills.API.ApiErrorUtils;
import com.fahad.mybills.API.ApiException;
import com.fahad.mybills.API.ApiService;
import com.fahad.mybills.Bill;
import com.fahad.mybills.CustomDialog;
import com.fahad.mybills.DatabaseHelper;
import com.fahad.mybills.R;
import com.fahad.mybills.Utils.DateUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;


public class BillInfoActivity extends AppCompatActivity {

    ApiService apiService;
    TextView billCompany, billName, refNo, month, reading, units, dueDate, amount, daysRemaining, errText, unitsLabel;

    LinearLayout errLayout;

    CardView mainLayout;

    CustomDialog customDialog;

    Bill bill;

    Button saveBtn, deleteBtn, viewBillBtn;

    DatabaseHelper databaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info);

        apiService = ApiClient.getClient().create(ApiService.class);

        databaseHelper = new DatabaseHelper(this);

        customDialog = new CustomDialog(this);

        billCompany = findViewById(R.id.billCompany);
        billName = findViewById(R.id.billName);
        refNo = findViewById(R.id.refNo);
        month = findViewById(R.id.billMonth);
        reading = findViewById(R.id.readingDate);
        units = findViewById(R.id.units);
        dueDate = findViewById(R.id.dueDate);
        amount = findViewById(R.id.amount);
        daysRemaining = findViewById(R.id.remainingDays);
        unitsLabel = findViewById(R.id.unitsLabel);

        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);
        errLayout = findViewById(R.id.errorLayout);

        errText = findViewById(R.id.errText);

        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        viewBillBtn = findViewById(R.id.viewBillBtn);

        Intent intent = getIntent();
        String ref = intent.getStringExtra("ref");

        assert ref != null;
        if (ref.length() < 14) {
            unitsLabel.setText(R.string.gas_consumed);
        }

        Bill savedBill = databaseHelper.getBillByRef(ref);
        if (savedBill == null) {
            saveBtn.setVisibility(View.VISIBLE);
        }
        else {
            deleteBtn.setVisibility(View.VISIBLE);
        }
        saveBtn.setOnClickListener(v->{
            databaseHelper.insertBill(bill);
            customDialog.showAlert("Bill saved successfully!");
            saveBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        });

        deleteBtn.setOnClickListener(v->{
            customDialog.showConfirmation("Are you sure?", "Yes", "No", new CustomDialog.ConfirmationListener() {
                @Override
                public void onConfirmed() {
                    databaseHelper.deleteBill(ref);
                    finish();
                }
                public void onCancelled() {
                    customDialog.hide();
                }
            });


        });

        viewBillBtn.setOnClickListener(v-> {
            Intent billIntent = new Intent(getApplicationContext(), ViewBillActivity.class);
            billIntent.putExtra("bill", bill);
            startActivity(billIntent);
        });

        bill = databaseHelper.getBillByRef(ref);
        if (bill != null) {
            billCompany.setText(bill.getCompany());
            billName.setText(bill.getPersonName());
            refNo.setText(ref);
            month.setText(bill.getMonth());
            reading.setText(bill.getReadingDate());
            units.setText(bill.getUnits());
            dueDate.setText(bill.getDueDate());
            amount.setText("Rs. "+bill.getCurrentBill());

            int days = DateUtils.calculateDaysLeft(bill.getDueDate());
            String daysText = "";
            if (days == 0) {
                daysText = "Last day of payment";
            }
            else if (days > 0) {
                daysText = days +" days";
            }
            else {
                daysText = "Date expired";
            }
            daysRemaining.setText(daysText);

            mainLayout.setVisibility(View.VISIBLE);
        }

        else {
            getBillInfo(ref);
        }
    }

    public void getBillInfo(String ref) {


        Call<Bill> requestCall = apiService.getBill(ref);

        customDialog.showProgress("Loading bill");

        requestCall.enqueue(new Callback<Bill>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                customDialog.hide();

                // if request is successful
                if (response.isSuccessful()) {
                    mainLayout.setVisibility(View.VISIBLE);
                    bill = response.body();
                    billCompany.setText(bill.getCompany());
                    billName.setText(bill.getPersonName());
                    refNo.setText(ref);
                    month.setText(bill.getMonth());
                    reading.setText(bill.getReadingDate());
                    units.setText(bill.getUnits());
                    dueDate.setText(bill.getDueDate());
                    amount.setText("Rs. "+bill.getCurrentBill());

                    int days = bill.getRemainingDays();
                    String daysText = "";
                    if (days == 0) {
                        daysText = "Last day of payment";
                    }
                    else if (days > 0) {
                        daysText = days +" days";
                    }
                    else {
                        daysText = "Date expired";
                    }
                    daysRemaining.setText(daysText);
                }

                // If request failed
                else {
                    customDialog.hide();
                    errLayout.setVisibility(View.VISIBLE);

                    // Parsing Error
                    ApiException apiException = ApiErrorUtils.parseError(new HttpException(response));

                    // Getting error message
                    String errorMessage = apiException.getErrorMessage();

                    errText.setText(errorMessage);
                }
            }


            // If request failed due to a network error
            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                errLayout.setVisibility(View.VISIBLE);
                customDialog.hide();

                // Parsing error
                ApiException apiException = ApiErrorUtils.parseError(t);

                // Getting error message
                String errorMessage = apiException.getErrorMessage();

                // Show error message
                errText.setText(errorMessage);

            }
        });

    }

}