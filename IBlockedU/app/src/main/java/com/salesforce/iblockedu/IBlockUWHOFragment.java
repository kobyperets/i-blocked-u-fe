package com.salesforce.iblockedu;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
 * {@link IBlockUWHOFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IBlockUWHOFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IBlockUWHOFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_EMAIL = "paramEmail";
    private static final String ARG_PARAM_BLOCKING_OWNER = "paramBlockingOwner";

    // TODO: Rename and change types of parameters
    private String mParamEmail;
    private String mParamBlockingOwner;

    private OnFragmentInteractionListener mListener;
    private TextView mMessageView;
    private Button bWhoBlocked;
    private ImageView imageBlocked;

    public IBlockUWHOFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment IBlockUWHOFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IBlockUWHOFragment newInstance(String email, String blockingOwner) {
        IBlockUWHOFragment fragment = new IBlockUWHOFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_EMAIL, email);
        args.putString(ARG_PARAM_BLOCKING_OWNER, blockingOwner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamEmail = getArguments().getString(ARG_PARAM_EMAIL);
            mParamBlockingOwner = getArguments().getString(ARG_PARAM_BLOCKING_OWNER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_iblock_uwho, container, false);
        mMessageView = (TextView)inflate.findViewById(R.id.textViewWhoBlocksMessage);
        bWhoBlocked = (Button)inflate.findViewById(R.id.buttonStillBlocked);
        imageBlocked = (ImageView)inflate.findViewById(R.id.image_who_blocked);

        bWhoBlocked.setVisibility(View.INVISIBLE);

        boolean hasInternet = ((IBlockedUMainActivity)getActivity()).hasInternetConnection();

        if(hasInternet && mParamBlockingOwner.isEmpty()) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // Request a string response from the provided URL.
            String email = getArguments().getString(ARG_PARAM_EMAIL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUtils.getRequestUrl(
                    HttpUtils.WHO_BLOCKS_ENDPOINT, email, ""),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String msg = "";
                            if(response.toLowerCase().contains("error")){
                                msg = "No information at the moment. Try again later...";
                            } else{
                                msg = response;
                                if (response.toLowerCase().contains("free")){
                                    bWhoBlocked.setVisibility(View.VISIBLE);
                                    imageBlocked.setVisibility(View.INVISIBLE);
                                }
                                if (response.toLowerCase().contains("currently")){
                                    imageBlocked.setVisibility(View.VISIBLE);
                                    imageBlocked.bringToFront();
                                }
                            }
                            mMessageView.setText(msg);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("IBlockUWHOFragment", error.toString());
                    mMessageView.setText("No information at the moment. Try again later...");
//                    Toast.makeText(getActivity().getApplicationContext(), "Please try submitting again", Toast.LENGTH_LONG).show();
                }
            });
            queue.add(stringRequest);
        } else {
            if(!mParamBlockingOwner.isEmpty()) {
                mMessageView.setText("It looks like " + mParamBlockingOwner + " is blocking you.\n\n" +
                        "Please contact the front desk at 03-6793600");
            } else {
                mMessageView.setText("No registered blocking \n\nYou are free to go!");
            }
        }

        return inflate;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
