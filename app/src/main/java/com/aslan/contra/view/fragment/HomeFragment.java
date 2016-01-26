package com.aslan.contra.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aslan.contra.R;
import com.aslan.contra.commons.App;
import com.aslan.contra.util.Utility;
import com.aslan.contra.view.adapter.PromoAppCollectionPagerAdapter;

public class HomeFragment extends Fragment {
    private TextView tvGreetings;
    private TextView tvDescription;
    // for dynamic list items
//    private RecyclerView listView;
//    private RecyclerView.Adapter<AppViewHolder> adapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    private ViewPager promoAppPager;
    private PromoAppCollectionPagerAdapter promoAppCollectionPagerAdapter;
    private App[] apps;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.startSensors(getContext(), false);
//        adapter = new AppAdapter(apps);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find the UI view
        tvGreetings = (TextView) view.findViewById(R.id.tvGreetings);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        promoAppPager = (ViewPager) view.findViewById(R.id.pager);
        promoAppCollectionPagerAdapter = new PromoAppCollectionPagerAdapter(getChildFragmentManager(), Utility.getAllApps(getContext()).length);
        promoAppPager.setAdapter(promoAppCollectionPagerAdapter);
//        listView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        listView.setHasFixedSize(true);
//        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        listView.setAdapter(adapter);

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


//    private class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {
//        private App[] apps;
//
//        public AppAdapter(App[] apps) {
//            this.apps = apps;
//        }
//
//        @Override
//        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            Context ctx = parent.getContext();
//            View itemView = LayoutInflater.from(ctx).inflate(R.layout.home_list_item_layout, parent, false);
//            return new AppViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(AppViewHolder holder, int position) {
//            App app = apps[position];
//            holder.setApps(app);
//        }
//
//        @Override
//        public int getItemCount() {
//            return apps.length;
//        }
//    }
//
//    private class AppViewHolder extends RecyclerView.ViewHolder {
//        private TextView tvPromoTitle;
//        private ImageView ivAppIcon;
//        private Button btnGPlay;
//        private App app;
//
//        public AppViewHolder(View itemView) {
//            super(itemView);
//
//            this.tvPromoTitle = (TextView) itemView.findViewById(R.id.tvPromoTitle);
//            this.ivAppIcon = (ImageView) itemView.findViewById(R.id.ivAppIcon);
//            this.btnGPlay = (Button) itemView.findViewById(R.id.btnGPlay);
//        }
//
//        public void setApps(final App app) {
//            this.app = app;
//            this.ivAppIcon.setImageDrawable(app.getIcon());
//            this.tvPromoTitle.setText(app.getTitle());
//            this.btnGPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(app.getUrl()));
//                    startActivity(intent);
//                }
//            });
//        }
//    }

}
