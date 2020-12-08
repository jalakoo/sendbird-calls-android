package com.sendbird.calls.quickstart.call;

import android.os.Bundle;
import android.widget.ImageView;

import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.quickstart.R;
import com.sendbird.calls.quickstart.SendbirdHelper;

import org.jetbrains.annotations.Nullable;

import androidx.appcompat.app.AppCompatActivity;

public class VoiceActivity extends AppCompatActivity {

    ImageView mImageViewAudioOff;
    ImageView mImageViewBluetooth;
    ImageView mImageViewEnd;
    ImageView mImageViewSpeakerphone;
    SendbirdHelper sendbirdHelper = new SendbirdHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        setupViews();
        setupListeners();
        dial();
    }

    void setupViews(){
        mImageViewBluetooth = findViewById(R.id.image_view_bluetooth);
        mImageViewEnd = findViewById(R.id.image_view_end);
        mImageViewAudioOff = findViewById(R.id.image_view_audio_off);
        mImageViewSpeakerphone = findViewById(R.id.image_view_speakerphone);
    }

    void setupListeners(){

        // TODO: Bind UI Features here
        mImageViewSpeakerphone.setOnClickListener(view -> {
            mImageViewSpeakerphone.setSelected(!mImageViewSpeakerphone.isSelected());

        });

        mImageViewBluetooth.setEnabled(false);
        mImageViewBluetooth.setOnClickListener(view -> {
            mImageViewBluetooth.setSelected(!mImageViewBluetooth.isSelected());
        });

        mImageViewEnd.setOnClickListener(view -> {
            hangup();
            finish();
        });
    }

    void dial(){
        String calleeId = getIntent().getStringExtra("CALLEE_ID");
        sendbirdHelper.startVoiceCall(calleeId);
    }

    void hangup(){
        sendbirdHelper.endCall();
    }
}
