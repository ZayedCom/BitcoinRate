package com.app_nfusion.bitcoinrate;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    BaseAdapter baseAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    LayoutInflater layoutInflater;

    JSONArray dataSource = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        layoutInflater = LayoutInflater.from(MainActivity.this);

        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return dataSource.length();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.list_item, null);
                }

                TextView BTCTRY = convertView.findViewById(R.id.btcTRY);
                TextView askPrice = convertView.findViewById(R.id.askPrice);
                TextView bidPrice = convertView.findViewById(R.id.bidPrice);
                TextView dailyPercent = convertView.findViewById(R.id.dailyPercent);

                try {
                    JSONObject pair = dataSource.getJSONObject(position);
                    BTCTRY.setText(pair.getString("pair"));
                    askPrice.setText(pair.getString("ask"));
                    bidPrice.setText(pair.getString("bid"));
                    dailyPercent.setText(pair.getString("dailyPercent"));
                }catch (Exception e){
                    Log.e("x", "Item Parse Ex : " +e);
                }

                return convertView;
            }
        };

        listView.setAdapter(baseAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    void getData(){
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String jsonString = Jsoup.connect("https://www.btcturk.com/api/ticker").ignoreContentType(true).timeout(10000).get().text();
                    Log.e("x", "JSON Stirng : ");
                    Log.e("x" , jsonString);

                    dataSource = new JSONArray(jsonString);
                    Log.e("x", "Item Count : " +dataSource.length());

                    return "ok";
                }catch (Exception e){
                    Log.e("x", "WS Parse Ex : " +e);

                    return e.toString();
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                swipeRefreshLayout.setRefreshing(false);
                baseAdapter.notifyDataSetChanged();

                if (!o.equals("ok")){
                    Toast.makeText(MainActivity.this, (Integer) o, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
