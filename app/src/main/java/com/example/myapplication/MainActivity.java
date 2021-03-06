package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;

import androidx.annotation.NonNull;

import android.Manifest;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.widget.FrameLayout;
import android.opengl.GLSurfaceView;


public class MainActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {
    private static String API_KEY = "46815714";
    private static String SECRET = "fb5a3de751bfe05b8640c64f2c507780ec0971d2";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjgxNTcxNCZzaWc9ZmI2NWQ5NzQzNDljZ" +
            "WVlMzc2ODMyNmZmNGMyYzdlN2JjNTg2ODgxMDpzZXNzaW9uX2lkPTFfTVg0ME5qZ3hOVGN4Tkg1" +
            "LU1UVTVNek0xTnpRMk1URXlPWDV0TjBOcFREUlFTakJFTW1OcmVuVlJabTlYU210aFkwRi1mZyZ" +
            "jcmVhdGVfdGltZT0xNTkzMzU3NTI3Jm5vbmNlPTAuMjY5NzEzMzA1MTc3Njk0NTQmcm9sZT1wdW" +
            "JsaXNoZXImZXhwaXJlX3RpbWU9MTU5MzM3OTEyNyZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private static final String SESSION_ID = "1_MX40NjgxNTcxNH5-MTU5MzM1NzQ2MTEyOX5tN0NpT" +
            "DRQSjBEMmNrenVRZm9XSmthY0F-fg";

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermission() {
        String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            // initialize view objects from your layout
            mPublisherViewContainer = findViewById(R.id.publisher_container);
            mSubscriberViewContainer = findViewById(R.id.subscriber_container);
            // initialize and connect to the session
            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, permissions);
        }
    }

    //Session listener methods
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);
        mPublisherViewContainer.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream dropped");
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_TAG, "Session Error" + opentokError.getMessage());
    }

    //PublisherListener methods
    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.i(LOG_TAG, "Publisher error" + opentokError.getMessage());
    }
}

