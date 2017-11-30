package com.salesforce.iblockedu;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IBlockedUFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IBlockedUFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IBlockedUFormFragment extends Fragment {
    public static final String PREFS_NAME = "IBlockedUPrefs";
    private static final String ARG_PARAM_LICENSE_PLATE = "param_license_plate";
    private static final String ARG_PARAM_EMAIL = "param_email";

    private String mParamLicensePlate;
    private String mParamEmail;

    private OnFragmentInteractionListener mListener;

    public IBlockedUFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param licensePlate Parameter 1.

     * @return A new instance of fragment IBlockedUFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IBlockedUFormFragment newInstance(String licensePlate, String email) {
        IBlockedUFormFragment fragment = new IBlockedUFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_LICENSE_PLATE, licensePlate);
        args.putString(ARG_PARAM_EMAIL, email);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamLicensePlate = getArguments().getString(ARG_PARAM_LICENSE_PLATE);
            mParamEmail = getArguments().getString(ARG_PARAM_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_iblocked_uform, container, false);
        String license = getArguments().getString(ARG_PARAM_LICENSE_PLATE);
        ((EditText)inflate.findViewById(R.id.license_plate)).setText(license);


        //Build the blocking message
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String owner = sharedPref.getString(license, "Guest");
        String message = "You are blocking "+ owner;
        ((TextView)inflate.findViewById(R.id.form_block_message)).setText(message);

        return inflate;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void handleSubmit(View view) {
        String licensePlate = getArguments().getString(ARG_PARAM_LICENSE_PLATE);
        String email = getArguments().getString(ARG_PARAM_EMAIL);

        boolean hasInternet = ((IBlockedUMainActivity)getActivity()).hasInternetConnection();

        if(hasInternet) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUtils.getRequestUrl(
                    HttpUtils.BLOCK_ENDPOINT, email, licensePlate),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please try submitting again", Toast.LENGTH_LONG).show();
                }
            });
            queue.add(stringRequest);
        } else {
            SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("offlline_email", email);
            editor.putString("offline_name", licensePlate);
            editor.apply();

            getActivity().registerReceiver(
                    new NetworkChangeReceiver((IBlockedUMainActivity)getActivity()),
                    new IntentFilter(
                            ConnectivityManager.CONNECTIVITY_ACTION));
        }
        ((IBlockedUMainActivity)getActivity()).openGoingHomeFragment();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
