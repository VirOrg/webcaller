package com.virorg.webcallerlib;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by Ashish on 18/12/16.
 */

public abstract class ApiCallBack<T> {

    private ProgressDialog progressDialog;
    private Activity mContext;
    private String progressText;
    private ProgressListener progressListener;
    private boolean isShowProgress = true;


    public interface ProgressListener {
        void progressStart();

        void progressEnd();
    }

    public ApiCallBack(Activity activity, ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        this.mContext = activity;

    }

    public ApiCallBack(Activity activity, ProgressDialog progressDialog, boolean isShowProgress) {
        this.progressDialog = progressDialog;
        this.isShowProgress = isShowProgress;
        this.mContext = activity;
    }

    protected void setShowProgress(boolean showProgress) {
        this.isShowProgress = showProgress;
    }

    protected void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public abstract void onSuccess(T modeledResponse, String actualResponse);

    public abstract void onFail(Exception e);

    public void showProgress() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    progressListener.progressStart();
                }
                if (isShowProgress) {
                    if (progressDialog != null) {
                        progressDialog.show();
                    }
                }
            }
        });

    }

    public void hideProgress() {

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    progressListener.progressEnd();
                }
                if (isShowProgress) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }

            }
        });

    }

}