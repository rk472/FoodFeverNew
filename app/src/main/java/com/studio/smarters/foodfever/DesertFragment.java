package com.studio.smarters.foodfever;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DesertFragment extends Fragment {
    View root;
    DatabaseReference dessertRef;
    RecyclerView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_desert, container, false);
        dessertRef= FirebaseDatabase.getInstance().getReference().child("items").child("dessert");
        FirebaseRecyclerAdapter<Items,DessertViewHolder> f=new FirebaseRecyclerAdapter<Items, DessertViewHolder>(
                Items.class,
                R.layout.items_row,
                DessertViewHolder.class,
                dessertRef
        ) {
            @Override
            protected void populateViewHolder(DessertViewHolder viewHolder, Items model, int position) {
                String name=getRef(position).getKey();
                int price=model.getPrice();
                viewHolder.setData(name,price);
            }
        };
        list=root.findViewById(R.id.dessert_list);
        list.setAdapter(f);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setHasFixedSize(true);


        return root;
    }
    public static class DessertViewHolder extends RecyclerView.ViewHolder{
        TextView nameText,priceText;
        EditText quantityText;
        Button addButon;
        public DessertViewHolder(View itemView) {
            super(itemView);
            nameText=itemView.findViewById(R.id.item_name);
            priceText=itemView.findViewById(R.id.item_price);
            quantityText=itemView.findViewById(R.id.item_quantity);
            addButon=itemView.findViewById(R.id.add_button);
        }
        public void setData(String name,int price){
            nameText.setText(name);
            priceText.setText(price+"");
        }
    }



}
