package com.sendbird.calls.demo.call;

import android.os.Bundle;
import android.widget.ImageView;

import com.sendbird.calls.demo.R;
import com.sendbird.calls.demo.SendbirdHelper;

import androidx.appcompat.app.AppCompatActivity;

public class VoiceActivity extends AppCompatActivity {

    ImageView mImageViewAudioOff;
    ImageView mImageViewBluetooth;
    ImageView mImageViewEnd;
    ImageView mImageViewSpeakerphone;
    SendbirdHelper sendbirdHelper = new SendbirdHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);
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

        // Optionally bind UI Features here
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
