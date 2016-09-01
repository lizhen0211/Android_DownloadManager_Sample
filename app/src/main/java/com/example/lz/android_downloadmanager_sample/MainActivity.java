package com.example.lz.android_downloadmanager_sample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button startDown;
    private Button pauseDown;
    private Button removeDownload;
    private Button displayDownload;

    private DownloadManager downManager;
    private DownloadManager.Request request;
    private long requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUIComponent();

        downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        request = buildRequest("http://down10.zol.com.cn/office/W.P.S.5457.12012.0.exe");
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initUIComponent() {
        startDown = (Button) findViewById(R.id.start_download);
        startDown.setOnClickListener(onClickListener);
        pauseDown = (Button) findViewById(R.id.pause_download);
        pauseDown.setOnClickListener(onClickListener);
        removeDownload = (Button) findViewById(R.id.remove_download);
        removeDownload.setOnClickListener(onClickListener);
        displayDownload = (Button) findViewById(R.id.display_download_content);
        displayDownload.setOnClickListener(onClickListener);
    }

    private DownloadManager.Request buildRequest(String uriStr) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uriStr));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("title");
        request.setDescription("description");
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "test.exe");
        return request;
    }

    private void queryDownLoadTask(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
//        query.setFilterByStatus()
        Cursor cursor = downManager.query(query);

        if (cursor.moveToNext()) {
            String size = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            String sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            //String id = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
            String lastModifiedTimestamp = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
            String localFilename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            String local_uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String mediaprovider_uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIAPROVIDER_URI));
            String media_type = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            String reason = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
            String status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String total_size = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
            Log.e("info:", "size:" + size + ";" + "sizeTotal:" + sizeTotal + ";" + "description:" + description + ";"
                    + "lastModifiedTimestamp:" + lastModifiedTimestamp + ";" + "localFilename:" + localFilename + ";"
                    + "local_uri:" + local_uri + ";" + "mediaprovider_uri:" + mediaprovider_uri + ";"
                    + "media_type:" + media_type + ";" + "reason:" + reason + ";" + "status:" + status + ";"
                    + "total_size:" + total_size + ";" + "uri:" + uri + ";"
            );
        }
        cursor.close();
    }

    public void showDownloadInFileDir() {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Toast.makeText(MainActivity.this, id + " 下载已经完成！", Toast.LENGTH_SHORT).show();
            } else if (action == DownloadManager.ACTION_NOTIFICATION_CLICKED) {
                long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                //点击通知栏取消下载
                downManager.remove(ids);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ids.length; i++) {
                    sb.append(ids[i] + ";");
                }
                Toast.makeText(MainActivity.this, "取消：" + sb.toString() + "任务", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.start_download:
                    requestID = downManager.enqueue(request);
                    break;
                case R.id.pause_download:
                    break;
                case R.id.remove_download:
                    downManager.remove(requestID);
                    break;
                case R.id.display_download_content:
                    queryDownLoadTask(requestID);
                    break;
                default:
            }
        }
    };
}
