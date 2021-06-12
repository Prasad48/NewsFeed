package com.bhavaniprasad.newsfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bhavaniprasad.newsfeed.Adapter.NewsAdapter;
import com.bhavaniprasad.newsfeed.model.NewsFeedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextView author,url,title,content,description,publishedAt;
    ListView lv;
    NewsAdapter newsAdapter;
    ArrayList<NewsFeedData> arrayList;
    RecyclerView recyclerView;
    private ProgressBar progressBar;
    NetworkConnection networkConnection;
    private RelativeLayout layout_nonetwork;
    private Button btn_retry;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout_nonetwork = findViewById(R.id.no_network);
        progressBar = findViewById(R.id.progress_bar);
        author = (TextView)findViewById(R.id.author);
        url = (TextView)findViewById(R.id.url);
        title = (TextView)findViewById(R.id.title);
        btn_retry = findViewById(R.id.retry);
        description = (TextView)findViewById(R.id.description);
        content = (TextView)findViewById(R.id.content);
        publishedAt = (TextView)findViewById(R.id.publishedAt);
        recyclerView=findViewById(R.id.recyclerView);
        networkConnection = new NetworkConnection();
        arrayList=new ArrayList<>();

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"NEW TO OLD", "OLD TO NEW"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        handleNotificationData();
        progressBar.setVisibility(View.VISIBLE);
        getToken();
        if(!networkConnection.isConnected(this)){
            layout_nonetwork.setVisibility(View.VISIBLE);
        }
        else {
            layout_nonetwork.setVisibility(View.INVISIBLE);
        }

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!networkConnection.isConnected(MainActivity.this)){
                    progressBar.setVisibility(View.INVISIBLE);
                    layout_nonetwork.setVisibility(View.VISIBLE);
                }
                else{
                    layout_nonetwork.setVisibility(View.INVISIBLE);
                    getData();
                }
            }
        });

        getData();

    }

    /**
     * method to fetch the data from given URL "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"
     */
    private void getData() {
        String url = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)   //request for url by passing url
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful())
                {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject reader = new JSONObject(myResponse);
                                JSONArray jsonArray = reader.getJSONArray("articles"); // get the whole json array list
                                System.out.println("json size is : "+jsonArray.length());

                                for(int i = 0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String author = jsonObject.getString("author");
                                    String url = jsonObject.getString("url");
                                    String sourcename = jsonObject.getJSONObject("source").getString("name");
                                    String title = jsonObject.getString("title");
                                    String description = jsonObject.getString("description");
                                    String imageurl = jsonObject.getString("urlToImage");
                                    String publishedAt = jsonObject.getString("publishedAt");
                                    String content = jsonObject.getString("content");

                                    NewsFeedData data = new NewsFeedData();
                                    data.setAuthor(author);
                                    data.setUrl(url);
                                    data.setName(sourcename);
                                    data.setTitle(title);
                                    data.setDescription(description);
                                    data.setImageurl(imageurl);
                                    data.setPublishedAt(publishedAt);
                                    data.setContent(content);

                                    arrayList.add(data);

                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    newsAdapter = new NewsAdapter(MainActivity.this,arrayList);
                                    recyclerView.setAdapter(newsAdapter);
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * Called if InstanceID token is updated.
     * callback fires whenever a new token is generated
     */
    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                if (!task.isSuccessful()) {
                    Log.e("failed", "Failed to get the token.");
                    return;
                }

                //get the token from task
                String token = task.getResult();

                Log.d("token", "Token : " + token);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed", "Failed to get the token : " + e.getLocalizedMessage());
            }
        });
    }



    /**
     * method to handle the data content on clicking of notification if both notification and data payload are sent
     */
    private void handleNotificationData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String Key1=bundle.getString("Key1");
            String Key0=bundle.getString("Key0");
                if (Key0!=null) {
                    Log.d("Key1", "Data1 : " + bundle.getString("data1"));
                    new AlertDialog.Builder(this)
                            .setTitle("Closing application")
                            .setMessage("Got FCM data"+bundle.getString("Key0"))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setNegativeButton("No", null).show();
                }
                if (Key1!=null) {
                    Log.d("Key2", "Data2 : " + bundle.getString("data2"));
                    new AlertDialog.Builder(this)
                            .setTitle("Closing application")
                            .setMessage("Got FCM data"+bundle.getString("Key1"))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setNegativeButton("No", null).show();
                }
        }
    }

    /***
     * method to sort the recycler view list according to the PublishedAt date from the data
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        switch (position) {
            case 0:
                Collections.sort(arrayList, new Comparator<NewsFeedData>() {
                    @Override
                    public int compare(NewsFeedData lhs, NewsFeedData rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        try {
                            return sdf.parse(lhs.getPublishedAt()).getTime() > sdf.parse(rhs.getPublishedAt()).getTime() ? -1 : (sdf.parse(lhs.getPublishedAt()).getTime() < sdf.parse(rhs.getPublishedAt()).getTime() ) ? 1 : 0;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                if(newsAdapter!=null)
                newsAdapter.notifyDataSetChanged();

                break;
            case 1:
                Collections.sort(arrayList, new Comparator<NewsFeedData>() {
                    @Override
                    public int compare(NewsFeedData lhs, NewsFeedData rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        try {
                            return sdf.parse(lhs.getPublishedAt()).getTime() < sdf.parse(rhs.getPublishedAt()).getTime() ? -1 : (sdf.parse(lhs.getPublishedAt()).getTime() > sdf.parse(rhs.getPublishedAt()).getTime() ) ? 1 : 0;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                if(newsAdapter!=null)
                    newsAdapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * method to handle the data content on clicking of Push notification
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = null;
        if (intent != null && intent.getExtras() != null && intent.getExtras().size() > 0) {
            extras = intent.getExtras();
            Log.i(TAG, extras.getString("Key1"));
            String Key1=extras.getString("Key1");
            String Key0=extras.getString("Key0");
            if (Key0!=null) {
                Log.d("Key0", "Data0 : " + extras.getString("Key0"));
                new AlertDialog.Builder(this)
                        .setTitle("Closing application")
                        .setMessage("Got FCM data value"+extras.getString("Key0"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("No", null).show();
            }
            if (Key1!=null) {
                Log.d("Key1", "Data1 : " + extras.getString("Key1"));
                new AlertDialog.Builder(this)
                        .setTitle("Closing application")
                        .setMessage("Got FCM data value"+extras.getString("Key1"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("No", null).show();
            }
        }
    }

}