package com.studio.smarters.foodfever;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView list;
    private DatabaseReference pendingRef;
    private ProgressDialog p;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        list=findViewById(R.id.order_history_list);
        mAuth=FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();
        p=new ProgressDialog(this);
        p.setTitle("Please Wait");
        p.setMessage("Please Wait While The Ready Orders are being loaded..");
        p.setCanceledOnTouchOutside(false);
        p.show();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);
        pendingRef= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("order_history");
        FirebaseRecyclerAdapter<OrderId,OrderViewHolder> f=new FirebaseRecyclerAdapter<OrderId, OrderViewHolder>(
                OrderId.class,
                R.layout.order_history_row,
                OrderViewHolder.class,
                pendingRef
        ) {
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, OrderId model, int position) {
                p.dismiss();
                final String orderId=model.getOrder_id();
                final DatabaseReference d=FirebaseDatabase.getInstance().getReference("orders").child(orderId);
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.child("name").getValue().toString();
                        String totalPrice=dataSnapshot.child("total_price").getValue().toString();
                        String status=dataSnapshot.child("status").getValue().toString();
                        viewHolder.setName(name);
                        viewHolder.setOrderNo(orderId);
                        viewHolder.setTotal(totalPrice);
                        viewHolder.setItemList(d.child("items"),getApplicationContext());
                        viewHolder.setStatus(status);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        list.setAdapter(f);
    }
}
