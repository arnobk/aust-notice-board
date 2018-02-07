package com.iamarnob.austnotice;

/**
 * Created by Arnob on 3/24/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView title;
        TextView date;
        TextView link;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        title = (TextView) itemView.findViewById(R.id.title);
        date = (TextView) itemView.findViewById(R.id.date);
        link = (TextView) itemView.findViewById(R.id.link);

        // Locate the ImageView in listview_item.xml

        // Capture position and set results to the TextViews
        title.setText(resultp.get(MainActivity.TITLE));
        date.setText(resultp.get(MainActivity.DATE));
        link.setText(resultp.get(MainActivity.LINK));
        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultp = data.get(position);

                    Intent intent = new Intent(context, NoticeDetailsActivity.class);
                    intent.putExtra("title", resultp.get(MainActivity.TITLE));
                    intent.putExtra("link", resultp.get(MainActivity.LINK));
                    // Pass all data flag
                    // Start SingleItemView Class
                    context.startActivity(intent);


            }
        });
        return itemView;
    }
}