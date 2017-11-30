package com.salesforce.iblockedu;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IBlockUGoingHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IBlockUGoingHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IBlockUGoingHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_EMAIL = "email";
    private static final String ARG_PARAM_LICENSE_PLATE = "param2";

    // TODO: Rename and change types of parameters
    private String mParamEmail;
    private String mParamLicensePlate;

    private OnFragmentInteractionListener mListener;
    private TextView mMessageView;
    private Button bUnblock;

    public IBlockUGoingHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param email Parameter 1.
     * @return A new instance of fragment IBlockUGoingHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IBlockUGoingHomeFragment newInstance(String email, String licensePlate) {
        IBlockUGoingHomeFragment fragment = new IBlockUGoingHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_EMAIL, email);
        args.putString(ARG_PARAM_LICENSE_PLATE, licensePlate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamEmail = getArguments().getString(ARG_PARAM_EMAIL);
            mParamLicensePlate = getArguments().getString(ARG_PARAM_LICENSE_PLATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_iblock_ugoing_home, container, false);
        mMessageView = (TextView)inflate.findViewById(R.id.textViewHomeMessage);
        bUnblock = (Button)inflate.findViewById(R.id.btnUnblock);

        return inflate;
    }

    @Override
    public void onResume() {
        //Build the blocking message
        String license = getArguments().getString(ARG_PARAM_LICENSE_PLATE);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String owner = sharedPref.getString(license, "a Guest");
        String message = "You are now blocking "+ owner + "\n\nClick UNBLOCK when you're heading out!";
        mMessageView.setText(message);

        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void handleUnblock() {
        boolean hasInternet = ((IBlockedUMainActivity)getActivity()).hasInternetConnection();

        if(hasInternet) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUtils.getRequestUrl(
                    HttpUtils.GOING_HOME_ENDPOINT, getArguments().getString("email"), ""),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String msg = "";
                            if(response.toLowerCase().contains("error")){
                                msg = "No information at the moment. Try again later...";
                            } else{
                                if (response.split(",").length > 1) {
                                    msg = response.split(",")[0] + "\n\n" + response.split(",")[1];
                                }else {
                                    msg = response;
                                }
                            }
                            if(response.isEmpty()) {
                                msg = "No information at the moment";
                            }
                            mMessageView.setText(msg);
                            bUnblock.setEnabled(false);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Please try submitting again", Toast.LENGTH_LONG).show();
                }
            });
            queue.add(stringRequest);
        } else {
            //TODO: IMPLEMENT OFFLINE MECHANISM
        }
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
