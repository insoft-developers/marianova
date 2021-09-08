package com.insoft.tabusearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.insoft.tabusearch.Utils.RegisterAPI;
import com.insoft.tabusearch.Utils.UtilsAPI;
import com.insoft.tabusearch.adapter.ItemAdapter;
import com.insoft.tabusearch.json.TabusearchResponseJson;

import retrofit2.Call;
import retrofit2.Callback;

public class WisataActivity extends AppCompatActivity {

    private RecyclerView rv_menu;
    private RegisterAPI registerAPI;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisata);
        registerAPI = UtilsAPI.getApiService();
        rv_menu = findViewById(R.id.rv_wisata);
        loading = findViewById(R.id.loading);
        LinearLayoutManager layoutManager = new LinearLayoutManager(WisataActivity.this);
        rv_menu.setLayoutManager(layoutManager);
        fetch_data();

    }

    private void fetch_data() {
        loading.setVisibility(View.VISIBLE);
        registerAPI = UtilsAPI.getApiService();
        registerAPI.daftarwisata().enqueue(new Callback<TabusearchResponseJson>() {
            @Override
            public void onResponse(Call<TabusearchResponseJson> call, retrofit2.Response<TabusearchResponseJson> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ItemAdapter itemAdapter = new ItemAdapter(WisataActivity.this, response.body().getData());
                    itemAdapter.notifyDataSetChanged();
                    rv_menu.setAdapter(itemAdapter);
                } else {
                    Log.d("REs", response.toString());
                }
            }

            @Override
            public void onFailure(Call<TabusearchResponseJson> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(WisataActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}