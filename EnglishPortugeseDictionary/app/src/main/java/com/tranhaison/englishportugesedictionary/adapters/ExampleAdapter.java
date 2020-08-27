package com.tranhaison.englishportugesedictionary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.examples.ExampleSentence;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class ExampleAdapter extends BaseAdapter {

    // Init Fragment listener
    private ListItemListener itemListenerSpeaker;

    // Init global variables
    private Context context;
    private ArrayList<ExampleSentence> exampleList;

    public ExampleAdapter(Context context, ArrayList<ExampleSentence> exampleList) {
        this.context = context;
        this.exampleList = exampleList;
    }

    @Override
    public int getCount() {
        return exampleList.size();
    }

    @Override
    public Object getItem(int i) {
        return exampleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return exampleList.get(i).get_id();
    }

    private class ViewHolder {
        TextView tvEnglishSentence, tvTranslation;
        ImageButton ibSpeakerExample;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            // Call layout inflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Set layout file to view
            view = inflater.inflate(R.layout.example_layout_item, null);

            // Map Views
            viewHolder = new ViewHolder();
            viewHolder.tvEnglishSentence = view.findViewById(R.id.tvEnglishSentence);
            viewHolder.tvTranslation = view.findViewById(R.id.tvTranslation);
            viewHolder.ibSpeakerExample = view.findViewById(R.id.ibSpeakerExample);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Set text to text view
        ExampleSentence sentence = exampleList.get(i);
        viewHolder.tvEnglishSentence.setText(sentence.getEnglish_sentences());
        viewHolder.tvTranslation.setText(sentence.getTranslation());

        // Event clicked
        viewHolder.ibSpeakerExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListenerSpeaker != null) {
                    itemListenerSpeaker.onItemClick(i);
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
}
