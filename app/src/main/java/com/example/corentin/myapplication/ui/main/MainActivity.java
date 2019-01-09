package com.example.corentin.myapplication.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.corentin.myapplication.R;
import com.example.corentin.myapplication.ui.main.adapter.EleveAdapter;
import com.example.corentin.myapplication.data.model.Device;
import com.example.corentin.myapplication.data.model.Eleve;
import com.example.corentin.myapplication.ui.main.scan.ScanActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

   private ArrayList<Device> deviceArrayList = new ArrayList<>();

   private ArrayList<Eleve> eleveArrayList = new ArrayList<>();
   TextView btCommander;
   // appCOmpatActivity permet de gérer tout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permet d'avoir une bulle
        Toast.makeText(this,"bonjour à tous", Toast.LENGTH_LONG).show();

        /*Material Dialog qui crée une boite de dialogue
        new MaterialDialog.Builder(this)
                .title(R.string.title)
                .content(R.string.content).backgroundColor(getResources().getColor(R.color.persoColor))
                .positiveText(R.string.agree)

                .negativeText(R.string.desagree)
                .show();
        */
       TextView btCommander = findViewById(R.id.commander);
       TextView btScan = findViewById(R.id.scan);
       btCommander.setOnClickListener(this::onClick2);
       btScan.setOnClickListener(this::onClick);


      /*  tvHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"ah oui oui oui",Toast.LENGTH_SHORT).show();
            }
        }); */

        //list d'élèves
        EleveAdapter eAdapter = new EleveAdapter(this,eleveArrayList);
        //ListView list = findViewById(R.id.listView);
        //list.setAdapter(eAdapter);
        eAdapter.add(new Eleve("corentin   ", "Audebert"));
        eAdapter.add(new Eleve("paul  ", "Ouistiti"));
        eAdapter.add(new Eleve("romain   ", "Legoalec"));
        eAdapter.add(new Eleve("Matthieu ", "Herault"));
        eAdapter.add(new Eleve("blabal", "de monmiraille"));
        eAdapter.add(new Eleve("essai nom  ", "essai prenom"));


        /*
        DeviceAdapter adapter = new DeviceAdapter(this,deviceArrayList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        for(int i = 0 ; i < 20; i++) {
            adapter.add(new Device("Capteur n°"+i+"   : ", "12:56:89:"+i+64));
        }*/


    }

    public static Intent getStartIntent(final Context ctx){
        return new Intent(ctx,MainActivity.class);
    }
    private void onClick(View l) {
        startActivity(ScanActivity.getStartIntent(this));
        Toast.makeText(this, "bonus point", Toast.LENGTH_SHORT).show();
    }
    private void onClick2(View l) {
        Toast.makeText(this, "bonus point", Toast.LENGTH_SHORT).show();
    }


}
