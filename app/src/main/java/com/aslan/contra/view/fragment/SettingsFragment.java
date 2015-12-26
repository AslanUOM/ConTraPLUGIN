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
import com.aslan.contra.services.ActivityRecognitionService;
import com.aslan.contra.services.EnvironmentMonitorService;
import com.aslan.contra.services.LocationTrackingService;
import com.aslan.contra.services.NearbyTerminalTrackingService;
import com.aslan.contra.util.DatabaseHelper;
import com.aslan.contra.util.RunningServices;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.SensorDataSendingServiceClient;

public class SettingsFragment extends Fragment implements OnResponseListener<String> {
    // UI Components
    private Button btnExportToSD;
    private Button btnGetContacts;

    private SwitchCompat swLocTrackEnable;
    private SwitchCompat swActivityTrack;
    private SwitchCompat swEnvironmentMonitor;
    private SwitchCompat swNearbyTerminal;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        // Find the user interface components
        this.btnGetContacts = (Button) view.findViewById(R.id.btnContacts);
        this.btnExportToSD = (Button) view.findViewById(R.id.btnExport);

        this.swLocTrackEnable = (SwitchCompat) view.findViewById(R.id.swLocTrack);
        this.swActivityTrack = (SwitchCompat) view.findViewById(R.id.swActivityTrack);
        this.swEnvironmentMonitor = (SwitchCompat) view.findViewById(R.id.swEnvironmentMonitor);
        this.swNearbyTerminal = (SwitchCompat) view.findViewById(R.id.swNearbyTerminal);

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

        swNearbyTerminal.setChecked(RunningServices.getInstance().isNearbyTerminalTrackingServiceRunning(getContext()));
        swNearbyTerminal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent serviceIntent = new Intent(getContext(), NearbyTerminalTrackingService.class);
                    serviceIntent.addCategory(NearbyTerminalTrackingService.TAG);
                    getContext().startService(serviceIntent);
                    Toast.makeText(getContext(), "Nearby Terminal Tracking Started @ PLUGIN", Toast.LENGTH_LONG).show();
                } else {
                    Intent serviceIntent = new Intent(getContext(), NearbyTerminalTrackingService.class);
                    serviceIntent.addCategory(NearbyTerminalTrackingService.TAG);
                    getContext().stopService(serviceIntent);
                    Toast.makeText(getContext(), "Nearby Terminal Tracking Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                }
            }
        });

        swActivityTrack.setChecked(RunningServices.getInstance().isActivityRecognitionServiceRunning(getContext()));
        swActivityTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent serviceIntent = new Intent(getContext(), ActivityRecognitionService.class);
                    serviceIntent.addCategory(ActivityRecognitionService.TAG);
                    getContext().startService(serviceIntent);
                    Toast.makeText(getContext(), "Activity Recognition Started @ PLUGIN", Toast.LENGTH_LONG).show();
                } else {
                    Intent serviceIntent = new Intent(getContext(), ActivityRecognitionService.class);
                    serviceIntent.addCategory(ActivityRecognitionService.TAG);
                    getContext().stopService(serviceIntent);
                    Toast.makeText(getContext(), "Activity Recognition Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                }
            }
        });

        swEnvironmentMonitor.setChecked(RunningServices.getInstance().isEnvironmentMonitorServiceRunning(getContext()));
        swEnvironmentMonitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent serviceIntent = new Intent(getContext(), EnvironmentMonitorService.class);
                    serviceIntent.addCategory(EnvironmentMonitorService.TAG);
                    getContext().startService(serviceIntent);
                    Toast.makeText(getContext(), "Environment Monitoring Started @ PLUGIN", Toast.LENGTH_LONG).show();
                } else {
                    Intent serviceIntent = new Intent(getContext(), EnvironmentMonitorService.class);
                    serviceIntent.addCategory(EnvironmentMonitorService.TAG);
                    getContext().stopService(serviceIntent);
                    Toast.makeText(getContext(), "Environment Monitoring Stopped @ PLUGIN", Toast.LENGTH_LONG).show();
                }
            }
        });


        btnGetContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendContacts();
            }
        });

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

    /**
     * Send contacts.
     */
    private void sendContacts() {
        SensorDataSendingServiceClient service = new SensorDataSendingServiceClient(getContext());
        service.setOnResponseListener(SettingsFragment.this);
        service.sendContacts();
    }

    //From OnResponseListener
    @Override
    public void onResponseReceived(String response) {
        if (response != null) {
            // TODO handle received response from server for location changed
            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(getContext(), "Unable to send data to server", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public Class getType() {
        return String.class;
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
