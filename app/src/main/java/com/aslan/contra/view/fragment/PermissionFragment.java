package com.aslan.contra.view.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aslan.contra.R;
import com.aslan.contra.commons.Feature;
import com.aslan.contra.util.Constants;
import com.aslan.contra.util.Utility;

import java.util.List;
import java.util.logging.ConsoleHandler;


public class PermissionFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private Button btnGrantPermissions;
    private Context context;
    private final int PERMISSION_REQUEST_CODE = 100;


    public PermissionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permission, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.context = view.getContext();
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerFeatures);
        this.btnGrantPermissions = (Button) view.findViewById(R.id.btnGrantPermissions);

        // Get all the features
        Feature[] features = Utility.getAllFeatures(getContext());
        final String[] nonGrantedPermissions = Utility.nonGrantedPermissions(getContext());

        if (nonGrantedPermissions.length != 0) {
            btnGrantPermissions.setVisibility(View.VISIBLE);
            btnGrantPermissions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissions(nonGrantedPermissions, PERMISSION_REQUEST_CODE);
                }
            });
        } else {
            btnGrantPermissions.setVisibility(View.GONE);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        RecyclerView.Adapter<FeatureViewHolder> adapter = new FeatureAdapter(features);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                btnGrantPermissions.setVisibility(View.GONE);
                mListener.onFragmentInteraction(this, Constants.ALL_PERMISSIONS_GRANTED);
            }
        }
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

    private class FeatureAdapter extends RecyclerView.Adapter<FeatureViewHolder> {
        private Feature[] features;

        public FeatureAdapter(Feature[] features) {
            this.features = features;
        }

        @Override
        public FeatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context ctx = parent.getContext();
            View itemView = LayoutInflater.from(ctx).inflate(R.layout.layout_feature_item, parent, false);
            return new FeatureViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FeatureViewHolder holder, int position) {
            Feature feature = features[position];
            holder.setFeature(feature);
        }

        @Override
        public int getItemCount() {
            return features.length;
        }
    }

    private class FeatureViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgIcon;
        private TextView txtFeature;
        private TextView txtDescription;
        private Feature feature;

        public FeatureViewHolder(View itemView) {
            super(itemView);

            this.imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            this.txtFeature = (TextView) itemView.findViewById(R.id.txtFeature);
            this.txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        }

        public void setFeature(Feature feature) {
            this.feature = feature;
            this.imgIcon.setImageDrawable(feature.getIcon());
            this.txtFeature.setText(feature.getName());
            this.txtDescription.setText(feature.getDescription());
        }
    }

}
