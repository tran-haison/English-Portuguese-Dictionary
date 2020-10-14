package com.tranhaison.englishportugesedictionary.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.network.NetworkUtil;

public class CountryRegion {

    // Init variables
    private static final String COUNTRY_PORTUGAL = "pt";
    private static final String COUNTRY_BRAZIL = "br";
    private static int country_flag_icon;

    public static void setCountryFlagIcon(Context context) {
        if (getCountryCode(context).equalsIgnoreCase(COUNTRY_BRAZIL)) {
            CountryRegion.country_flag_icon = R.drawable.img_brazil_flag;
        } else {
            CountryRegion.country_flag_icon = R.drawable.img_portugal_flag;
        }
    }

    public static int getCountryFlagIcon() {
        return country_flag_icon;
    }

    /**
     * Get country code using the Phone's language configuration
     * @param context
     * @return
     */
    public static String getCountryPhoneConfiguration(Context context) {
        String locale = context.getResources().getConfiguration().locale.getCountry();
        return locale;
    }

    /**
     * Get country location using SIM card or Wifi (with internet connection)
     * @param context
     * @return
     */
    public static String getCountryLocation(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        return countryCodeValue;
    }

    /**
     * If user has internet connection -> get country based on location
     * otherwise get country configuration on device
     * @param context
     * @return
     */
    public static String getCountryCode(Context context) {
        if (NetworkUtil.isNetworkConnected(context)) {
            return getCountryLocation(context);
        } else {
            return getCountryPhoneConfiguration(context);
        }
    }

}
