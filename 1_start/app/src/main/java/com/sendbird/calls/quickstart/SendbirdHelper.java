package com.sendbird.calls.quickstart;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sendbird.calls.quickstart.utils.PrefUtils;

import java.util.UUID;

public class SendbirdHelper {

    // Optionally add your SendBird app id here
    public static final String APP_ID = "";
    public interface CallEnded {
        void onResult();
    }
    CallEnded callEnded;

    // TODO: Add an instance of DirectCall for ending calls from UI


    // TODO Implementation steps follows guide from https://sendbird.com/docs/calls/v1/android/getting-started/make-first-call

    // TODO: Sendbird Step 1 - Init SDK

    public boolean init(Context context, String appId){
        if (appId == null){
            appId = "";
        }

        // TODO: Replace & Add init code here

        return true;
    }


    // TODO: Sendbird Step 2 - Authenticate User

    public interface AuthenticateHandler {
        void onResult(boolean isSuccess);
    }

    public static void authenticate(Context context, String userId, String accessToken, AuthenticateHandler handler) {

        // Check for user id
        if (userId == null) {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => Failed (userId == null)");
            if (handler != null) {
                handler.onResult(false);
            }
            return;
        }

        // TODO: Replace and Add authentication code here
        handler.onResult(true);

    }

    // TODO: Sendbird Step 3 & 5 - Handling incoming calls

    public void addInboundListener(){

        // TODO: Add SendBirdCall listener here

    }

    // TODO: Sendbird Step 4 - Make calls

//    public void startVideoCall(String calleeId, SendBirdVideoView remoteView, SendBirdVideoView localView){
//        // TODO: Set params for DirectCall
//
//        // TODO: Set the image streams to the appropriate views
//
//
//        // TODO: Add listeners to DirectCall object
//
//
//        // TODO: Start the video stream
//
//    }

    public void startVoiceCall(String calleeId){
        // TODO: Create DialParams

        // TODO: Create DirectCall & dial

        // TODO: Add listeners to DirectCall object

    }

    public void endCall(){
        if (callEnded != null){
            callEnded.onResult();
        }

        // TODO: Stop this instance's DirectCall object

    }
}
