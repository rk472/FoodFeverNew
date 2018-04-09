package com.studio.smarters.foodfever;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView list;
    private DatabaseReference mRef,totalRef;
    private TextView totalPrice,emptyText;
    private  String total="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mAuth = FirebaseAuth.getInstance();
        totalRef=FirebaseDatabase.getInstance().getReference().child("cart").child("total_cart").child(mAuth.getCurrentUser().getUid());
        list=findViewById(R.id.cart_list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        totalPrice=findViewById(R.id.total_cart_price);
        emptyText=findViewById(R.id.empty_cart);
        mRef= FirebaseDatabase.getInstance().getReference().child("cart");
        totalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total=dataSnapshot.child("total_price").getValue().toString();
                totalPrice.setText(total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                    FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart,CartViewHolder>(
                            Cart.class,
                            R.layout.cart_row,
                            CartViewHolder.class,
                            mRef.child(mAuth.getCurrentUser().getUid())
                    ) {
                        @Override
                        protected void populateViewHolder(CartViewHolder viewHolder, final Cart model, final int position) {
                            viewHolder.setData(model.getItem_name(),model.getItem_price(),model.getItem_total_price(),model.getItem_quantity());
                            viewHolder.removeButon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String key=getRef(position).getKey();
                                    String newTotal=Integer.toString(Integer.parseInt(total)-Integer.parseInt(model.getItem_total_price()));
                                    total=newTotal;
                                    totalRef.child("total_price").setValue(total);
                                    mRef.child(mAuth.getCurrentUser().getUid()).child(key).removeValue();
                                }
                            });
                        }
                    };
                    list.setAdapter(adapter);
                    list.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                }else{
                    emptyText.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }
    public static class CartViewHolder extends RecyclerView.ViewHolder{
        TextView nameText,priceText,quantityText,totalText;
        Button removeButon;
        public CartViewHolder(View itemView) {
            super(itemView);
            nameText=itemView.findViewById(R.id.cart_item_name);
            priceText=itemView.findViewById(R.id.cart_item_price);
            removeButon=itemView.findViewById(R.id.delete_btn);
            quantityText=itemView.findViewById(R.id.cart_item_quantity);
            totalText=itemView.findViewById(R.id.cart_total_price);
        }
        public void setData(String name,int price,String tot,String qty){
            nameText.setText(name);
            priceText.setText(price+"");
            quantityText.setText(qty);
            totalText.setText(tot);
        }
    }
    public void order(View v){
        final DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("orders") .push();
        final String orderKey=orderRef.getKey();
        mRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    final String key=d.getKey();
                    Map m=new HashMap();
                    m.put("item_name",d.child("item_name").getValue().toString());
                    m.put("item_price",d.child("item_price").getValue().toString());
                    m.put("item_quantity",d.child("item_quantity").getValue().toString());
                    m.put("item_total_price",d.child("item_total_price").getValue().toString());
                    orderRef.child("items").child(key).updateChildren(m);

                }
                mRef.child(mAuth.getCurrentUser().getUid()).removeValue();
                mRef.child(mAuth.getCurrentUser().getUid()).removeEventListener(this);
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        orderRef.child("name").setValue(dataSnapshot1.child("name").getValue().toString());
                        orderRef.child("time").setValue(ServerValue.TIMESTAMP);
                        orderRef.child("status").setValue("pending");
                        FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid()+"/order_history").push().child("order_id").setValue(orderKey);
                        FirebaseDatabase.getInstance().getReference("pending_orders").push().child("order_id").setValue(orderKey);
                        totalRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                total=dataSnapshot.child("total_price").getValue().toString();
                                totalRef.child("total_price").setValue("0");
                                orderRef.child("total_price").setValue(total);
                                totalRef.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toast.makeText(this, "Your order is placed successfully...", Toast.LENGTH_SHORT).show();

    }
}
