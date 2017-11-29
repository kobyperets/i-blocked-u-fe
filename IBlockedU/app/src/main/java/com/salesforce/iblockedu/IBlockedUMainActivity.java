package com.salesforce.iblockedu;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import cz.msebera.android.httpclient.Header;

public class IBlockedUMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IBlockedUFragment.OnFragmentInteractionListener {

    public static final String PREFS_NAME = "IBlockedUPrefs";
    private IBlockedUFragment iBlockedUFragment;
    private IBlockUWHOFragment iBlockUWHOFragment;
    private TextView nameLabel;
    private TextView emailLabel;
    private EditText signinEmailEditText;
    private Button signinInBtn;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iblocked_umain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userEmail = settings.getString("email", "");
        String name = settings.getString("name", "");
        View signinView = navigationView.getHeaderView(0);
        nameLabel = ((TextView)signinView.findViewById(R.id.name_label));
        emailLabel = (TextView) signinView.findViewById(R.id.email_label);
        signinEmailEditText = (EditText)signinView.findViewById(R.id.email_signin_edittext);
        signinInBtn = (Button)signinView.findViewById(R.id.signin_btn);
        if(!userEmail.isEmpty()) {
            enableSignedInUser(userEmail, name);
        }
    }

    private void enableSignedInUser(String userEmail, String name) {
        nameLabel.setText(name);
        emailLabel.setText(userEmail);

        signinInBtn.setVisibility(View.INVISIBLE);
        signinEmailEditText.setVisibility(View.INVISIBLE);

        navigationView.getMenu().setGroupEnabled(R.id.side_menu, true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.iblocked_umain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_iblockedu_camera) {
            iBlockedUFragment = new IBlockedUFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, iBlockedUFragment).commit();
        } else if (id == R.id.nav_gallery) {
            iBlockUWHOFragment = new IBlockUWHOFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, iBlockUWHOFragment).commit();
        } else if (id == R.id.nav_slideshow) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, new IBlockUGoingHomeFragment()).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void signIn(View view) {
        final String emailAddress = signinEmailEditText.getText().toString();
        if(emailAddress.isEmpty()) {
            Toast.makeText(this, "Please provide valid email address", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUtils.getRequestUrl(
                HttpUtils.SIGN_IN_ENDPOINT, emailAddress, ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        enableSignedInUser(emailAddress, response);

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("email", emailAddress);
                        editor.putString("name", response);
                        editor.apply();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Please try signing-in again", Toast.LENGTH_LONG).show();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);


//
//        RequestParams rp = new RequestParams();
//        rp.add(HttpUtils.EMAIL_PARAM, emailAddress);
//
//        HttpUtils.get(HttpUtils.SIGN_IN_ENDPOINT, rp, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // If the response is JSONObject instead of expected JSONArray
//                Log.d("asd", "---------------- this is response : " + response);
//                try {
//                    JSONObject serverResp = new JSONObject(response.toString());
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
//                int a =1;
//                // Pull out the first event on the public timeline
//
//            }
//
//        });

    }

    public void openBlockSubmissionForm(String detetction) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(iBlockedUFragment);
        fragmentTransaction.replace(R.id.main_content, IBlockedUFormFragment.newInstance(detetction));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
