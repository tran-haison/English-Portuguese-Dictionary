package com.tranhaison.englishportugesedictionary.adapters.detailactivity.fragment;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tranhaison.englishportugesedictionary.utils.Constants;
import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.utils.Utils;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.examples.ExampleSentence;
import com.tranhaison.englishportugesedictionary.dictionaryhelper.words.DictionaryWord;
import com.tranhaison.englishportugesedictionary.interfaces.ListItemListener;

import java.util.ArrayList;

public class ExampleAdapter extends BaseAdapter {

    // Init Fragment listener
    private ListItemListener itemListenerSpeaker;

    // Init variables
    private Context context;
    private ArrayList<ExampleSentence> exampleList;
    private String word;

    private int dictionary_type;
    private DatabaseHelper databaseHelper;
    private int wordList_id;

    public ExampleAdapter(Context context, ArrayList<ExampleSentence> exampleList, String word) {
        this.context = context;
        this.exampleList = exampleList;
        this.word = word;
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
        long item_id = 0;

        try {
            item_id = exampleList.get(i).get_id();
        } catch (IndexOutOfBoundsException e) {
        }

        return item_id;
    }

    private class ViewHolder {
        TextView tvEnglishSentence, tvTranslation, tvExampleItemNumber;
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
            viewHolder.tvExampleItemNumber = view.findViewById(R.id.tvExampleItemNumber);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Set text to text view
        ExampleSentence sentence = exampleList.get(i);
        viewHolder.tvTranslation.setText(sentence.getTranslation());
        viewHolder.tvExampleItemNumber.setText((i + 1) + ".");

        // Check dictionary_type and get new colored sentence based on it
        String new_sentence = null;
        if (dictionary_type == Constants.ENG_POR) {
            new_sentence = getNewEnglishExampleSentence(sentence.getEnglish_sentences());
        } else if (dictionary_type == Constants.POR_ENG) {
            new_sentence = getNewPortugueseExampleSentence(sentence.getEnglish_sentences());
        }

        // Set text to text view
        if (new_sentence != null) {
            viewHolder.tvEnglishSentence.setText(Html.fromHtml(new_sentence));
        }

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
     *
     * @param itemListenerSpeaker
     */
    public void setOnItemSpeakerClick(ListItemListener itemListenerSpeaker) {
        this.itemListenerSpeaker = itemListenerSpeaker;
    }

    public void setVariables(int dictionary_type, DatabaseHelper databaseHelper, int wordList_id) {
        this.dictionary_type = dictionary_type;
        this.databaseHelper = databaseHelper;
        this.wordList_id = wordList_id;
    }

    /**
     * Return a new English example sentence with color
     *
     * @return
     */
    public String getNewEnglishExampleSentence(String english_sentence) {
        ArrayList<String> grammarList = databaseHelper.getGrammars(wordList_id);

        if (english_sentence == null || english_sentence.isEmpty()) {
            return null;
        }

        if (grammarList == null) {
            english_sentence = Utils.getNewColoredSentence(word, english_sentence);
        } else {
            for (String grammar : grammarList) {
                english_sentence = Utils.getNewColoredSentence(grammar, english_sentence);
            }
        }

        return english_sentence;
    }

    /**
     * Return new example sentence with color word
     *
     * @return
     */
    public String getNewPortugueseExampleSentence(String english_sentence) {
        ArrayList<DictionaryWord> engRelatedWordList = databaseHelper.getEngRelatedWord(wordList_id);

        if (english_sentence == null || english_sentence.isEmpty()) {
            return null;
        }

        if (engRelatedWordList != null) {
            for (DictionaryWord dictionaryWord : engRelatedWordList) {
                String displayWord = dictionaryWord.getDisplayWord();
                english_sentence = Utils.getNewColoredSentence(displayWord, english_sentence);
            }
        }
        return english_sentence;
    }

}
