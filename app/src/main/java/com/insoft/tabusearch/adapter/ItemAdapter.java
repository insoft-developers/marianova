package com.insoft.tabusearch.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.insoft.tabusearch.R;
import com.insoft.tabusearch.model.Tabulist;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>  {

    private Context context;
    private List<Tabulist> datawisata;

    public ItemAdapter(Context context, List<Tabulist> datawisata) {
        this.context = context;
        this.datawisata = datawisata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView_Name.setText(datawisata.get(position).getNama_wisata());
        int id_wisata = datawisata.get(position).getId();
        String nama_wisata = datawisata.get(position).getNama_wisata();
        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result_intent = new Intent();
                result_intent.putExtra("id_wisata", id_wisata);
                result_intent.putExtra("nama_wisata", nama_wisata);
                ((Activity) context).setResult(Activity.RESULT_OK, result_intent);
                ((Activity) context).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return datawisata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView_Name;
        private LinearLayout rootlayout;


        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_menu);
            textView_Name = itemView.findViewById(R.id.txt_menu);
            rootlayout = itemView.findViewById(R.id.rootlayout);

        }
    }

}
