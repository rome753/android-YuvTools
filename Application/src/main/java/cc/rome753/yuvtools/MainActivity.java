package cc.rome753.yuvtools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.camera1basic.Camera1Activity;
import com.example.android.camera2basic.Camera2Activity;
import com.example.android.camera2basic.R;
import com.example.android.camerax.CameraXActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View v) {
        startActivity(new Intent(this, Camera1Activity.class));
    }

    public void click2(View v) {
        startActivity(new Intent(this, Camera2Activity.class));
    }

    public void click3(View v) {
        startActivity(new Intent(this, CameraXActivity.class));
    }
}
