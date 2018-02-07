package com.iamarnob.austnotice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NoticeDetailsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);
        Bundle bundle=getIntent().getExtras();
        setTitle(bundle.getString("title"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        // Not Available in Published Version :/
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);


        if(CheckNetwork.isInternetAvailable(NoticeDetailsActivity.this)) //returns true if internet available
        {
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(bundle.getString("link"));

        } else {
            webView.setVisibility(View.INVISIBLE);
            Toast toast = Toast.makeText(NoticeDetailsActivity.this, "No Internet Connection",Toast.LENGTH_LONG);
            toast.show();
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout2);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_external) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            Bundle bundle=getIntent().getExtras();
            intent.setData(Uri.parse(bundle.getString("link")));
            startActivity(intent);
        }
        else if (id == R.id.action_share) {

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            Bundle bundle=getIntent().getExtras();
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,bundle.getString("link"));
            intent.putExtra(Intent.EXTRA_SUBJECT,bundle.getString("title"));
            //intent.setData(Uri.parse(bundle.getString("link")));
            startActivity(Intent.createChooser(intent,"Share using"));
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Bundle bundle=getIntent().getExtras();
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        if(CheckNetwork.isInternetAvailable(NoticeDetailsActivity.this)) //returns true if internet available
        {
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(bundle.getString("link"));
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            Toast toast = Toast.makeText(NoticeDetailsActivity.this, "No Internet Connection",Toast.LENGTH_LONG);
            toast.show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
