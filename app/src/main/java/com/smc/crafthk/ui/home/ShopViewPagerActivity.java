package com.smc.crafthk.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityShopPagerBinding;
import com.smc.crafthk.databinding.ActivityShopViewPagerBinding;
import com.smc.crafthk.dto.UserMetadata;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.implementation.ShopPagerAdapter;
import com.smc.crafthk.implementation.ShopViewPagerAdapter;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShopViewPagerActivity extends AppCompatActivity {

    private ActivityShopViewPagerBinding binding;

    private Integer shopId;
    private FirebaseFirestore firestore;
    private static String FCM_KEY = "AAAAWqqN6Us:APA91bFaz-YFpWnVm0GDrlZJ5xHhlvKq16Yr5oPK2RsvE2qgV6gogk7ggoW6WMrxW5B7Dgt2XVmE1aVmQ2ABhl-NWSCm0i1hUl_mu0993FrugUaN9gtMuP9tpNh-dvgGeZbCBuJH_SWt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopViewPagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        shopId = getIntent().getIntExtra(Constraint.SHOP_ID_INTENT_EXTRA, -1);

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager = binding.viewPager;

        ShopDao shopDao = AppDatabase.getDatabase(this).shopDao();
        Shop shop = shopDao.getShopById(shopId);
        binding.textShopName.setText(shop.name);
        binding.textShopAddress.setText(Util.getAddress(this, new LatLng(shop.latitude, shop.longitude)));
        binding.textShopDesc.setText(shop.shopDescription);
        binding.textShopPhone.setText(shop.shopPhoneNumber);
        firestore = FirebaseFirestore.getInstance();
        Glide.with(this).load(shop.shopImagePath).circleCrop().into(binding.iamgeShopImage);

        binding.buttonMessage.setOnClickListener(v->{
            EditText input = new EditText(ShopViewPagerActivity.this);
            new AlertDialog.Builder(this)
                    .setTitle("Send Message")
                    .setMessage("Please enter a message below:")
                    .setView(input)
                    .setPositiveButton("Send", (dialog, i)->{
                        String value = input.getText().toString();
                        CollectionReference messagesRef = firestore.collection("user");
                        Query query = messagesRef.whereEqualTo("userId", shop.userId);
                        query.get().addOnSuccessListener((querySnapshot)->{
                            try {
                                if(querySnapshot.size() >= 1){
                                    UserMetadata userMetadata = querySnapshot.toObjects(UserMetadata.class).get(0);
                                    OkHttpClient client = new OkHttpClient();
                                    JSONObject data = new JSONObject();
                                    data.put("to", userMetadata.getDeviceToken());

                                    JSONObject notification = new JSONObject();
                                    notification.put("title", "New Message");
                                    notification.put("body", value);

                                    data.put("notification", notification);
                                    String payload = data.toString();

                                    RequestBody requestBody = RequestBody.create(payload, MediaType.parse("application/json"));

                                    Request request = new Request.Builder()
                                            .url("https://fcm.googleapis.com/fcm/send")
                                            .post(requestBody)
                                            .addHeader("Authorization", "key="+FCM_KEY)
                                            .addHeader("Content-Type", "application/json")
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                            System.out.println("****"+response.body().string());
                                        }
                                    });
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        });
                    })
                    .setNegativeButton("Cancel", (dialog, i)->{
                        dialog.cancel();
                    })
                    .show();
        });
        ShopViewPagerAdapter adapter = new ShopViewPagerAdapter(shopId, getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Product");
                    break;
                case 1:
                    tab.setText("Event");
                    break;
            }
        }).attach();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}