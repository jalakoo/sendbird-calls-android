package com.sendbird.calls.quickstart;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.sendbird.calls.SendBirdVideoView;
import com.sendbird.calls.handler.DialHandler;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.sendbird.calls.quickstart.utils.PrefUtils;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SendbirdHelper {

    public static final String APP_ID = "";
    public DirectCall call;

    public interface CallEnded {
        void onResult();
    }
    CallEnded callEnded;

    // TODO Implementation steps follows guide from https://sendbird.com/docs/calls/v1/android/getting-started/make-first-call

    // TODO: Sendbird Step 1 - Init SDK

    public boolean init(Context context, String appId){
        if (appId == null){
            appId = "";
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();

            addInboundListener();

            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);

            return true;
        }
        return false;
    }


    // TODO: Sendbird Step 2 - Authenticate User

    public interface AuthenticateHandler {
        void onResult(boolean isSuccess);
    }

    public static void authenticate(Context context, String userId, String accessToken, AuthenticateHandler handler) {

        // Make sure required userId available
        if (userId == null) {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => Failed (userId == null)");
            if (handler != null) {
                handler.onResult(false);
            }
            return;
        }

        // Make authentication call to Sendbird
        SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(accessToken), (user, e1) -> {
            if (e1 != null) {
                Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => Failed (e1: " + e1.getMessage() + ")");
                if (e1.getCode() == 400111) {
                    Toast.makeText(context, context.getString(R.string.calls_invalid_notifications_setting_in_dashboard), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (handler != null) {
                    handler.onResult(false);
                }
                return;
            }

            PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
            PrefUtils.setUserId(context, userId);
            PrefUtils.setAccessToken(context, accessToken);
            handler.onResult(true);
        });

    }

    // TODO: Sendbird Step 3 & 5 - Handling incoming calls

    public void addInboundListener(){
        SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
            @Override
            public void onRinging(DirectCall call) {
                int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                Log.i(BaseApplication.TAG, "[BaseApplication] onRinging() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                if (ongoingCallCount >= 2) {
                    call.end();
                    return;
                }

                call.setListener(new DirectCallListener() {
                    @Override
                    public void onConnected(DirectCall call) {
                        Log.i(BaseApplication.TAG, "[BaseApplication] onConnected() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);
                        call.accept(new AcceptParams());
                    }

                    @Override
                    public void onEnded(DirectCall call) {
                        int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                        Log.i(BaseApplication.TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);
                        endCall();
                    }
                });

                call.accept(new AcceptParams());
            }
        });
    }

    // TODO: Sendbird Step 4 - Make calls

    public void startVideoCall(String calleeId, SendBirdVideoView remoteView, SendBirdVideoView localView){
        // Set params and dial
        DialParams params = new DialParams(calleeId);
        params.setVideoCall(true);
        params.setCallOptions(new CallOptions());
        call = SendBirdCall.dial(params, new DialHandler(){

            @Override
            public void onResult(@Nullable DirectCall directCall, @Nullable SendBirdException e) {
                if (e != null){
                    Log.e(BaseApplication.TAG, e.toString());
                }
            }
        });

        // Set the image streams to the appropriate views
        call.setRemoteVideoView(remoteView);
        call.setLocalVideoView(localView);

        // Add DirectCall listeners
        call.setListener(new DirectCallListener() {
            @Override
            public void onConnected(DirectCall call) {
                Log.i(BaseApplication.TAG, "[CallActivity] onConnected()");
            }

            @Override
            public void onEnded(DirectCall call) {
                Log.i(BaseApplication.TAG, "[CallActivity] onEnded()");
                endCall();
            }

        });

        // Start the video stream
        call.startVideo();
    }

    public void startVoiceCall(String calleeId){
        DialParams params = new DialParams(calleeId);
        params.setVideoCall(false);
        params.setCallOptions(new CallOptions());
        DirectCall call = SendBirdCall.dial(params, new DialHandler(){

            @Override
            public void onResult(@Nullable DirectCall directCall, @Nullable SendBirdException e) {
                if (e != null){
                    Log.e(BaseApplication.TAG, e.toString());
                }
            }
        });
        call.setListener(new DirectCallListener() {
            @Override
            public void onConnected(DirectCall call) {
                Log.i(BaseApplication.TAG, "[CallActivity] onConnected()");
            }

            @Override
            public void onEnded(DirectCall call) {
                Log.i(BaseApplication.TAG, "[CallActivity] onEnded()");
                endCall();
            }

        });

    }

    public void endCall(){
        if (callEnded != null){
            callEnded.onResult();
        }
        if (call == null){
            return;
        }
        call.stopVideo();
        call.end();
    }
}
