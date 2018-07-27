package com.example.vincenttieng.restaurant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vincenttieng.restaurant.Common.Common;
import com.example.vincenttieng.restaurant.Interface.ItemClickListener;
import com.example.vincenttieng.restaurant.Model.Category;
import com.example.vincenttieng.restaurant.Model.Food;
import com.example.vincenttieng.restaurant.ViewHolder.FoodViewHolder;
import com.example.vincenttieng.restaurant.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!categoryId.isEmpty() && categoryId != null) {
            if(Common.isConnectedToInternet(this))
                loadListFood(categoryId);
            else
            {
                Toast.makeText(FoodList.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void loadListFood(final String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>
                (Food.class,
                        R.layout.food_item,
                        FoodViewHolder.class,
                        foodList.orderByChild("menuId").equalTo(categoryId)) {
                    @Override
                    protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                        viewHolder.food_name.setText(model.getName());
                        Picasso.with(getBaseContext()).load(model.getImage())
                                .into(viewHolder.food_image);
                        final Food local = model;

                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent foodList = new Intent(FoodList.this,FoodDetail.class);
                                //Because CategoryId is key, so we just get key of this item
                                foodList.putExtra("FoodId",adapter.getRef(position).getKey());
                                startActivity(foodList);

                            }
                        });

                    }
                };
        recyclerView.setAdapter(adapter);

    }

    /*private void loadListFood(String categoryId) {
        Query query = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("category");

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(foodList, Food.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item,
                FoodViewHolder.class,foodList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void onBindViewHolder(FoodViewHolder holder, int position, Food model) {
                holder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.food_image);
                final Food clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();
                        //Get CategoryId and send to new Activity
                        Intent foodList = new Intent(FoodList.this,FoodDetail.class);
                        //Because CategoryId is key, so we just get key of this item
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new FoodViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }*/

        };



