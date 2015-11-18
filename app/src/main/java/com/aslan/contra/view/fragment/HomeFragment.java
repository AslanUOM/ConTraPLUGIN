package com.aslan.contra.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.aslan.contra.R;
import com.aslan.contra.services.LocationTrackingService;
import com.aslan.contra.util.DatabaseHelper;
import com.aslan.contra.util.RunningServices;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;

public class HomeFragment extends Fragment implements OnResponseListener<String> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //From OnResponseListener
    @Override
    public void onResponseReceived(String response) {
        if (response != null) {
            // TODO handle received response from server for location changed
            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(getContext(), "Unable to send location to server", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        SwitchCompat swLocTrackEnable = (SwitchCompat) view.findViewById(R.id.swLocTrack);
        swLocTrackEnable.setChecked(RunningServices.getInstance().isLocationServiceRunning(getContext()));
        swLocTrackEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent serviceIntent = new Intent(getContext(), LocationTrackingService.class);
                    serviceIntent.addCategory(LocationTrackingService.TAG);
                    getContext().startService(serviceIntent);
                    Toast.makeText(getContext(), "Location Tracking Started @ PLUGIN", Toast.LENGTH_LONG).show();
                } else {
                    Intent serviceIntent = new Intent(getContext(), LocationTrackingService.class);
                    serviceIntent.addCategory(LocationTrackingService.TAG);
                    getContext().stopService(serviceIntent);
                    Toast.makeText(getContext(), "Location Tracking Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                }
            }
        });

        //TODO remove commented code when switch is working fine
//        Button mButton = (Button) view.findViewById(R.id.btnStart);
//        mButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                if (!RunningServices.getInstance().isLocationServiceRunning(getContext())) {
//                    Intent serviceIntent = new Intent(getContext(), LocationTrackingService.class);
//                    serviceIntent.addCategory(LocationTrackingService.TAG);
//                    getContext().startService(serviceIntent);
//                    Toast.makeText(getContext(), "Location Tracking Started @ PLUGIN", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getContext(), "Tracking service is already running", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        Button mButton2 = (Button) view.findViewById(R.id.btnStop);
//        mButton2.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                if (RunningServices.getInstance().isLocationServiceRunning(getContext())) {
//                    Intent serviceIntent = new Intent(getContext(), LocationTrackingService.class);
//                    serviceIntent.addCategory(LocationTrackingService.TAG);
//                    getContext().stopService(serviceIntent);
//                    Toast.makeText(getContext(), "Location Tracking Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getContext(), "Tracking service is not running", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        Button btnGetContacts = (Button) view.findViewById(R.id.btnContacts);
        btnGetContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(getContext());
                service.setOnResponseListener(HomeFragment.this);
                service.sendContacts();
            }
        });
        btnGetContacts.performClick();
        Button btnExportToSD = (Button) view.findViewById(R.id.btnExport);
        btnExportToSD.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!RunningServices.getInstance().isLocationServiceRunning(getContext())) {
                    dbHelper.exportToSdCard(getContext());
                } else {
                    Toast.makeText(getContext(), "Stop Tracking service first and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
