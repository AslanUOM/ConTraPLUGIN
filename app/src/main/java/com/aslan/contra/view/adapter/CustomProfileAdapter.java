package com.aslan.contra.view.adapter;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.aslan.contra.R;

import java.util.List;

/**
 * Created by vishnuvathsan on 17-Jan-16.
 */

//Custom adapter to create custom list item on the view
public class CustomProfileAdapter extends RecyclerView.Adapter {
    //        private List<String> otherNumbers;
    private List<String> otherNumbers;
    private View header;
    private int focusedPosition = Integer.MAX_VALUE;
    private boolean isRemovePressed = false;

    public CustomProfileAdapter(List<String> numbers, View view) {
        super();
        otherNumbers = numbers;
        header = view;
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
                    notifyDataSetChanged();
                    //the best practice
//        adapter.notifyItemRemoved(position);
                }
            });

            // Find the EditText from holder
            TextInputLayout etExtraPhoneHint = (TextInputLayout) holder.itemView.findViewById(R.id.etExtraPhoneHint);
            etExtraPhoneHint.setHint("Number " + (position));
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
