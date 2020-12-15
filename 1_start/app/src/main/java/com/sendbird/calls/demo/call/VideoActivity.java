package com.sendbird.calls.demo.call;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sendbird.calls.demo.R;
import com.sendbird.calls.demo.SendbirdHelper;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

public class VideoActivity extends AppCompatActivity {

    private View mViewConnectingVideoViewFullScreenFg;
    private RelativeLayout mRelativeLayoutVideoViewSmall;
    private ImageView mImageViewCameraSwitch;
    private ImageView mImageViewVideoOff;
    private ImageView mImageViewBluetooth;
    private ImageView mImageViewEnd;
    private ImageView mImageViewAudioOff;
    SendbirdHelper sendbirdHelper = new SendbirdHelper();

    // TODO: Add SendBirdVideoViews

    // TODO: Add DirectCall object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        setupViews();
        setupListeners();
        dial();
    }

    void setupViews(){
        mViewConnectingVideoViewFullScreenFg = findViewById(R.id.view_connecting_video_view_fullscreen_fg);
        mRelativeLayoutVideoViewSmall = findViewById(R.id.relative_layout_video_view_small);
        mImageViewCameraSwitch = findViewById(R.id.image_view_camera_switch);
        mImageViewVideoOff = findViewById(R.id.image_view_video_off);
        mImageViewBluetooth = findViewById(R.id.image_view_bluetooth);
        mImageViewEnd = findViewById(R.id.image_view_end);
        mImageViewAudioOff = findViewById(R.id.image_view_audio_off);

        // TODO: Link SendBirdVideoViews

    }

    void setupListeners() {

        sendbirdHelper.addInboundListener();

        // Optionally bind other UI features here

        // Toggle front/back camera
        mImageViewCameraSwitch.setOnClickListener(view -> {
            mImageViewCameraSwitch.setSelected(!mImageViewCameraSwitch.isSelected());

        });

        // Toggle camera on/off
        mImageViewVideoOff.setOnClickListener(view -> {
            mImageViewVideoOff.setSelected(!mImageViewVideoOff.isSelected());

        });

        // Switch bluetooth speaker options
        mImageViewBluetooth.setOnClickListener(view -> {
            mImageViewBluetooth.setSelected(!mImageViewBluetooth.isSelected());

        });

        // Toggle mute
        mImageViewAudioOff.setOnClickListener(view -> {
            mImageViewAudioOff.setSelected(!mImageViewAudioOff.isSelected());

        });

        // End call
        mImageViewEnd.setOnClickListener(view -> {
            hangup();
            finish();
        });
    }

    void dial(){
        String calleeId = getIntent().getStringExtra("CALLEE_ID");

            // TODO: Create the DirectCall object

            // TODO: Set the image streams to the appropriate views

            // TODO: Set call listener

            // TODO: Start the video stream


    }

    void hangup(){
        // TODO: Hangup

    }
}
