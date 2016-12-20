package com.example.j33lai.curio;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.j33lai.curio.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * An activity representing a list of CrowdCurios. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CrowdCurioDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CrowdCurioListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static boolean loadweb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowdcurio_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //View recyclerView = findViewById(R.id.crowdcurio_list);
        //assert recyclerView != null;
        //setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.crowdcurio_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        if (!loadweb) {
            try {
                new getProjects().execute(new URL("http://test.crowdcurio.com/api/project/"));
            } catch (MalformedURLException e) {
                Log.d("URL", "formatError");
            }
        } else {
            View recyclerView = findViewById(R.id.crowdcurio_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView, null);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, JSONObject jo) {
        if (!loadweb) {
            loadweb = true;
            DummyContent.initItem(jo);
            Log.d("repeat", "repeat");
        }
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.crowdcurio_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CrowdCurioDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        CrowdCurioDetailFragment fragment = new CrowdCurioDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.crowdcurio_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CrowdCurioDetailActivity.class);
                        intent.putExtra(CrowdCurioDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private class getProjects extends AsyncTask<URL, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            int count = urls.length;
            JSONObject jo = new JSONObject();
            if (count > 0) {
                URL url = urls[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null) {
                        responseStrBuilder.append(inputStr);
                    }
                    jo = new JSONObject(responseStrBuilder.toString());
                    JSONArray joa = jo.getJSONArray("data");


                } catch (IOException|JSONException e) {
                    Log.d("test", "catcherror");
                } finally {
                    urlConnection.disconnect();
                }

            }

            Log.d("test", "test");
            return jo;
        }

        protected void onPostExecute(JSONObject result) {
            String test = "";
            /*
            try {
                test = result.getJSONArray("data").getJSONObject(2).getString("type");
            } catch (JSONException e) {
                Log.d("result", "error");
            }*/

            View recyclerView = findViewById(R.id.crowdcurio_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView, result);
            Log.d("result", test);
        }
    }
}
