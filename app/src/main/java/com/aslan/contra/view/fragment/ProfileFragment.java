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
import com.aslan.contra.dto.common.Person;
import com.aslan.contra.dto.ws.Message;
import com.aslan.contra.util.Utility;
import com.aslan.contra.view.adapter.CustomProfileAdapter;
import com.aslan.contra.wsclient.ServiceConnector;
import com.aslan.contra.wsclient.UserManagementServiceClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements ServiceConnector.OnResponseListener<Person> {

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

        String name = Utility.getUserName(getContext());
        String email = Utility.getUserEmail(getContext());
        if (name != null && email != null) {
            etName.setText(name);
            etEmail.setText(email);
        } else {
            // Todo Load the existing information from the server
//        loadProfile();
        }
    }

    /**
     * Retrieve the profile infomration form the server.
     */
    private void loadProfile() {
        // UserManagementServiceClient
        UserManagementServiceClient service = new UserManagementServiceClient(getContext());
        //service.setOnResponseListener(this);
        service.retrieveUserProfile(Utility.getUserId(getContext()), ProfileFragment.this);

        // Show progress dialog while retrieving information from server
        this.progressDialog = ProgressDialog.show(getContext(), "", "Loading...");

        // Retrieve the information

    }

    /**
     * Update the given information to the server.
     */
    private void updateProfile() {
        // Show progress bar during update
        progressDialog = ProgressDialog.show(getContext(), "", "Updating...");

        UserManagementServiceClient service = new UserManagementServiceClient(getContext());
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        Utility.saveUserName(getContext(), name);
        Utility.saveUserEmail(getContext(), email);

//        service.setOnResponseListener(new OnResponseListener<String>() {
//            @Override
//            public void onResponseReceived(String result) {
//                progressDialog.dismiss();
//                String msg;
//                if (result != null) {
//                    msg = "Profile is updated successfully.";
//                } else {
//                    msg = "Failed to update the profile. Please retry again";
//                }
//                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public Class getType() {
//                return String.class;
//            }
//        });
        service.updateUserProfile(Utility.getUserId(getContext()), name, email, otherNumbers.toArray(new String[0]), new ServiceConnector.OnResponseListener<String>() {
            @Override
            public void onResponseReceived(Message<String> result) {
                progressDialog.cancel();
                progressDialog.dismiss();
                progressDialog = null;

                if (result != null && result.isSuccess()) {
//            etName.setText(result.getEntity().getName());
//            etEmail.setText(result.getEntity().getEmail());
//            String[] nums = result.getEntity().getPhoneNumbers();
//            otherNumbers.clear();
//            for (int i = 0; i < nums.length; i++) {
//                otherNumbers.add(nums[i]);
//            }
//            adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "User updated", Toast.LENGTH_LONG).show();
                } else {
                    // TODO: Replace by AlertDialog
                    Toast.makeText(getContext(), "Unable to register the user", Toast.LENGTH_LONG).show();
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

    @Override
    public void onResponseReceived(Message<Person> result) {
        // Hide the progress dialog
        progressDialog.cancel();
        progressDialog.dismiss();
        progressDialog = null;

        if (result != null && result.isSuccess()) {
            etName.setText(result.getEntity().getName());
            etEmail.setText(result.getEntity().getEmail());
            String[] nums = result.getEntity().getPhoneNumbers();
            otherNumbers.clear();
            for (int i = 0; i < nums.length; i++) {
                otherNumbers.add(nums[i]);
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "User updated", Toast.LENGTH_LONG).show();
        } else {
            // TODO: Replace by AlertDialog
            Toast.makeText(getContext(), "Unable to register the user", Toast.LENGTH_LONG).show();
        }
    }
}
