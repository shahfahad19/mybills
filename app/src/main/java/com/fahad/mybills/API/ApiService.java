package com.fahad.mybills.API;

import com.fahad.mybills.Bill;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("bill/{ref}")
    Call<Bill> getBill(@Path("ref") String refNo);

    @GET("version")
    Call<VersionResponse> getVersion();
}
