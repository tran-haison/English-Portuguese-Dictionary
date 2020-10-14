package com.tranhaison.englishportugesedictionary.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.tranhaison.englishportugesedictionary.R;

import java.util.ArrayList;
import java.util.List;

public class AdsManager {

    // Facebook ads
    private static NativeBannerAd nativeBannerAd;
    private static LinearLayout adView;
    private static final String FB_AD_PLACEMENT_ID = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

    // Google ads
    private static UnifiedNativeAd ggUnifiedNativeAd;
    private static final String GG_UNIFIED_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    private static InterstitialAd ggInterstitialAd;
    private static final String GG_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    /**
     * Instantiate a NativeBannerAd object.
     * NOTE: the placement ID will eventually identify this as your App, you can ignore it for
     * now, while you are testing and replace it later when you have signed up.
     * While you are using this temporary code you will only get test ads and if you release
     * your code like this to the Google Play your users will not receive ads (you will get a no fill error).
     **/
    public static void createFacebookNativeAd(final Activity activity, final NativeAdLayout nativeAdLayout, final CardView ggUnifiedAdContainer) {

        nativeBannerAd = new NativeBannerAd(activity, FB_AD_PLACEMENT_ID);
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load

                // Create gg unified ad instead
                createGoogleUnifiedAd(activity, ggUnifiedAdContainer);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }

                // Inflate Native Banner Ad into Container
                inflateFacebookNativeAd(nativeBannerAd, activity, nativeAdLayout);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        };

        // Load the ad
        nativeBannerAd.loadAd(
                nativeBannerAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    /**
     * Instantiate Ad view and set text to corresponding view
     *
     * @param nativeBannerAd
     * @param context
     */
    private static void inflateFacebookNativeAd(NativeBannerAd nativeBannerAd, Context context, NativeAdLayout nativeAdLayout) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        LayoutInflater inflater = LayoutInflater.from(context);
        adView = (LinearLayout) inflater.inflate(R.layout.facebook_native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.fb_ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.fb_native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.fb_native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.fb_native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.fb_native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.fb_native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.GONE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    public static void createGoogleUnifiedAd(final Activity activity, final CardView gg_ad_container) {
        final AdLoader adLoader = new AdLoader.Builder(activity, GG_UNIFIED_AD_UNIT_ID)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.

                        if (ggUnifiedNativeAd == null) {
                            ggUnifiedNativeAd = unifiedNativeAd;
                        }

                        UnifiedNativeAdView adView = (UnifiedNativeAdView) activity.getLayoutInflater().inflate(R.layout.google_unified_ad_layout, null);
                        inflateGoogleUnifiedAd(ggUnifiedNativeAd, adView);

                        gg_ad_container.removeAllViews();
                        gg_ad_container.addView(adView);

                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }

                    @Override
                    public void onAdLoaded() {
                        // Set ad visible
                        gg_ad_container.setVisibility(View.VISIBLE);

                        super.onAdLoaded();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private static void inflateGoogleUnifiedAd(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        // Set view to View Group
        adView.setIconView(adView.findViewById(R.id.gg_ad_icon));
        adView.setHeadlineView(adView.findViewById(R.id.gg_ad_headline));
        adView.setAdvertiserView(adView.findViewById(R.id.gg_ad_advertiser));
        adView.setBodyView(adView.findViewById(R.id.gg_ad_body_text));
        adView.setStarRatingView(adView.findViewById(R.id.gg_ad_star_rating));
        adView.setMediaView((com.google.android.gms.ads.formats.MediaView) adView.findViewById(R.id.gg_ad_media_view));
        adView.setCallToActionView(adView.findViewById(R.id.gg_ad_call_to_action));

        // Set content to views
        try {
            if (nativeAd.getHeadline() == null) {
                adView.getHeadlineView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
                adView.getHeadlineView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                adView.getBodyView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.GONE);
            } else {
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);

                if (nativeAd.getMediaContent() == null) {
                    adView.getMediaView().setVisibility(View.GONE);
                } else {
                    adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
                    adView.getMediaView().setVisibility(View.VISIBLE);
                }

            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.GONE);
            } else {
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
                adView.getCallToActionView().setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        adView.setNativeAd(nativeAd);
    }

    public static void destroyGoogleUnifiedAd() {
        if (ggUnifiedNativeAd != null) {
            ggUnifiedNativeAd.destroy();
        }
    }

    public static void createGoogleInterstitialAd(final Activity activity) {
        ggInterstitialAd = new InterstitialAd(activity);
        ggInterstitialAd.setAdUnitId(GG_INTERSTITIAL_AD_UNIT_ID);
        ggInterstitialAd.loadAd(new AdRequest.Builder().build());

        ggInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                activity.finish();
                ggInterstitialAd.loadAd(new AdRequest.Builder().build());
                //super.onAdClosed();
            }
        });
    }

    public static void showGoogleInterstitialAd(Activity activity) {
        if (ggInterstitialAd.isLoaded()) {
            ggInterstitialAd.show();
        } else {
            activity.finish();
        }
    }

}
