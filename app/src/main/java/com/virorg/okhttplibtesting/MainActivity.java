package com.virorg.okhttplibtesting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.virorg.webcallerlib.ApiCallBack;
import com.virorg.webcallerlib.ContentValue;
import com.virorg.webcallerlib.HttpWebCall;
import com.virorg.webcallerlib.OkHttpRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 2;
    private String filePath;
    private Uri mImageUri;
    public final static int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4;
    final HttpWebCall httpWebCall = HttpWebCall.getInstance(this);
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_btn:
                sampleGetCall();
                break;
            case R.id.post_btn:
                samplePostCall();
                break;
            case R.id.multipart_btn:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    openGallery();
                }

                break;
            case R.id.cancel_btn:
                cancelCall();
                break;
            case R.id.retry_btn:
                reTryCall();
                break;
        }
    }

    private void cancelCall() {
        //httpWebCall.cancelCalls("get_call");
        httpWebCall.cancelAllCall();
    }

    private void reTryCall() {
        try {
            httpWebCall.doNetworkCallAsynchronous(httpWebCall.getRequestBundle("get_call"));
        } catch (Exception e) {
            Toast.makeText(this, "Request stack is empty.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void showLoader() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Loading....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
    }


    /*hide loader */
    public void hideLoader() {
        progressBar.dismiss();
    }

    private void sampleGetCall() {
        showLoader();
        List<ContentValue> list = new ArrayList<ContentValue>();
        list.add(new ContentValue("ABC", "XYZ"));
        list.add(new ContentValue("DEF", "UVW"));
        final OkHttpRequest.Builder builder = new OkHttpRequest.Builder<Object>()
                .setUrl("http://publicobject.com/helloworld.txt")
//                .setAuthorization("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImJAeWFob28uY29tIiwidXNlcklkIjoiNTczZWE4NDg1ZTYwMGNlMjc5Y2JlNjFlIiwiaWF0IjoxNDgyMjI3NzMxfQ.4MOx9iUBQtaKSXO7p2aUCCTKCS0zcBkWVyPx3kYcf98")
                .setTag("get_call")
//                .setRequestParams(list)
                .setHeader(list)
                .setResponseClass(Object.class)
                .setCallBack(new ApiCallBack<Object>(this, getProgressDiaLog(), false) {
                    @Override
                    public void onSuccess(Object modeledResponse, final String actualResponse) {
                        Log.i("------>", "actualResponse : " + actualResponse);
//                        Log.i("------>", "modeledResponse : " + modeledResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(actualResponse);
                                hideLoader();
                            }
                        });

                    }

                    @Override
                    public void onFail(final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(e.getMessage());
                                hideLoader();
                            }
                        });
                    }
                });
        OkHttpRequest okHttpRequest = builder.build();
        httpWebCall.getRequest(true, okHttpRequest);
    }

    private void samplePostCall() {
        showLoader();
        List<ContentValue> list = new ArrayList<ContentValue>();
        ContentValue req2 = new ContentValue();
        req2.setKey("search");
        req2.setValue("Jurassic Park dgsdgfsd");
        list.add(req2);
        //HttpWebCall httpWebCall = HttpWebCall.getInstance(this);
        final OkHttpRequest.Builder builder = new OkHttpRequest.Builder<Object>()
                .setUrl("https://en.wikipedia.org/w/index.php")
//                .setAuthorization("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImJAeWFob28uY29tIiwidXNlcklkIjoiNTczZWE4NDg1ZTYwMGNlMjc5Y2JlNjFlIiwiaWF0IjoxNDgyMjI3NzMxfQ.4MOx9iUBQtaKSXO7p2aUCCTKCS0zcBkWVyPx3kYcf98")
                .setTag("post_call")
                .setRequestParams(list)
                .setResponseClass(Object.class)
                .setCallBack(new ApiCallBack<Object>(this, getProgressDiaLog(), true) {
                    @Override
                    public void onSuccess(Object modeledResponse, final String actualResponse) {
                        Log.i("------>", "actualResponse : " + actualResponse);
//                        Log.i("------>", "modeledResponse : " + modeledResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(actualResponse);
                                hideLoader();
                            }
                        });

                    }

                    @Override
                    public void onFail(final Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(e.getMessage());
                                hideLoader();
                            }
                        });
                    }
                });
        OkHttpRequest okHttpRequest = builder.build();
        httpWebCall.postRequest(true, okHttpRequest);
    }

    private void sampleMultipartCall() {
        showLoader();
        List<ContentValue> mediaList = new ArrayList<ContentValue>();
        mediaList.add(new ContentValue("media", filePath));

        List<ContentValue> headerList = new ArrayList<ContentValue>();
        headerList.add(new ContentValue("Authorization", "Client-ID ..."));
        //HttpWebCall httpWebCall = HttpWebCall.getInstance(this);
        final OkHttpRequest.Builder builder = new OkHttpRequest.Builder<Object>()
                .setUrl("https://api.imgur.com/3/image")
                .setHeader(headerList)
                .setTag("multipart_call")
//                .setRequestParams(list)
                .setMedia(mediaList)
                .setResponseClass(Object.class)
                .setCallBack(new ApiCallBack<Object>(this, getProgressDiaLog(), true) {
                    @Override
                    public void onSuccess(Object modeledResponse, final String actualResponse) {
                        Log.i("------>", "actualResponse : " + actualResponse);
//                        Log.i("------>", "modeledResponse : " + modeledResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(actualResponse);
                                hideLoader();
                            }
                        });

                    }

                    @Override
                    public void onFail(final Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(e.getMessage());
                                hideLoader();
                            }
                        });
                    }
                });
        OkHttpRequest okHttpRequest = builder.build();
        httpWebCall.multipartRequest(true, okHttpRequest);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), GALLERY_REQUEST);
    }


    public ProgressDialog getProgressDiaLog() {
        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Loading....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressBar;
    }

    public void showDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("response");
        builder.setMessage(content);
        builder.setCancelable(true);
        builder.setNegativeButton("ok", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                    filePath = FileUtils.getPath(this, mImageUri);
                    sampleMultipartCall();

                }
                break;
        }
    }
}
