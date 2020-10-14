package com.tranhaison.englishportugesedictionary.adapters.detailactivity.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class SynonymAdapter extends BaseAdapter {

    // Init Fragment listener
    private ListItemListener itemListenerSpeaker, itemListener;

    // Init variables
    private Context context;
    private ArrayList<DictionaryWord> relatedWordList;

    public SynonymAdapter(Context context, ArrayList<DictionaryWord> relatedWordList) {
        this.context = context;
        this.relatedWordList = relatedWordList;
    }

    @Override
    public int getCount() {
        return relatedWordList.size();
    }

    @Override
    public Object getItem(int i) {
        return relatedWordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        try {
            return relatedWordList.get(i).getWordList_id();
        } finally {
            return 0;
        }
    }

    private class ViewHolder {
        ImageButton ibSpeakerRelated;
        TextView tvRelatedWord, tvRelatedWordExplanation, tvRelatedWordNumber;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.synonym_layout_item, null);

            // Map Views from layout file
            viewHolder = new ViewHolder();
            viewHolder.ibSpeakerRelated = view.findViewById(R.id.ibSpeakerRelated);
            viewHolder.tvRelatedWord = view.findViewById(R.id.tvRelatedWord);
            viewHolder.tvRelatedWordExplanation = view.findViewById(R.id.tvRelatedWordExplanation);
            viewHolder.tvRelatedWordNumber = view.findViewById(R.id.tvRelatedWordNumber);

            // Set tag
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Set text to text view
        DictionaryWord dictionaryWord = relatedWordList.get(i);
        viewHolder.tvRelatedWord.setText(dictionaryWord.getDisplayWord());
        viewHolder.tvRelatedWordExplanation.setText(dictionaryWord.getExplanations());
        viewHolder.tvRelatedWordNumber.setText((i+1) + ".");

        // Speaker button clicked
        viewHolder.ibSpeakerRelated.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Handle item speaker clicked event
     * @param itemListenerSpeaker
     */
    public void setOnItemSpeakerClick(ListItemListener itemListenerSpeaker) {
        this.itemListenerSpeaker = itemListenerSpeaker;
    }

    /**
     * Handle item clicked event
     * @param itemListener
     */
    public void setOnItemClick(ListItemListener itemListener) {
        this.itemListener = itemListener;
    }
}
