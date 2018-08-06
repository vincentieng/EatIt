package com.example.vincenttieng.restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.example.vincenttieng.restaurant.Database.Database;
import com.example.vincenttieng.restaurant.Interface.ItemClickListener;
import com.example.vincenttieng.restaurant.Model.Category;
import com.example.vincenttieng.restaurant.Model.Food;
import com.example.vincenttieng.restaurant.ViewHolder.FoodViewHolder;
import com.example.vincenttieng.restaurant.ViewHolder.MenuViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import static com.example.vincenttieng.restaurant.R.drawable.ic_favorite_black_24dp;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    Database localDB;
    String categoryId = "";

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();

    Target  target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo= new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        localDB = new Database(this);

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
                    protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                        viewHolder.food_name.setText(model.getName());
                        Picasso.with(getBaseContext()).load(model.getImage())
                                .into(viewHolder.food_image);

                        if(localDB.isFavorite(adapter.getRef(position).getKey()))
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                        viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Picasso.with(getApplicationContext())
                                        .load(model.getImage())
                                        .into(target);

                            }
                        });
                        viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!localDB.isFavorite(adapter.getRef(position).getKey()))
                                {
                                    localDB.addToFavorites(adapter.getRef(position).getKey());
                                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                                    Toast.makeText(FoodList.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    localDB.removeFromFavorites(adapter.getRef(position).getKey());
                                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                    Toast.makeText(FoodList.this, ""+model.getName()+" was removed from Favorites", Toast.LENGTH_SHORT)
                                            .show();

                                }
                            }
                        });

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



