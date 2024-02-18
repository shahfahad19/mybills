package com.fahad.mybills.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.RenderEffect;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fahad.mybills.API.ApiClient;
import com.fahad.mybills.API.ApiService;
import com.fahad.mybills.API.VersionResponse;
import com.fahad.mybills.Activities.BillInfoActivity;
import com.fahad.mybills.Bill;
import com.fahad.mybills.BillAdapter;
import com.fahad.mybills.DatabaseHelper;
import com.fahad.mybills.R;
import com.fahad.mybills.Utils.AppVersionUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BillAdapter.ItemClickListener {

    Button searchBtn;
    EditText ref;
    BillAdapter billAdapter;
    DatabaseHelper databaseHelper;
    RecyclerView recyclerView;
    List<Bill> bills;
    LinearLayout errorLayout;
    ApiService apiService;
    DrawerLayout drawerLayout;

    int checkedBills = 0, updatedBills =0, failedBills = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = ApiClient.getClient().create(ApiService.class);
        checkAppVersion();

        searchBtn = findViewById(R.id.searchBtn);
        errorLayout = findViewById(R.id.errorLayout);
        drawerLayout = findViewById(R.id.drawer_layout);

        ref = findViewById(R.id.ref);

        searchBtn.setOnClickListener(v-> {
            Intent intent = new Intent(getApplicationContext(), BillInfoActivity.class);
            intent.putExtra("ref", ref.getText().toString().trim());
            startActivity(intent);
        });

        databaseHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        bills = databaseHelper.getAllBills();

        billAdapter = new BillAdapter(this, this);
        recyclerView.setAdapter(billAdapter);

        if (bills.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
        }
        else {

           if (isInternetAvailable()) {
               for (Bill bill : bills) {
                   refreshBillData(bill.getRef());
               }
           }
           else {
               Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
           }
        }
    }

    @Override
    public void onItemClickListener(String id) {
        Intent intent = new Intent(getApplicationContext(), BillInfoActivity.class);
        intent.putExtra("ref", id);
        ref.clearFocus();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the latest list of bills from the database
        bills = databaseHelper.getAllBills();

        // Update the data in the RecyclerView
        billAdapter.updateData(bills);

        if (bills.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
        }
        else {
            errorLayout.setVisibility(View.GONE);
        }


    }

    // Method to send a request to the API to refresh data for a specific bill
    private void refreshBillData(String ref) {
        Call<Bill> requestCall =  apiService.getBill(ref);

        requestCall.enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                // Handle the API response and update the corresponding item in the RecyclerView
                if (response.isSuccessful()) {
                    Bill updatedBill = response.body();
                    Bill oldBill = databaseHelper.getBillByRef(ref);

                    if (!oldBill.getMonth().equals(updatedBill.getMonth()) || !oldBill.getCompany().equals(updatedBill.getCompany())) {
                        updateBillInList(updatedBill);
                        databaseHelper.updateBill(updatedBill);
                        updatedBills += 1;
                    }
                    checkIfAllRequestsCompleted(true);
                }
                else {
                    checkIfAllRequestsCompleted(false);
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                checkIfAllRequestsCompleted(false);
            }
        });

    }

    private void updateBillInList(Bill updatedBill) {
        int position = findBillPosition(updatedBill.getRef());
        if (position != -1) {
            bills.set(position, updatedBill);
            billAdapter.notifyItemChanged(position);
        }
    }

    private int findBillPosition(String ref) {
        for (int i = 0; i < bills.size(); i++) {
            if (bills.get(i).getRef().equals(ref)) {
                return i;
            }
        }
        return -1; // Return -1 if the bill is not found in the list
    }

    private void checkAppVersion() {
        Intent updateIntent = getIntent();
        boolean check = updateIntent.getBooleanExtra("check", true);

        if (check) {
            Call<VersionResponse> versionResponseCall = apiService.getVersion();

            versionResponseCall.enqueue(new Callback<VersionResponse>() {
                @Override
                public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                    if (response.isSuccessful()) {
                        VersionResponse versionResponse = response.body();
                        String versionName = AppVersionUtil.getAppVersionName(getApplicationContext());
                        int versionCode = AppVersionUtil.getAppVersionCode(getApplicationContext());

                        if (versionCode != versionResponse.getVersionCode() || !versionName.equals(versionResponse.getVersionName())) {
                            Intent updateIntent = new Intent(getApplicationContext(), UpdateActivity.class);
                            updateIntent.putExtra("link", versionResponse.getAppLink());
                            updateIntent.putExtra("message", versionResponse.getMessage());
                            updateIntent.putExtra("skipable", versionResponse.isSkipable());

                            startActivity(updateIntent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VersionResponse> call, Throwable t) {
                }
            });
        }
    }

    private synchronized void checkIfAllRequestsCompleted(boolean successful) {
        checkedBills +=1;
        if (!successful) {
            failedBills += 1;
        }

        if (bills.size() == checkedBills) {

            if (checkedBills != failedBills) {
                if (updatedBills == 0) {
//                    Toast.makeText(this, "All bills are up to date", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (updatedBills == 1) {
                        Toast.makeText(this, "1 bill updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, updatedBills+" bills updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
//                Toast.makeText(this, "Failed to check " + failedBills + " bills", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }

    public void openSidebar(View view) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
    }




}
