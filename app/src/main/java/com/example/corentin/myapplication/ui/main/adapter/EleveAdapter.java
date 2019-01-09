package com.example.corentin.myapplication.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.corentin.myapplication.R;
import com.example.corentin.myapplication.data.model.Eleve;

import java.util.ArrayList;

public class EleveAdapter extends ArrayAdapter<Eleve> {
    public EleveAdapter(Context context, ArrayList<Eleve> eleves){
        super(context, 0, eleves);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Eleve eleve = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView tvNom = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvPrenom = (TextView) convertView.findViewById(R.id.tvMac);

        tvNom.setText(eleve.nom);
        tvPrenom.setText(eleve.prenom);


        return convertView;
    }
}
