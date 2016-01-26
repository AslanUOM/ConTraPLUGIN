package com.aslan.contra.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aslan.contra.R;
import com.aslan.contra.commons.App;
import com.aslan.contra.util.Utility;

/**
 * Created by Vishnuvathsasarma on 26-Jan-16.
 */
public class PromoAppObjectFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    private TextView tvPromoTitle;
    private ImageView ivAppIcon;
    private Button btnGPlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//            promoAppPager.removeView(videoView);
//            videoView = (VideoView) rootView;

//            index = args.getInt(ARG_OBJECT);
//            changeView();

        return inflater.inflate(R.layout.home_list_item_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App[] apps = Utility.getAllApps(getContext());
        this.tvPromoTitle = (TextView) view.findViewById(R.id.tvPromoTitle);
        this.ivAppIcon = (ImageView) view.findViewById(R.id.ivAppIcon);
        this.btnGPlay = (Button) view.findViewById(R.id.btnGPlay);
        Bundle args = getArguments();
        setApps(apps[args.getInt(ARG_OBJECT)]);
    }

    public void setApps(final App app) {
        this.ivAppIcon.setImageDrawable(app.getIcon());
        this.tvPromoTitle.setText(app.getTitle());
        this.btnGPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(app.getUrl()));
                startActivity(intent);
            }
        });
    }
}
