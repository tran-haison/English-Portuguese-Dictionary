package com.tranhaison.englishportugesedictionary.adapters;

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

public class FavoriteAdapter extends BaseAdapter {

    // Init Fragment listener
    private ListItemListener listItemListener;
    private ListItemListener listItemDeleteListener;

    // Init global variables
    private Context context;
    private ArrayList<String> wordsList;

    public FavoriteAdapter(Context context, ArrayList<String> wordsList) {
        this.context = context;
        this.wordsList = wordsList;
    }

    @Override
    public int getCount() {
        return wordsList.size();
    }

    @Override
    public Object getItem(int i) {
        return wordsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView tvFavoriteWord;
        ImageButton ibDelete;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            // Call layout inflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Set layout file to view
            view = inflater.inflate(R.layout.favorite_layout_item, null);

            // Map Views and set tag to view
            viewHolder = new ViewHolder();
            viewHolder.tvFavoriteWord = view.findViewById(R.id.tvFavoriteWord);
            viewHolder.ibDelete = view.findViewById(R.id.ibDelete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Set text to tvFavoriteWord
        String favorite_word = wordsList.get(position);
        viewHolder.tvFavoriteWord.setText(favorite_word);

        // tvFavoriteWord clicked
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItemListener != null) {
                    listItemListener.onItemClick(position);
                }
            }
        });

        // ibDelete clicked
        viewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItemDeleteListener != null) {
                    listItemDeleteListener.onItemClick(position);
                }
            }
        });

        return view;
    }

    /**
     * Handle item clicked event
     * @param listItemListener
     */
    public void setOnItemClick(ListItemListener listItemListener) {
        this.listItemListener = listItemListener;
    }

    /**
     * Handle item clicked event
     * @param listItemListener
     */
    public void setOnItemDeleteClick(ListItemListener listItemListener) {
        this.listItemDeleteListener = listItemListener;
    }

    /**
     * Remove a word from list of favorites
     * @param position
     */
    public void removeWord(int position) {
        wordsList.remove(position);
    }
}
