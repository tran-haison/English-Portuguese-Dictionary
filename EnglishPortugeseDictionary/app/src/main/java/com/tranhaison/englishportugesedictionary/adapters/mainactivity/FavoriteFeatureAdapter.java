package com.tranhaison.englishportugesedictionary.adapters.mainactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.bookmarks.BookmarkWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class FavoriteFeatureAdapter extends RecyclerView.Adapter<FavoriteFeatureAdapter.FavoriteFeatureViewHolder> {

    // Init variables
    private ArrayList<BookmarkWord> favoriteFeatureList;
    private ListItemListener itemListener;

    public FavoriteFeatureAdapter(ArrayList<BookmarkWord> favoriteFeatureList) {
        this.favoriteFeatureList = favoriteFeatureList;
    }

    public void setRecyclerViewItemListener(ListItemListener itemListener) {
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public FavoriteFeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_feature_layout_item, parent, false);
        FavoriteFeatureViewHolder favoriteFeatureViewHolder = new FavoriteFeatureViewHolder(view);
        return favoriteFeatureViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFeatureViewHolder holder, final int position) {
        BookmarkWord favoriteWord = favoriteFeatureList.get(position);

        holder.tvFavoriteFeatureWord.setText(favoriteWord.getDisplayWord());
        holder.tvFavoriteFeatureDefinition.setText(favoriteWord.getExplanations());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListener != null) {
                    itemListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (favoriteFeatureList != null) {
            return favoriteFeatureList.size();
        } else {
            return 0;
        }
    }

    public void resetFavoriteFeatureList(ArrayList<BookmarkWord> favoriteFeatureList) {
        try {
            this.favoriteFeatureList = favoriteFeatureList;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static class FavoriteFeatureViewHolder extends RecyclerView.ViewHolder {

        TextView tvFavoriteFeatureWord, tvFavoriteFeatureDefinition;

        public FavoriteFeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            // Mapping
            tvFavoriteFeatureWord = itemView.findViewById(R.id.tvFavoriteFeatureWord);
            tvFavoriteFeatureDefinition = itemView.findViewById(R.id.tvFavoriteFeatureDefinition);
        }
    }
}
