package mobiledev.unb.ca.lab4uielements;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * An activity representing a list of GeoData. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link GeoDataDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class GeoDataListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private DataModel dataModel;
    private List<GeoData> mGeoDataList;
    private int downloadTime = 4;      // Download time simulation
    private Button mBgButton;
    private final String TAG = "GeoDataListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_geodata_list);

        mBgButton = (Button) findViewById(R.id.button);

        // TODO Nothing to do here, just note that you will be completing the downloadGeoData()
        // function. It will set mGeoDataList to contain the downloaded geo data.
        downloadGeoData();

        // Test if we're on a tablet
        if(findViewById(R.id.geodata_detail_container) != null) {
            mTwoPane = true;
            // Create a new detail fragment if one does not exist
            GeoDataDetailFragment geoDataDetailFragment = (GeoDataDetailFragment) fragmentManager.findFragmentByTag("Detail");
            if (geoDataDetailFragment == null) {
                // Init new detail fragment
                geoDataDetailFragment = new GeoDataDetailFragment();
                Bundle args = new Bundle();
                //for (int i=0; i < mGeoDataList.size(); i++) {
                //    args.putString("course", mGeoDataList.get(i).title);
                //}
                geoDataDetailFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.geodata_detail_container, geoDataDetailFragment, "Detail").commit();
            }
        }

        mBgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Again, nothing to do here. Just note that the downloadGeoData() function
                // that you will be completing is also called here.
                downloadGeoData();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "I'm working!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.geodata_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    /**
     *  Setting up of RecyclerView with relevant data
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mGeoDataList));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<GeoData> mValues;

        public SimpleItemRecyclerViewAdapter(List<GeoData> data) {
            mValues = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.geodata_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mGeoData = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).title);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Setting the data to be sent to the Detail portion of the template.
                     * Here, we send the title, longitude, and latitude of the Earthquake
                     * that was clicked in the RecyclerView. The Detail Activity/Fragment
                     * will then display this information. Condition check is whether we
                     * are twoPane on a Tablet, which varies how we pass arguments to the
                     * participating activity/fragment.
                     */
                    String title = holder.mGeoData.title;
                    String lng = holder.mGeoData.longitude;
                    String lat = holder.mGeoData.latitude;
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(GeoDataDetailFragment.TITLE, title);
                        arguments.putString(GeoDataDetailFragment.LNG, lng);
                        arguments.putString(GeoDataDetailFragment.LAT, lat);
                        GeoDataDetailFragment fragment = new GeoDataDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.geodata_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, GeoDataDetailActivity.class);
                        intent.putExtra(GeoDataDetailFragment.TITLE, title);
                        intent.putExtra(GeoDataDetailFragment.LNG, lng);
                        intent.putExtra(GeoDataDetailFragment.LAT, lat);

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
            public GeoData mGeoData;

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

    private void downloadGeoData() {
        // TODO -done Check whether there is a network connection. If there is, Create a DownLoaderTask
        // and execute it. If there isn't, create a Toast indicating that there is no network
        // connection.
        // Hint: Read this for help on checking network connectivity:
        // http://developer.android.com/training/basics/network-ops/connecting.html
        // Hint: Read this for help with Toast:
        // http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        Log.i("downloadGeoData():","Starting DownloaderTask");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloaderTask dt = new DownloaderTask();
            dt.execute();
        } else {
            Context context = getApplicationContext();
            CharSequence text = "No network connection.";
            int duration = Toast.LENGTH_SHORT;
            Log.i("test:","got the toast");

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public class DownloaderTask extends AsyncTask<Void, Integer, String> {

        // done - TODO Get a reference to the progress bar so we can interact with it later

        private ProgressBar mProgressBar;

        @Override
        protected void onPreExecute() {
            /** Anything executed in here will be done on the UI thread before
             *  the doInBackground method is executed. This allows some prep work
             *  to be completed on the UI thread before the new thread activates.
             */

            // done - TODO Disable the button so it can't be clicked again once a download has been
            // started
            // Hint: Button is subclass of TextView. Read this document to see how to
            // disable it.
            // http://developer.android.com/reference/android/widget/TextView.html

            mBgButton.setClickable(false);
            mBgButton.getBackground().setAlpha(36);


            // done - TODO Set the progress bar's maximum to be downloadTime, its initial progress to be
            // 0, and also make sure it's visible.
            // Hint: Read the documentation on ProgressBar
            // http://developer.android.com/reference/android/widget/ProgressBar.html
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setMax(downloadTime);

        }
        @Override
        protected String doInBackground(Void... params) {

            // done-TODO Create an instance of DataModel and get the data from it. Store the data
            // in mGeoDataList
            dataModel = new DataModel();
            mGeoDataList = dataModel.getGeoData();


            // Leave this while loop here to simulate a lengthy download
            for(int i = 0; i < downloadTime; i++) {
                try {
                    Thread.sleep(1000);
                    // done-TODO update the progress bar; calculate an appropriate value for
                    // the new progress using i
                    publishProgress(i+1); //publish units of progress using onProgressUpdate()
                    Log.i("i=",""+i+1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i("i=","done");

            return "Download Via BG Thread Complete";
        }

        /** Once the DownloaderTask completes, hide the progress bar and update the
         *  RecyclerView with the geographic earthquake data we simulated downloading.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("onPostExecute(): ", "starting onPostExecute()");

            // TODO Now that the download is complete, enable the button again

            mBgButton.setClickable(true);

            // TODO Reset the progress bar, and make it disappear

            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.INVISIBLE);

            // TODO Setup the RecyclerView

            View recyclerView = findViewById(R.id.geodata_list);
            setupRecyclerView((RecyclerView) recyclerView);

            // TODO Create a Toast indicating that the download is complete. Set its text
            // to be the result String from doInBackground

            Context context = getApplicationContext();
            CharSequence text = result;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            mBgButton.getBackground().setAlpha(100);


        }

        /** Handle mProgressBar display updates whenever the AsyncTask subclass
         * DownloaderTask notifies its onProgressUpdate()
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("onProgressUpdate():",values[0]+"!!!!!!!");
            // done-TODO Update the progress bar using values
            mProgressBar.setProgress(values[0]);

        }
    }
}
