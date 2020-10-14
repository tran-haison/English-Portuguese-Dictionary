package com.tranhaison.englishportugesedictionary.adapters.detailactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.tranhaison.englishportugesedictionary.fragments.DefinitionFragment;
import com.tranhaison.englishportugesedictionary.fragments.ExampleFragment;
import com.tranhaison.englishportugesedictionary.fragments.ExplanationFragment;
import com.tranhaison.englishportugesedictionary.fragments.SynonymFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to help manage all Fragments in DetailActivity
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    // Lists to hold Fragments and their correspond titles
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentListTitles = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentListTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentListTitles.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // Return POSITION_NONE means that "Fragment must be always recreated" when notifyDataSetChanged() is called
        return POSITION_NONE;
    }

    /**
     * Add new fragment to list of Fragments of DetailActivity
     * @param fragment
     * @param title
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentListTitles.add(title);
    }

    /**
     * Set new value to dictionary type and dictionary word
     * @param dictionary_type
     * @param wordList_id
     */
    public void updateData(int dictionary_type, int wordList_id, String current_word) {
        // Update all fragments
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof DefinitionFragment) {
                ((DefinitionFragment) fragment).update(dictionary_type, wordList_id);
            } else if (fragment instanceof ExplanationFragment) {
                ((ExplanationFragment) fragment).update(dictionary_type, wordList_id);
            } else if (fragment instanceof ExampleFragment) {
                ((ExampleFragment) fragment).updateCurrentWord(current_word);
                ((ExampleFragment) fragment).update(dictionary_type, wordList_id);
            } else if (fragment instanceof SynonymFragment) {
                ((SynonymFragment) fragment).update(dictionary_type, wordList_id);
            }
        }
    }

}
