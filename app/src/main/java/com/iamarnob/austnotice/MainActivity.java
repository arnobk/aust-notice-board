package com.iamarnob.austnotice;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout mSwipeRefreshLayout;
    ListView listview;
    ListViewAdapter adapter;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> arraylist;
    static String TITLE = "title";
    static String DATE = "date";
    static String LINK = "link";
    static String FLAG = "flag";
    // URL Address
    String url = "http://aust.edu/news_events.htm";
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        FirebaseInstanceId.getInstance().getToken();
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if(CheckNetwork.isInternetAvailable(MainActivity.this)) //returns true if internet available
        {
            JsoupListView jsoupListView = new JsoupListView();
            jsoupListView.execute();
        } else {
            Toast toast = Toast.makeText(MainActivity.this, "Check your Internet Connection. Swipe to Refresh! ",Toast.LENGTH_LONG);
            toast.show();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onRefresh() {
        if(CheckNetwork.isInternetAvailable(MainActivity.this)) //returns true if internet available
        {

            new JsoupListView().execute();
            mSwipeRefreshLayout.setRefreshing(false);

        }
        else {
            Toast toast = Toast.makeText(MainActivity.this, "No Internet Connection",Toast.LENGTH_LONG);
            toast.show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    // Title AsyncTask
    private class JsoupListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("AUST News & Events");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            arraylist = new ArrayList<HashMap<String, String>>();

            try {
                // Connect to the Website URL
                Document doc = Jsoup.connect(url).get();
                // Identify Table Class "worldlink"
                for (Element table : doc.select("table[id=AutoNumber6]")) {
                    //Log.d("Table1",table.toString());
                    // Identify all the table row's(tr)
                    for (Element row : table.select("tr:gt(0)")) {

                        // Identify all the table cell's(td)
                        Elements tds = row.select("td");
                        //Log.d("tds",tds.toString());
                        // my work
                        for (Element table2 : tds.select("table")) {
                            //Log.d("Table2",table2.toString());
                            Elements row2 = table2.select("tr");
                                //Log.d("row2",row2.toString());
                                HashMap<String, String> map = new HashMap<String, String>();
                                Elements tds2 = row2.select("td");
                                Elements linktag = row2.select("a[href]");
                                String link = linktag.attr("href");
                                String linkfinal;
                            if(Objects.equals(link, "javascript:void(0)")){
                                Elements linktag2 = row2.select("a[onclick]");
                                String testlink = linktag2.attr("onclick");
                                String testlink1 = testlink.substring(0,testlink.length()-100);
                                String testlink2 = testlink1.substring(13,testlink1.length());

                                linkfinal = "http://aust.edu/" + testlink2;
                                //Log.d("onclick",linkfinal);
                            }
                            else{
                                linkfinal = "http://aust.edu/" + linktag.attr("href");
                            }
                                // Identify all img src's
                                Elements imgSrc = row.select("img[src]");
                                // Get only src from img src
                                String imgSrcStr = imgSrc.attr("src");

                                // Retrive Jsoup Elements
                                // Get the first td
                                String Date = "Posted on " + tds.get(3).text();
                                map.put("title", tds2.get(1).text());
                                // Get the second td
                                map.put("date", Date);
                                // Get the third td
                                map.put("link", linkfinal);
                                // Get the image src links
                                map.put("flag", imgSrcStr);
                                // Set all extracted Jsoup Elements into the array
                                arraylist.add(map);
                            }
                        }
                    }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(MainActivity.this, arraylist);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }

//////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_about) {

        //}


        return super.onOptionsItemSelected(item);
    }
}
