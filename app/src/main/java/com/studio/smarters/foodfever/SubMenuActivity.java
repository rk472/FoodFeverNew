package com.studio.smarters.foodfever;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubMenuActivity extends AppCompatActivity {
    private DatabaseReference subMenuRef;
    private FirebaseAuth mAuth;
    private AlertDialog dialog1;
    private RecyclerView list;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Please Wait while Your Menu is being loaded..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        list=findViewById(R.id.sub_menu_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);
        String path=getIntent().getExtras().getString("path");
        subMenuRef= FirebaseDatabase.getInstance().getReference(path);
        subMenuRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        FirebaseRecyclerAdapter<Items,DesertFragment.DessertViewHolder> f=new FirebaseRecyclerAdapter<Items, DesertFragment.DessertViewHolder>(
                Items.class,
                R.layout.items_row,
                DesertFragment.DessertViewHolder.class,
                subMenuRef
        ) {
            @Override
            protected void populateViewHolder(DesertFragment.DessertViewHolder viewHolder, final Items model, int position) {
                progressDialog.dismiss();
                final String name=getRef(position).getKey();
                subMenuRef.child(name).child("availability").setValue("Available");
                final int price=model.getPrice();
                final String description = model.getDesc();
                viewHolder.setData(name,price);
                viewHolder.addButon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SubMenuActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.layout_add_cart,null);
                        //Setting Views
                        TextView iName = mView.findViewById(R.id.cart_item_name);
                        TextView iPrice = mView.findViewById(R.id.cart_item_price);
                        final TextView iTotalPrice = mView.findViewById(R.id.cart_item_total_price);
                        final EditText iQuantity = mView.findViewById(R.id.cart_item_quantity);
                        Button iCart = mView.findViewById(R.id.add_cart);
                        iName.setText(name.toUpperCase());
                        iPrice.setText(price+"");
                        iTotalPrice.setText(price+"");
                        //Setting Listeners
                        iQuantity.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }
                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if("".equals(iQuantity.getText().toString())){
                                    iTotalPrice.setText("0");
                                }else{
                                    iTotalPrice.setText(""+(price*Integer.parseInt(iQuantity.getText().toString())));
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });
                        iCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(iQuantity.getText().toString().equals("")){
                                    Toast.makeText(getApplicationContext(), "Quantity Can't be blank...", Toast.LENGTH_SHORT).show();
                                    iQuantity.setText("1");
                                }else{
                                    DatabaseReference mCartRef = FirebaseDatabase.getInstance().getReference().child("cart");
                                    String cartkey = mCartRef.child(mAuth.getCurrentUser().getUid()).push().getKey();
                                    mCartRef.child(mAuth.getCurrentUser().getUid()).child(cartkey).child("item_name").setValue(name);
                                    mCartRef.child(mAuth.getCurrentUser().getUid()).child(cartkey).child("item_quantity").setValue(iQuantity.getText().toString());
                                    mCartRef.child(mAuth.getCurrentUser().getUid()).child(cartkey).child("item_total_price").setValue(iTotalPrice.getText().toString());
                                    mCartRef.child(mAuth.getCurrentUser().getUid()).child(cartkey).child("item_price").setValue(model.getPrice());

                                    final DatabaseReference mTotalRef = FirebaseDatabase.getInstance().getReference().child("cart").child("total_cart");
                                    mTotalRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                                            {
                                                String valOld = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("total_price").getValue().toString();
                                                int tot_val = Integer.parseInt(valOld)+Integer.parseInt(iTotalPrice.getText().toString());
                                                mTotalRef.child(mAuth.getCurrentUser().getUid()).child("total_price").setValue(tot_val+"");
                                            }else{
                                                mTotalRef.child(mAuth.getCurrentUser().getUid()).child("total_price").setValue(iTotalPrice.getText().toString());
                                            }
                                            mTotalRef.removeEventListener(this);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    dialog1.dismiss();
                                    Toast.makeText(SubMenuActivity.this, "Item Added to Cart Succesfully...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        mBuilder.setView(mView);
                        dialog1 = mBuilder.create();
                        dialog1.show();
                    }
                });
                if(!description.equals(" ")){
                    viewHolder.descText.setText("Click to Know The Item");
                    viewHolder.descText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SubMenuActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.layout,null);
                            TextView iName = mView.findViewById(R.id.item_name);
                            TextView iDesc = mView.findViewById(R.id.item_desc);
                            iName.setText(name.toUpperCase());
                            iDesc.setText(description);
                            mBuilder.setView(mView);
                            AlertDialog dialog = mBuilder.create();
                            dialog.show();
                        }
                    });
                }
            }
        };
        list.setAdapter(f);

    }


}
