package com.nibm.brewlab.Admin.Orders;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nibm.brewlab.R;

public class DoneOrdersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}