package com.tranhaison.englishportugesedictionary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class FavoriteAdapter extends BaseAdapter {

    // Init Fragment listener
    private ListItemListener listItemListener;
    private ListItemListener listItemDeleteListener;

    // Init global variables
    private Context context;
    private ArrayList<BookmarkWord> favoriteList;
    private DatabaseHelper databaseHelper;

    public FavoriteAdapter(Context context, DatabaseHelper databaseHelper) {
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public int getCount() {
        getData();
        return favoriteList.size();
    }

    @Override
    public Object getItem(int i) {
        getData();
        return favoriteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView tvFavoriteWord, tvFavoriteDefinition;
        ImageButton ibFavoriteDelete;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        getData();
        ViewHolder viewHolder;

        if (view == null) {
            // Call layout inflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Set layout file to view
            view = inflater.inflate(R.layout.favorite_layout_item, null);

            // Map Views and set tag to view
            viewHolder = new ViewHolder();
            viewHolder.tvFavoriteWord = view.findViewById(R.id.tvFavoriteWord);
            viewHolder.tvFavoriteDefinition = view.findViewById(R.id.tvFavoriteDefinition);
            viewHolder.ibFavoriteDelete = view.findViewById(R.id.ibFavoriteDelete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Get favorite word and definition
        BookmarkWord favoriteWord = favoriteList.get(position);
        String displayWord = favoriteWord.getDisplayWord();
        String explanation = favoriteWord.getExplanations();

        // Set text to text view
        viewHolder.tvFavoriteWord.setText(displayWord);
        viewHolder.tvFavoriteDefinition.setText(explanation);

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
        viewHolder.ibFavoriteDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItemDeleteListener != null) {
                    listItemDeleteListener.onItemClick(position);
                }
            }
        });

        return view;
    }

    public void getData() {
        //favoriteList.clear();
        favoriteList = databaseHelper.getAllFavorite();
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
        favoriteList.remove(position);
    }
}
