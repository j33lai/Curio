package com.example.j33lai.curio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

/**
 * A fragment representing a single CrowdCurio detail screen.
 * This fragment is either contained in a {@link CrowdCurioListActivity}
 * in two-pane mode (on tablets) or a {@link CrowdCurioDetailActivity}
 * on handsets.
 */
public class CrowdCurioDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    private ImageView mImageView;
    private TextView mAboutView, mQeustionView, mTeamView;
    private boolean imageload = false;

    private ProgressBar pbar;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CrowdCurioDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.crowdcurio_detail, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.crowdcurio_detail_head);
        mAboutView = (TextView) rootView.findViewById(R.id.crowdcurio_detail_about);
        mQeustionView = (TextView) rootView.findViewById(R.id.crowdcurio_detail_question);
        mTeamView = (TextView) rootView.findViewById(R.id.crowdcurio_detail_team);
        pbar = (ProgressBar) rootView.findViewById(R.id.app_progress1);

        Log.d("image", "image");
        if (!imageload) {
            imageload = true;
            pbar.setVisibility(View.VISIBLE);
            Log.d("image1", "image1");
            new getDetails().execute(mItem.content);
        }
        // Show the dummy content as text in a TextView.

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.crowdcurio_detail_about)).setText(mItem.details);
        }

        return rootView;
    }

    private class ProjectDetails {
        Bitmap bmp;
        String about;
        String question;
        String team;
        ProjectDetails(Bitmap bmp1, String s0, String s1, String s2) {
            bmp = bmp1;
            about = s0;
            question = s1;
            team = s2;
        }
    }

    private JSONObject getJSONObj(URL url) {
        JSONObject result = new JSONObject();
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
            result = new JSONObject(responseStrBuilder.toString());
        } catch (IOException|JSONException e) {
            Log.d("getJson", "failed");
        }
        return result;
    }

    private class getDetails extends AsyncTask<String, Void, ProjectDetails> {
        @Override
        protected ProjectDetails doInBackground(String... names) {
            Bitmap bmp = null;
            String s0, s1, s2;
            s0 = "";
            s1 = "Questions\n";
            s2 = "";
            String name = names[0];
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://crowdcurio.com/api/project/");

                JSONObject jo = getJSONObj(url);
                JSONArray joa = jo.getJSONArray("data");
                JSONObject joi = joa.getJSONObject(Integer.parseInt(mItem.id)-1);
                JSONObject joia = joi.getJSONObject("attributes");


                URL urli = new URL(joia.getString("avatar"));
                bmp = BitmapFactory.decodeStream(urli.openConnection().getInputStream());
                s0 = joia.getString("description");

                JSONArray joc = getJSONObj(new URL("http://crowdcurio.com/api/curio/")).getJSONArray("data");

                for (int i = 0; i < joc.length(); i++) {
                    JSONObject tmp_jo = joc.getJSONObject(i);
                    if (mItem.id.equals(tmp_jo.getJSONObject("relationships").getJSONObject("project").getJSONObject("data").getString("id"))) {
                        s1 += tmp_jo.getJSONObject("attributes").getString("question") + "\n";
                    }

                }



                s2 = "";



            } catch (IOException|JSONException e) {
                Log.d("image", "Error");
            }
            return new ProjectDetails(bmp, s0, s1, s2);
        }

        protected void onPostExecute(ProjectDetails result) {
            mImageView.setImageBitmap(result.bmp);
            mAboutView.setText(result.about);
            mQeustionView.setText(result.question);
            mTeamView.setText(result.team);
            pbar.setVisibility(View.GONE);

        }

    }
}
