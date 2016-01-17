package com.aslan.contra.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aslan.contra.R;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;
import com.aslan.contra.view.adapter.CustomProfileAdapter;
import com.aslan.contra.wsclient.OnResponseListener;
import com.aslan.contra.wsclient.UserManagementServiceClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ViewGroup header;
    // for dynamic list items
    private RecyclerView listView;
    private List<String> otherNumbers = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    //    private int focusedPosition;
//    private boolean isRemovePressed = false;
    private OnFragmentInteractionListener mListener;
    // UI components
    private EditText etRegPhoneNo;
    private EditText etName;
    private EditText etEmail;
    private Button btnAdd;
    private Button btnUpdate;
    private ProgressDialog progressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
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
        listView = (RecyclerView) view.findViewById(R.id.recyclerView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        header = (ViewGroup) getLayoutInflater(savedInstanceState).inflate(R.layout.profile_header_layout, listView, false);

        etRegPhoneNo = (EditText) header.findViewById(R.id.etRegPhoneNo);
        etRegPhoneNo.setText(Utility.getUserId(getContext()));
        etName = (EditText) header.findViewById(R.id.etName);
        etName.requestFocus();
        etEmail = (EditText) header.findViewById(R.id.etEmail);

        // Find the Add button from holder
        btnAdd = (Button) header.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo onclick
                otherNumbers.add("");//todo retrieve and add the correct data
//                focusedPosition = otherNumbers.size() - 1;
                adapter.notifyDataSetChanged();
                //the best practice
//                adapter.notifyItemInserted(focusedPosition);
            }
        });

        otherNumbers = Utility.getOtherNumbers(getContext());
        adapter = new CustomProfileAdapter(otherNumbers, header);
        listView.setAdapter(adapter);
        //the best practice
//        adapter.notifyItemInserted(otherNumbers.size() - 1);

        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        // Set OnClickListener
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                try {
                    Utility.saveOtherNumbers(getContext(), otherNumbers);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                    //todo retrieve and add the correct data
//                    adapter.notifyDataSetChanged();
//                    focusedPosition = Integer.MAX_VALUE;

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
