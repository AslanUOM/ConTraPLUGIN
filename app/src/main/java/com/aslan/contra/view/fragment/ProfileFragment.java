package com.aslan.contra.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aslan.contra.R;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.UserManagementServiceClient;

import java.util.Map;

public class ProfileFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    // UI components
    private EditText etName;
    private EditText etEmail;
    private Button btnUpdate;
    private ProgressDialog progressDialog;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the UI view
        this.etName = (EditText) view.findViewById(R.id.etName);
        this.etEmail = (EditText) view.findViewById(R.id.etEmail);
        this.btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

        // Set OnClickListener
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Load the existing information from the server
        loadProfile();
    }

    /**
     * Retrieve the profile infomration form the server.
     */
    private void loadProfile() {
        // UserManagementServiceClient
        final UserManagementServiceClient<Map<String, String>> retrieveServiceClient = new UserManagementServiceClient<>(getContext());

        // Show progress dialog while retrieving information from server
        this.progressDialog = ProgressDialog.show(getContext(), "", "Loading...");

        // Retrieve the information
        retrieveServiceClient.setOnResponseListener(new OnResponseListener<Map<String, String>>() {
            @Override
            public void onResponseReceived(Map<String, String> result) {
                // Hide the progress dialog
                progressDialog.dismiss();
                if (result != null) {
                    etName.setText(result.get(Constants.NAME));
                    etEmail.setText(result.get(Constants.EMAIL));
                }
            }

            @Override
            public Class getType() {
                return Map.class;
            }
        });
        // Execute the task
        retrieveServiceClient.retrieveUserProfile(Utility.getUserId(getContext()));
    }

    /**
     * Update the given information to the server.
     */
    private void updateProfile() {
        // Show progress bar during update
        progressDialog = ProgressDialog.show(getContext(), "", "Updating...");

        final UserManagementServiceClient<String> updateServiceClient = new UserManagementServiceClient<>(getContext());
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();

        updateServiceClient.setOnResponseListener(new OnResponseListener<String>() {
            @Override
            public void onResponseReceived(String result) {
                progressDialog.dismiss();
                String msg;
                if (result != null) {
                    msg = "Profile is updated successfully.";
                } else {
                    msg = "Failed to update the profile. Please retry again";
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public Class getType() {
                return String.class;
            }
        });
        updateServiceClient.updateUserProfile(Utility.getUserId(getContext()), name, email);
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
