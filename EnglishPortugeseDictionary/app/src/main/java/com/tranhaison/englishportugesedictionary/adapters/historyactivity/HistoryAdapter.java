package com.tranhaison.englishportugesedictionary.adapters.historyactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {

    // Init global variables
    private Context context;
    private ArrayList<BookmarkWord> historyList;
    private DatabaseHelper databaseHelper;

    private ListItemListener itemListener;

    public HistoryAdapter(Context context, DatabaseHelper databaseHelper) {
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public int getCount() {
        getData();
        return historyList.size();
    }

    @Override
    public Object getItem(int i) {
        getData();
        return historyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView tvHistoryWord, tvHistoryWordType, tvHistoryExplanation;
        CardView cvHistoryWordType;
        ImageButton ibFavoriteFromHistory;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        getData();
        final ViewHolder viewHolder;

        if (view == null) {
            // Get view from layout file
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.history_layout_item, null);

            // Hooks
            viewHolder = new ViewHolder();
            viewHolder.tvHistoryWord = view.findViewById(R.id.tvHistoryWord);
            viewHolder.tvHistoryWordType = view.findViewById(R.id.tvHistoryWordType);
            viewHolder.tvHistoryExplanation = view.findViewById(R.id.tvHistoryExplanation);
            viewHolder.cvHistoryWordType = view.findViewById(R.id.cvHistoryWordType);
            viewHolder.ibFavoriteFromHistory = view.findViewById(R.id.ibFavoriteFromHistory);

            // Set tag
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final BookmarkWord historyWord = historyList.get(position);

        // Set text to word and its explanation
        viewHolder.tvHistoryWord.setText(historyWord.getDisplayWord());
        viewHolder.tvHistoryExplanation.setText(historyWord.getExplanations());

        // Set dictionary type
        if (historyWord.getDictionary_type() == Constants.ENG_POR) {
            viewHolder.tvHistoryWordType.setText("en");
            viewHolder.cvHistoryWordType.setCardBackgroundColor(context.getResources().getColor(R.color.colorGreen3));
        } else if (historyWord.getDictionary_type() == Constants.POR_ENG) {
            viewHolder.tvHistoryWordType.setText("pt");
            viewHolder.cvHistoryWordType.setCardBackgroundColor(context.getResources().getColor(R.color.colorBlue2));
        }

        BookmarkWord isFavoriteWord = databaseHelper.getFavorite(historyWord.getWordList_id());
        if (isFavoriteWord != null) {
            viewHolder.ibFavoriteFromHistory.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            viewHolder.ibFavoriteFromHistory.setImageResource(R.drawable.ic_favorite_border_24dp);
        }

        // Add/remove a word to/from Favorite
        viewHolder.ibFavoriteFromHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookmarkWord isFavorite = databaseHelper.getFavorite(historyWord.getWordList_id());

                if (isFavorite != null) {
                    viewHolder.ibFavoriteFromHistory.setImageResource(R.drawable.ic_favorite_border_24dp);
                    databaseHelper.deleteFavorite(historyWord.getWordList_id());

                    Toast.makeText(context,
                            historyWord.getDisplayWord()
                                    + context.getResources().getString(R.string.is_removed_from_favorite),
                            Toast.LENGTH_SHORT).show();
                } else {
                    viewHolder.ibFavoriteFromHistory.setImageResource(R.drawable.ic_favorite_red_24dp);
                    databaseHelper.insertFavorite(
                            historyWord.getWordList_id(),
                            historyWord.getDisplayWord(),
                            historyWord.getExplanations(),
                            historyWord.getDictionary_type());

                    Toast.makeText(context,
                            historyWord.getDisplayWord()
                                    + context.getResources().getString(R.string.is_added_to_favorite),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListener != null) {
                    itemListener.onItemClick(position);
                }
            }
        });

        return view;
    }

    /**
     * Get list of all recent words in History
     */
    public void getData() {
        historyList = databaseHelper.getAllHistory();
    }

    /**
     * Delete all words in History
     */
    public void deleteAll() {
        historyList.clear();
    }

    public void setOnItemListener(ListItemListener itemListener) {
        this.itemListener = itemListener;
    }

}
