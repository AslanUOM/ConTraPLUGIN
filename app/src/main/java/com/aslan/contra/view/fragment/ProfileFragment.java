package com.aslan.contra.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String extraNo = "Number ";
    private ViewGroup header;
    private ViewGroup footer;
    // for dynamic list items
    private RecyclerView listView;
    private List<String> otherNumbers = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private int focusedPosition;
    private boolean isRemovePressed = false;
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
                focusedPosition = otherNumbers.size() - 1;
                adapter.notifyDataSetChanged();
                //the best practice
//                adapter.notifyItemInserted(focusedPosition);
            }
        });

        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
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

                    // todo refresh list
//                    otherNumbers.add("");
                    //todo retrieve and add the correct data
                    focusedPosition = Integer.MAX_VALUE;
                    //       todo call at end(after fetching data from net)
                    adapter = new CustomAdapter();
                    listView.setAdapter(adapter);
                    //the best practice
//        adapter.notifyItemInserted(otherNumbers.size() - 1);
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

//    /*
//     * Refresh the view after ading or removing a list item
//     */
//    private void refreshList() {
//        focusedPosition = otherNumbers.size();
//        otherNumbers.add("");
//        adapter.notifyDataSetChanged();
//
//        //the best practice
////        adapter.notifyItemInserted(otherNumbers.size() - 1);
//    }

//    private void refreshList(int position) {
//        if (position == )
//        focusedPosition = position;
//        otherNumbers.remove(position);
////        if (otherNumbers.isEmpty()) {
////            otherNumbers.add("");
////        }
//        adapter.notifyDataSetChanged();
//
//        //the best practice
////        adapter.notifyItemRemoved(position);
//    }

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


    //Custom adapter to create custom list item on the view
    private class CustomAdapter extends RecyclerView.Adapter {
//        private List<String> otherNumbers;

        public CustomAdapter() {
            super();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ViewType.HEADER;
            } else {
                return ViewType.NORMAL;
            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ViewType.NORMAL) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_list_item_layout, parent, false);
                return new NumberHolder(itemView);
            } else {
                return new NameHolder(header);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (position != 0) {
                // Find the Remove button from holder
                final Button btnRemove = (Button) holder.itemView.findViewById(R.id.btnRemove);
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("BTNINDEX_DEL", position - 1 + "");
                        isRemovePressed = true;
                        //todo change: this currently update the extra number array list before deletion
                        focusedPosition = position - 1;
                        if (position == otherNumbers.size()) {
                            focusedPosition = position - 2;
                        }
                        otherNumbers.remove(position - 1);
//        if (otherNumbers.isEmpty()) {
//            otherNumbers.add("");
//        }
                        adapter.notifyDataSetChanged();
                        //the best practice
//        adapter.notifyItemRemoved(position);
                    }
                });

                // Find the EditText from holder
                TextInputLayout etExtraPhoneHint = (TextInputLayout) holder.itemView.findViewById(R.id.etExtraPhoneHint);
                etExtraPhoneHint.setHint(extraNo + (position));
                final EditText etExtraPhone = (EditText) holder.itemView.findViewById(R.id.etExtraPhone);
                etExtraPhone.setText(otherNumbers.get(position - 1));
//            btnRemove.setVisibility(View.INVISIBLE);
                etExtraPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            btnRemove.setVisibility(View.VISIBLE);
                            focusedPosition = position - 1;
                        } else if (!isRemovePressed) {
                            btnRemove.setVisibility(View.INVISIBLE);
                            String val = etExtraPhone.getText().toString().trim();//todo check for null?
                            if (position - 1 < otherNumbers.size()) {
                                otherNumbers.set(position - 1, val);
                            }
                        } else {
                            btnRemove.setVisibility(View.INVISIBLE);
                            isRemovePressed = false;
                        }
                    }
                });


                if (position - 1 == focusedPosition) {
                    etExtraPhone.requestFocus();
                    Log.e("FOCUSSED_POS", "" + focusedPosition);
                }

//                holder.itemView.setClickable(true);
//                holder.itemView.setFocusable(true);
                //binds on click listener to list items
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });

            }
        }

        @Override
        public int getItemCount() {
            return otherNumbers.size() + 1;
        }

        private class ViewType {
            public static final int HEADER = 1;
            public static final int NORMAL = 2;
        }

        class NumberHolder extends RecyclerView.ViewHolder {
            public NumberHolder(View itemView) {
                super(itemView);
            }
        }

        class NameHolder extends RecyclerView.ViewHolder {
            public NameHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
