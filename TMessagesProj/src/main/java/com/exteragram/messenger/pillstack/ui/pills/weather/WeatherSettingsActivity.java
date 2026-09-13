package com.exteragram.messenger.pillstack.ui.pills.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ClipRoundedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.Stories.recorder.Weather;

public class WeatherSettingsActivity extends BasePreferencesActivity {
    private FrameLayout addressContainer;
    private TextView addressText;
    private TLRPC.GeoPoint currentGeo;
    private ClipRoundedDrawable mapLoadingDrawable;
    private View mapMarker;
    private BackupImageView mapPreview;
    private FrameLayout mapPreviewContainer;

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean needHideTitle() {
        return true;
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.WeatherPill);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        TextView textView = new TextView(context);
        this.addressText = textView;
        textView.setTextSize(1, 14.0f);
        this.addressText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.addressText.setGravity(17);
        this.addressText.setPadding(0, 0, 0, 0);
        FrameLayout frameLayout = new FrameLayout(context);
        this.addressContainer = frameLayout;
        frameLayout.addView(this.addressText, LayoutHelper.createFrame(-1, -2.0f, 48, 21.0f, 15.0f, 21.0f, 15.0f));
        FrameLayout frameLayout2 = this.addressContainer;
        int i = Theme.key_windowBackgroundWhite;
        frameLayout2.setBackgroundColor(getThemedColor(i));
        this.mapPreview = new BackupImageView(context) { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity.1
            @Override // org.telegram.ui.Components.BackupImageView
            public ImageReceiver createImageReciever() {
                return new ImageReceiver(this) { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity.1.1
                    @Override // org.telegram.messenger.ImageReceiver
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i2, boolean z, int i3) {
                        if (drawable != null && i2 != 1) {
                            WeatherSettingsActivity.this.mapMarker.animate().alpha(1.0f).translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_BACK).setDuration(250L).start();
                        }
                        return super.setImageBitmapByKey(drawable, str, i2, z, i3);
                    }
                };
            }

            @Override // android.view.View
            public void onMeasure(int i2, int i3) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(240.0f), TLObject.FLAG_30));
            }

            @Override // org.telegram.ui.Components.BackupImageView, android.view.View
            public boolean verifyDrawable(Drawable drawable) {
                return drawable == WeatherSettingsActivity.this.mapLoadingDrawable || super.verifyDrawable(drawable);
            }
        };
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(R.raw.map_placeholder, Theme.key_chat_outLocationIcon, 0.2f);
        svgThumb.setColorKey(Theme.key_windowBackgroundWhiteBlackText, getResourceProvider());
        svgThumb.setAspectCenter(true);
        svgThumb.setParent(this.mapPreview.getImageReceiver());
        ClipRoundedDrawable clipRoundedDrawable = new ClipRoundedDrawable(svgThumb);
        this.mapLoadingDrawable = clipRoundedDrawable;
        clipRoundedDrawable.setCallback(this.mapPreview);
        this.mapPreview.setBackgroundColor(getThemedColor(i));
        this.mapMarker = new View(context) { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity.2
            final AvatarDrawable avatarDrawable;
            final ImageReceiver avatarImage;
            final Drawable pin = getContext().getResources().getDrawable(R.drawable.map_pin_photo).mutate();

            {
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                this.avatarDrawable = avatarDrawable;
                ImageReceiver imageReceiver = new ImageReceiver(this);
                this.avatarImage = imageReceiver;
                avatarDrawable.setInfo(WeatherSettingsActivity.this.getUserConfig().getCurrentUser());
                imageReceiver.setForUserOrChat(WeatherSettingsActivity.this.getUserConfig().getCurrentUser(), avatarDrawable);
            }

            @Override // android.view.View
            public void dispatchDraw(Canvas canvas) {
                this.pin.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(85.0f));
                this.pin.draw(canvas);
                this.avatarImage.setRoundRadius(AndroidUtilities.dp(62.0f));
                this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f));
                this.avatarImage.draw(canvas);
            }

            @Override // android.view.View
            public void onMeasure(int i2, int i3) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), TLObject.FLAG_30));
            }
        };
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.mapPreviewContainer = frameLayout3;
        frameLayout3.addView(this.mapPreview, LayoutHelper.createFrame(-1, -1.0f));
        this.mapPreviewContainer.addView(this.mapMarker, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, -31.0f, 0.0f, 0.0f));
        this.mapPreviewContainer.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WeatherSettingsActivity.this.lambda$createView$0(view);
            }
        });
        if (PillStackConfig.getCustomWeatherLocation() != null) {
            try {
                this.currentGeo = (TLRPC.GeoPoint) ExteraConfig.getGSON().fromJson(PillStackConfig.getCustomWeatherLocation(), TLRPC.TL_geoPoint.class);
            } catch (Exception unused) {
            }
        }
        updateMapPreview();
        return super.createView(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        openMapPicker();
    }

    private void updateMapPreview() {
        BackupImageView backupImageView;
        View view = this.mapMarker;
        if (view == null || (backupImageView = this.mapPreview) == null) {
            return;
        }
        if (this.currentGeo != null) {
            view.setAlpha(0.0f);
            this.mapMarker.setTranslationY(-AndroidUtilities.dp(12.0f));
            int measuredWidth = this.mapPreview.getMeasuredWidth() <= 0 ? AndroidUtilities.displaySize.x : this.mapPreview.getMeasuredWidth();
            float f = AndroidUtilities.density;
            int i = (int) (measuredWidth / f);
            int iMin = Math.min(2, (int) Math.ceil(f));
            BackupImageView backupImageView2 = this.mapPreview;
            TLRPC.GeoPoint geoPoint = this.currentGeo;
            backupImageView2.setImage(ImageLocation.getForWebFile(WebFile.createWithGeoPoint(geoPoint.lat, geoPoint._long, 0L, iMin * i, iMin * 240, 15, iMin)), i + "_240", this.mapLoadingDrawable, 0, (Object) null);
            TextView textView = this.addressText;
            if (textView != null) {
                textView.setText(PillStackConfig.getCustomWeatherAddress());
                return;
            }
            return;
        }
        backupImageView.setImageBitmap(null);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(getTitle(), LocaleController.getString(R.string.WeatherPillTopInfo), "RestrictedEmoji", "🌤"));
        arrayList.add(UItem.asButton(1, R.drawable.msg_settings_old, LocaleController.getString(R.string.PillStackPills)));
        arrayList.add(UItem.asShadow());
        if (PillStackConfig.getActivePills().contains(Integer.valueOf(PillType.WEATHER.getId()))) {
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.WeatherLocation)));
            arrayList.add(UItem.asRadio(2, LocaleController.getString(R.string.CurrentLocation)).setChecked(PillStackConfig.getUseCurrentLocation()));
            arrayList.add(UItem.asRadio(3, LocaleController.getString(R.string.SelectLocation)).setChecked(true ^ PillStackConfig.getUseCurrentLocation()));
            if (PillStackConfig.getUseCurrentLocation()) {
                arrayList.add(UItem.asShadow(LocaleController.getString(R.string.WeatherSettingsInfo)));
                if (!Weather.isLocationPermissionGranted()) {
                    arrayList.add(UItem.asButton(2, R.drawable.report, LocaleController.getString(R.string.WeatherLocationPermissionGrant)).red());
                    arrayList.add(UItem.asShadow(LocaleController.getString(R.string.WeatherLocationPermissionNo)));
                    return;
                } else {
                    if (Weather.isLocationEnabled()) {
                        return;
                    }
                    arrayList.add(UItem.asButton(4, R.drawable.filled_location, LocaleController.getString(R.string.WeatherLocationServicesEnable)).accent());
                    arrayList.add(UItem.asShadow(LocaleController.getString(R.string.GpsDisabledAlertText)));
                    return;
                }
            }
            arrayList.add(UItem.asCustom(5, this.mapPreviewContainer));
            if (!TextUtils.isEmpty(PillStackConfig.getCustomWeatherAddress())) {
                arrayList.add(UItem.asCustom(6, this.addressContainer));
            }
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.WeatherSettingsInfo)));
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 == 1) {
            presentFragment(new PillStackPreferencesActivity());
            return;
        }
        if (i2 == 2) {
            if (PillStackConfig.getUseCurrentLocation() && Weather.isLocationPermissionGranted()) {
                return;
            }
            Weather.getUserLocation(true, new Utilities.Callback() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity$$ExternalSyntheticLambda2
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    WeatherSettingsActivity.this.lambda$onClick$1((Location) obj);
                }
            });
            return;
        }
        if (i2 == 4) {
            Weather.getUserLocation(true, new Utilities.Callback() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity$$ExternalSyntheticLambda3
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    WeatherSettingsActivity.this.lambda$onClick$2((Location) obj);
                }
            });
            return;
        }
        if (i2 != 3) {
            if (i2 == 5 || i2 == 6) {
                openMapPicker();
                return;
            }
            return;
        }
        if (PillStackConfig.getUseCurrentLocation()) {
            if (this.currentGeo == null) {
                TLRPC.TL_geoPoint tL_geoPoint = new TLRPC.TL_geoPoint();
                this.currentGeo = tL_geoPoint;
                tL_geoPoint.lat = 55.7558d;
                tL_geoPoint._long = 37.6173d;
                PillStackConfig.setCustomWeatherLocation(ExteraConfig.getGSON().toJson(this.currentGeo));
            }
            updateLocationSetting(false);
        }
        openMapPicker();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$1(Location location) {
        if (location != null) {
            updateLocationSetting(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$2(Location location) {
        UniversalRecyclerView universalRecyclerView;
        UniversalAdapter universalAdapter;
        if (location == null || (universalRecyclerView = this.listView) == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    private void updateLocationSetting(boolean z) {
        PillStackConfig.setUseCurrentLocation(z);
        PillStackConfig.getEditor().putBoolean("useCurrentLocation", z).apply();
        PillStackConfig.notifySettingsChanged(PillType.WEATHER.getId());
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || universalRecyclerView.adapter == null) {
            return;
        }
        updateMapPreview();
        this.listView.adapter.update(true);
    }

    private void openMapPicker() {
        final LocationActivity locationActivity = new LocationActivity(8);
        if (this.currentGeo != null) {
            TLRPC.TL_channelLocation tL_channelLocation = new TLRPC.TL_channelLocation();
            tL_channelLocation.geo_point = this.currentGeo;
            tL_channelLocation.address = PillStackConfig.getCustomWeatherAddress();
            locationActivity.setInitialLocation(tL_channelLocation);
        }
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherSettingsActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.LocationActivity.LocationActivityDelegate
            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
                WeatherSettingsActivity.this.lambda$openMapPicker$3(locationActivity, messageMedia, i, z, i2, j);
            }
        });
        presentFragment(locationActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openMapPicker$3(LocationActivity locationActivity, TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
        this.currentGeo = messageMedia.geo;
        String addressName = locationActivity.getAddressName();
        if (addressName == null) {
            addressName = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        PillStackConfig.setCustomWeatherLocation(ExteraConfig.getGSON().toJson(this.currentGeo));
        PillStackConfig.setCustomWeatherAddress(addressName);
        PillStackConfig.getEditor().putString("customWeatherLocation", PillStackConfig.getCustomWeatherLocation()).putString("customWeatherAddress", PillStackConfig.getCustomWeatherAddress());
        updateLocationSetting(false);
    }
}
