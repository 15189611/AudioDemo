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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.charles.audiodemo.R;
import com.charles.audiodemo.audio.AudioCapturerManager;
import com.charles.audiodemo.utils.Util;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.charles.audiodemo.R.id.tvRecordComplete;

/**
 * Created by Charles.
 */

public class RecordAudioDialog extends DialogFragment {

    @Bind(R.id.ivPlay)
    ImageView playIv;
    @Bind(R.id.tvRecordHint)
    TextView tvRecordHint;
    @Bind(R.id.tvAudioDur)
    TextView audioDurTv;
    @Bind(tvRecordComplete)
    TextView recordCompleteTv;
    @Bind(R.id.tvRecordPlay)
    TextView tvRecordPlay;//播放
    @Bind(R.id.tvRecordDurRemind)
    TextView recordDurRemindTv;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;//环形进度条

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
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = inflater.inflate(R.layout.dialog_record_audio, container, false);
        ButterKnife.bind(this, view);
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


    @OnClick({R.id.ivPlay, R.id.ivCancel,R.id.tvRecordComplete,R.id.tvRecordPlay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivPlay:   //开始
                boolean startAudioRecord = AudioCapturerManager.getInstance().startAudioRecord();
                if(startAudioRecord){
                    Toast.makeText(getActivity(), "开始录制", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tvRecordComplete:  //停止
                boolean stopAudioRecord = AudioCapturerManager.getInstance().stopAudioRecord();
                if(stopAudioRecord){
                    Toast.makeText(getActivity(), "停止录制", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tvRecordPlay: //播放
                boolean startAudioPlay = AudioCapturerManager.getInstance().startAudioPlay();
                if(startAudioPlay){
                    Toast.makeText(getActivity(), "播放成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivCancel:
                dismiss();
                break;
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
