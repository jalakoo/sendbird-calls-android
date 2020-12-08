package com.sendbird.calls.quickstart;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.calls.AudioDevice;
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
import com.sendbird.calls.quickstart.utils.ToastUtils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class SendbirdHelper {

    public static final String APP_ID = "";
    public DirectCall call;

    // TODO: Sendbird Step 1 - Init SDK

    public boolean init(Context context, String appId){
        if (appId == null){
            appId = "";
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
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
                        }

                        @Override
                        public void onEnded(DirectCall call) {
                            int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                            Log.i(BaseApplication.TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                        }
                    });
                }
            });

            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);
            return true;
        }
        return false;
    }

    // TODO: Sendbird Step 4 - Make calls

    public void startVideoCall(String calleeId, SendBirdVideoView remoteView, SendBirdVideoView localView){
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
        setCallListeners(call, remoteView, localView);
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
        setCallListeners(call, null, null);

    }

    public void setCallListeners(DirectCall call, SendBirdVideoView remoteView, SendBirdVideoView localView) {
        Log.i(BaseApplication.TAG, "[CallActivity] setListener()");

        if (call != null) {

            call.setRemoteVideoView(remoteView);

            call.setListener(new DirectCallListener() {
                @Override
                public void onConnected(DirectCall call) {
                    Log.i(BaseApplication.TAG, "[CallActivity] onConnected()");
                    call.setLocalVideoView(localView);
                }

                @Override
                public void onEnded(DirectCall call) {
                    Log.i(BaseApplication.TAG, "[CallActivity] onEnded()");
                }

                @Override
                public void onRemoteVideoSettingsChanged(DirectCall call) {
                    Log.i(BaseApplication.TAG, "[CallActivity] onRemoteVideoSettingsChanged()");
                }

                @Override
                public void onLocalVideoSettingsChanged(DirectCall call) {
                    Log.i(BaseApplication.TAG, "[CallActivity] onLocalVideoSettingsChanged()");
                }

                @Override
                public void onRemoteAudioSettingsChanged(DirectCall call) {
                    Log.i(BaseApplication.TAG, "[CallActivity] onRemoteAudioSettingsChanged()");
                }

                @Override
                public void onAudioDeviceChanged(DirectCall call, AudioDevice currentAudioDevice, Set<AudioDevice> availableAudioDevices) {
                    Log.i(BaseApplication.TAG, "[CallActivity] onAudioDeviceChanged(currentAudioDevice: " + currentAudioDevice + ", availableAudioDevices: " + availableAudioDevices + ")");
                }
            });
        }
    }

    // TODO: Sendbird Step 3 & 5 - Handling incoming calls
    public void setInboundListener(){

    }

    public void endCall(){
        call.end();
    }

    // PUSH UTILS

    public interface GetPushTokenHandler {
        void onResult(String token, SendBirdException e);
    }

    public static void getPushToken(Context context, final SendbirdHelper.GetPushTokenHandler handler) {
        Log.i(BaseApplication.TAG, "[PushUtils] getPushToken()");

        String savedToken = PrefUtils.getPushToken(context);
        if (TextUtils.isEmpty(savedToken)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.i(BaseApplication.TAG, "[PushUtils] getPushToken() => getInstanceId failed", task.getException());
                    if (handler != null) {
                        handler.onResult(null, new SendBirdException((task.getException() != null ? task.getException().getMessage() : "")));
                    }
                    return;
                }

                String pushToken = (task.getResult() != null ? task.getResult().getToken() : "");
                Log.i(BaseApplication.TAG, "[PushUtils] getPushToken() => pushToken: " + pushToken);
                if (handler != null) {
                    handler.onResult(pushToken, null);
                }
            });
        } else {
            Log.i(BaseApplication.TAG, "[PushUtils] savedToken: " + savedToken);
            if (handler != null) {
                handler.onResult(savedToken, null);
            }
        }
    }

    public interface PushTokenHandler {
        void onResult(SendBirdException e);
    }

    public static void registerPushToken(Context context, String pushToken, SendbirdHelper.PushTokenHandler handler) {
        Log.i(BaseApplication.TAG, "[PushUtils] registerPushToken(pushToken: " + pushToken + ")");

        SendBirdCall.registerPushToken(pushToken, false, e -> {
            if (e != null) {
                Log.i(BaseApplication.TAG, "[PushUtils] registerPushToken() => e: " + e.getMessage());
                PrefUtils.setPushToken(context, pushToken);

                if (handler != null) {
                    handler.onResult(e);
                }
                return;
            }

            Log.i(BaseApplication.TAG, "[PushUtils] registerPushToken() => OK");
            PrefUtils.setPushToken(context, pushToken);

            if (handler != null) {
                handler.onResult(null);
            }
        });
    }

    // AUTHENTICATION

    public interface AuthenticateHandler {
        void onResult(boolean isSuccess);
    }

    public static void authenticate(Context context, String userId, String accessToken, SendbirdHelper.AuthenticateHandler handler) {

        if (userId == null) {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => Failed (userId == null)");
            if (handler != null) {
                handler.onResult(false);
            }
            return;
        }

        deauthenticate(context, isSuccess -> {
            SendbirdHelper.getPushToken(context, (pushToken, e) -> {
                if (e != null) {
                    Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => Failed (e: " + e.getMessage() + ")");
                    if (handler != null) {
                        handler.onResult(false);
                    }
                    return;
                }

                Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate(userId: " + userId + ")");

                SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(accessToken), (user, e1) -> {
                    if (e1 != null) {
                        Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => Failed (e1: " + e1.getMessage() + ")");
                        showToastErrorMessage(context, e1);

                        if (handler != null) {
                            handler.onResult(false);
                        }
                        return;
                    }

                    Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => registerPushToken(pushToken: " + pushToken + ")");
                    SendBirdCall.registerPushToken(pushToken, false, e2 -> {
                        if (e2 != null) {
                            Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => registerPushToken() => Failed (e2: " + e2.getMessage() + ")");
                            showToastErrorMessage(context, e2);

                            if (handler != null) {
                                handler.onResult(false);
                            }
                            return;
                        }

                        PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
                        PrefUtils.setUserId(context, userId);
                        PrefUtils.setAccessToken(context, accessToken);
                        PrefUtils.setPushToken(context, pushToken);

                        Log.i(BaseApplication.TAG, "[AuthenticationUtils] authenticate() => OK");
                        if (handler != null) {
                            handler.onResult(true);
                        }
                    });
                });
            });
        });
    }

    public interface CompletionWithDetailHandler {
        void onCompletion(boolean isSuccess, boolean hasInvalidValue);
    }

    public static void authenticateWithEncodedAuthInfo(Activity activity, String encodedAuthInfo, SendbirdHelper.CompletionWithDetailHandler handler) {
        String appId = null;
        String userId = null;
        String accessToken = null;

        try {
            if (!TextUtils.isEmpty(encodedAuthInfo)) {
                String jsonString = new String(Base64.decode(encodedAuthInfo, Base64.DEFAULT), "UTF-8");
                JSONObject jsonObject = new JSONObject(jsonString);
                appId = jsonObject.getString("app_id");
                userId = jsonObject.getString("user_id");
                accessToken = jsonObject.getString("access_token");
            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(userId)
                && ((BaseApplication) activity.getApplication()).initSendBirdCall(appId)) {
            SendbirdHelper.authenticate(activity, userId, accessToken, isSuccess -> {
                if (handler != null) {
                    handler.onCompletion(isSuccess, false);
                }
            });
        } else {
            if (handler != null) {
                handler.onCompletion(false, true);
            }
        }
    }

    public interface DeauthenticateHandler {
        void onResult(boolean isSuccess);
    }

    public static void deauthenticate(Context context, SendbirdHelper.DeauthenticateHandler handler) {
        if (SendBirdCall.getCurrentUser() == null) {
            if (handler != null) {
                handler.onResult(false);
            }
            return;
        }

        Log.i(BaseApplication.TAG, "[AuthenticationUtils] deauthenticate(userId: " + SendBirdCall.getCurrentUser().getUserId() + ")");
        String pushToken = PrefUtils.getPushToken(context);
        if (!TextUtils.isEmpty(pushToken)) {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] deauthenticate() => unregisterPushToken(pushToken: " + pushToken + ")");
            SendBirdCall.unregisterPushToken(pushToken, e -> {
                if (e != null) {
                    Log.i(BaseApplication.TAG, "[AuthenticationUtils] unregisterPushToken() => Failed (e: " + e.getMessage() + ")");
                    showToastErrorMessage(context, e);
                }

                doDeauthenticate(context, handler);
            });
        } else {
            doDeauthenticate(context, handler);
        }
    }

    private static void doDeauthenticate(Context context, SendbirdHelper.DeauthenticateHandler handler) {
        SendBirdCall.deauthenticate(e -> {
            if (e != null) {
                Log.i(BaseApplication.TAG, "[AuthenticationUtils] deauthenticate() => Failed (e: " + e.getMessage() + ")");
                showToastErrorMessage(context, e);
            } else {
                Log.i(BaseApplication.TAG, "[AuthenticationUtils] deauthenticate() => OK");
            }

            PrefUtils.setUserId(context, null);
            PrefUtils.setAccessToken(context, null);
            PrefUtils.setCalleeId(context, null);
            PrefUtils.setPushToken(context, null);

            if (handler != null) {
                handler.onResult(e == null);
            }
        });
    }

    public interface AutoAuthenticateHandler {
        void onResult(String userId);
    }

    public static void autoAuthenticate(Context context, SendbirdHelper.AutoAuthenticateHandler handler) {
        Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate()");

        if (SendBirdCall.getCurrentUser() != null) {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate(userId: " + SendBirdCall.getCurrentUser().getUserId() + ") => OK (SendBirdCall.getCurrentUser() != null)");
            if (handler != null) {
                handler.onResult(SendBirdCall.getCurrentUser().getUserId());
            }
            return;
        }

        String userId = PrefUtils.getUserId(context);
        String accessToken = PrefUtils.getAccessToken(context);
        String pushToken = PrefUtils.getPushToken(context);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(pushToken)) {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate() => authenticate(userId: " + userId + ")");
            SendBirdCall.authenticate(new AuthenticateParams(userId).setAccessToken(accessToken), (user, e) -> {
                if (e != null) {
                    Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate() => authenticate() => Failed (e: " + e.getMessage() + ")");
                    showToastErrorMessage(context, e);

                    if (handler != null) {
                        handler.onResult(null);
                    }
                    return;
                }

                Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate() => registerPushToken(pushToken: " + pushToken + ")");
                SendBirdCall.registerPushToken(pushToken, false, e1 -> {
                    if (e1 != null) {
                        Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate() => registerPushToken() => Failed (e1: " + e1.getMessage() + ")");
                        showToastErrorMessage(context, e1);

                        if (handler != null) {
                            handler.onResult(null);
                        }
                        return;
                    }

                    Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate() => authenticate() => OK (Authenticated)");
                    if (handler != null) {
                        handler.onResult(userId);
                    }
                });
            });
        } else {
            Log.i(BaseApplication.TAG, "[AuthenticationUtils] autoAuthenticate() => Failed (No userId and pushToken)");
            if (handler != null) {
                handler.onResult(null);
            }
        }
    }

    private static void showToastErrorMessage(Context context, SendBirdException e) {
        if (context != null) {
            if (e.getCode() == 400111) {
                ToastUtils.showToast(context, context.getString(R.string.calls_invalid_notifications_setting_in_dashboard));
            } else {
                ToastUtils.showToast(context, e.getMessage());
            }
        }
    }
}
