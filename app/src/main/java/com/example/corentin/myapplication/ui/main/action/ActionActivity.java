package com.example.corentin.myapplication.ui.main.action;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.corentin.myapplication.R;
import com.example.corentin.myapplication.data.model.local.LocalPreferences;
import com.example.corentin.myapplication.data.model.service.ApiService;
import com.example.corentin.myapplication.remote.LedStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionActivity extends AppCompatActivity {

    private final ApiService apiService = ApiService.Builder.getInstance();


    public static Intent getStartIntent(final Context ctx) {
        return new Intent(ctx, ActionActivity.class);
    }

    Button refresh;
    ImageButton status;
    private LedStatus ledStatus = new LedStatus();
    private LedStatus newStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        final String currentSelectedDevice = LocalPreferences.getInstance(this).getCurrentSelectedDevice();
        if (currentSelectedDevice == null) {
            Toast.makeText(this, "Aucun périphérique connu", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ledStatus.setIdentifier(currentSelectedDevice);

            refresh = findViewById(R.id.refresh);
            status = findViewById(R.id.eclair);

            refresh.setOnClickListener(v -> refreshLedState());
            status.setOnClickListener(v -> toggleWithNetwork());
        }
    }

    private void refreshLedState() {
        apiService.readStatus(ledStatus.getIdentifier()).enqueue(new Callback<LedStatus>() {
            @Override
            public void onResponse(Call<LedStatus> call, Response<LedStatus> ledStatusResponse) {
                runOnUiThread(() -> {
                    if (ledStatusResponse.body() != null) {
                        newStatus = ledStatusResponse.body(); // LedStatus
                        stateLed(newStatus.getStatus());
                    }
                });
            }

            @Override
            public void onFailure(Call<LedStatus> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ActionActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void toggleWithNetwork(){
        boolean state = ledStatus.getStatus();
        apiService.writeStatus(ledStatus).enqueue(new Callback<LedStatus>() {
            @Override
            public void onResponse(Call<LedStatus> call, Response<LedStatus> ledStatusResponse) {
                runOnUiThread(() -> {
                    if (ledStatusResponse.body() != null) {
                        stateLed(!state);
                    }
                });
            }

            @Override
            public void onFailure(Call<LedStatus> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ActionActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void stateLed(boolean status_bool){
        ledStatus.setStatus(status_bool);
        if(status_bool){
            status.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
        }
        else{
            status.setImageResource(R.drawable.eteins);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLedState();
    }

}