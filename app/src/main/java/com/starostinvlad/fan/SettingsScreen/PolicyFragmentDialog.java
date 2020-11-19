package com.starostinvlad.fan.SettingsScreen;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.starostinvlad.fan.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PolicyFragmentDialog extends BottomSheetDialogFragment {

    private final String TAG = getClass().getSimpleName();

    static PolicyFragmentDialog newInstance() {
        Bundle args = new Bundle();
        PolicyFragmentDialog fragment = new PolicyFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pp, container, false);
        TextView textView = view.findViewById(R.id.policy_text);

        String policy = "";
        if (getActivity() != null && getArguments().containsKey("type")) {
            if (getArguments().getByte("type") == 1) {
                policy = getString(R.string.private_policy);
                Log.d(TAG, "onCreate: type 1");
            } else {
                policy = getString(R.string.content_policy);
                Log.d(TAG, "onCreate: type 2");
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(policy, Html.FROM_HTML_MODE_LEGACY));
        } else
            textView.setText(Html.fromHtml(policy));

        return view;
    }
}
