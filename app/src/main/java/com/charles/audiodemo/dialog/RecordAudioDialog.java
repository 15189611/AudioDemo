package com.charles.audiodemo.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.charles.audiodemo.R;
import com.charles.audiodemo.utils.Util;

/**
 * Created by Charles.
 */

public class RecordAudioDialog extends DialogFragment {

    public static RecordAudioDialog newInstance() {
        RecordAudioDialog dialog = new RecordAudioDialog();
        Bundle data = new Bundle();
        dialog.setArguments(data);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.dialog_record_audio, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.getAttributes().width = (int) (Util.getWidth(getActivity()) * 0.85);
        }
    }


    public void show(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!isAdded()){
            transaction.add(this, this.getClass().getSimpleName());
        }
        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void dismiss() {
        if (getActivity() == null || getActivity().isFinishing()){
            return;
        }

        try {
            dismissAllowingStateLoss();
        } catch (Exception ignored) {
        }
    }

}
