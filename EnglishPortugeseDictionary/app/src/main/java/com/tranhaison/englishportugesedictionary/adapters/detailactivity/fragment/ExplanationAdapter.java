package com.tranhaison.englishportugesedictionary.adapters.detailactivity.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class ExplanationAdapter extends BaseAdapter {

    // Init fragment listener
    private ListItemListener itemListenerSpeaker, itemListener;

    // Init variables
    private Context context;
    private ArrayList<String> explanationList;

    public ExplanationAdapter(Context context, ArrayList<String> explanationList) {
        this.context = context;
        this.explanationList = explanationList;
    }

    @Override
    public int getCount() {
        return explanationList.size();
    }

    @Override
    public Object getItem(int i) {
        return explanationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            // Call layout inflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Set layout file to view
            view = inflater.inflate(R.layout.explanation_layout_item, null);

            // Map Views
            viewHolder = new ViewHolder();
            viewHolder.tvExplanationItem = view.findViewById(R.id.tvExplanationItem);
            viewHolder.ibSpeakerExplanation = view.findViewById(R.id.ibSpeakerExplanation);
            viewHolder.tvExplanationItemNumber = view.findViewById(R.id.tvExplanationItemNumber);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String explanation = explanationList.get(i);
        viewHolder.tvExplanationItem.setText(explanation);
        viewHolder.tvExplanationItemNumber.setText((i+1) + ".");

        // Speaker clicked
        viewHolder.ibSpeakerExplanation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListenerSpeaker != null) {
                    itemListenerSpeaker.onItemClick(i);
                }
            }
        });

        // Item clicked
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListener != null) {
                    itemListener.onItemClick(i);
                }
            }
        });

        return view;
    }

    private class ViewHolder {
        TextView tvExplanationItem, tvExplanationItemNumber;
        ImageButton ibSpeakerExplanation;
    }

    /**
     * Handle item speaker clicked event
     * @param itemListenerSpeaker
     */
    public void setOnItemSpeakerClicked(ListItemListener itemListenerSpeaker) {
        this.itemListenerSpeaker = itemListenerSpeaker;
    }

    /**
     * Handle item clicked event
     * @param itemListener
     */
    public void setOnItemClicked(ListItemListener itemListener) {
        this.itemListener = itemListener;
    }
}
