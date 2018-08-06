package com.example.vincenttieng.restaurant.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.vincenttieng.restaurant.Model.User;
import com.example.vincenttieng.restaurant.Remote.APIService;
import com.example.vincenttieng.restaurant.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        if (status.equals("0")){
            return "Placed";
        }
        if (status.equals("1")){
            return "On my way";
        }
        else
            return "Shipped";
    }

    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";



    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for (int i = 0; i<info.length ; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

                }
            }
        }
        return false;
    }

}
