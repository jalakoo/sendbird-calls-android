package com.sendbird.calls.quickstart.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.sendbird.calls.handler.DialHandler;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;
import com.sendbird.calls.quickstart.BaseApplication;
import com.sendbird.calls.quickstart.R;
import com.sendbird.calls.quickstart.call.CallService;
import com.sendbird.calls.quickstart.call.VideoActivity;
import com.sendbird.calls.quickstart.call.VideoCallActivity;
import com.sendbird.calls.quickstart.utils.PrefUtils;
import com.sendbird.calls.quickstart.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

public class DialFragment extends Fragment {

    private static final String UNIQUE_HANDLER_ID = "abc123";
    private InputMethodManager mInputMethodManager;

    private TextInputEditText mTextInputEditTextUserId;
    private ImageView mImageViewVideoCall;
    private ImageView mImageViewVoiceCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() != null) {
            mInputMethodManager = (InputMethodManager) (getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        }

        return inflater.inflate(R.layout.fragment_dial, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTextInputEditTextUserId = view.findViewById(R.id.text_input_edit_text_user_id);
        mImageViewVideoCall = view.findViewById(R.id.image_view_video_call);
        mImageViewVoiceCall = view.findViewById(R.id.image_view_voice_call);

        mImageViewVideoCall.setEnabled(false);
        mImageViewVoiceCall.setEnabled(false);

        String savedCalleeId = PrefUtils.getCalleeId(getContext());
        if (!TextUtils.isEmpty(savedCalleeId)) {
            mTextInputEditTextUserId.setText(savedCalleeId);
            mTextInputEditTextUserId.setSelection(savedCalleeId.length());
            mImageViewVideoCall.setEnabled(true);
            mImageViewVoiceCall.setEnabled(true);
        }

        mTextInputEditTextUserId.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mTextInputEditTextUserId.clearFocus();
                if (mInputMethodManager != null) {
                    mInputMethodManager.hideSoftInputFromWindow(mTextInputEditTextUserId.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        mTextInputEditTextUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mImageViewVideoCall.setEnabled(editable != null && editable.length() > 0);
                mImageViewVoiceCall.setEnabled(editable != null && editable.length() > 0);
            }
        });

        mImageViewVideoCall.setOnClickListener(view1 -> {

            String calleeId = (mTextInputEditTextUserId.getText() != null ? mTextInputEditTextUserId.getText().toString() : "");
            if (!TextUtils.isEmpty(calleeId)) {
                PrefUtils.setCalleeId(getContext(), calleeId);
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("CALLEE_ID", calleeId);
                intent.putExtra("IS_VIDEO", true);
                intent.putExtra("INCOMMING", false);
                startActivity(intent);
            }
        });

        mImageViewVoiceCall.setOnClickListener(view1 -> {

            String calleeId = (mTextInputEditTextUserId.getText() != null ? mTextInputEditTextUserId.getText().toString() : "");
            if (!TextUtils.isEmpty(calleeId)) {
                CallService.dial(getContext(), calleeId, false);
                PrefUtils.setCalleeId(getContext(), calleeId);
                // TODO: Make actual call
            }
        });
    }
}
