package ru.yandex.yamblz.artists;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class DefaultLoadingObserver implements DataLoadingModel.Observer {

    private ProgressDialog progressDialog;
    private Context context;
    private static final String TAG = "DefaultLoadingObserver";

    public DefaultLoadingObserver(Context context){
        this.context=context;
    }

    @Override
    public void onLoadingStart(DataLoadingModel loadingModel) {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Log.i(TAG, "start getting data");
    }

    @Override
    public void onLoadingSucceeded(DataLoadingModel loadingModel) {
        Log.i(TAG, "successfully get data");
        progressDialog.dismiss();
        dispose();

    }

    @Override
    public void onLoadingFailed(DataLoadingModel loadingModel) {
        progressDialog.dismiss();
        Log.i(TAG, "failed get data");
        dispose();
    }

    public void dispose(){
        if(progressDialog!=null)progressDialog.dismiss();
    }

}
