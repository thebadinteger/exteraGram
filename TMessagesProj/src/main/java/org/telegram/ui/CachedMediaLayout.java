package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.HlsSegmentFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell2;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.NestedSizeNotifierLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Storage.CacheModel;
import org.telegram.ui.Stories.StoriesListPlaceProvider;

public abstract class CachedMediaLayout extends FrameLayout implements NestedSizeNotifierLayout.ChildLayout {
    private final LinearLayout actionModeLayout;
    private final ArrayList<View> actionModeViews;
    Page[] allPages;
    private final BackDrawable backDrawable;
    private int bottomPadding;
    CacheModel cacheModel;
    private final ActionBarMenuItem clearItem;
    private final ImageView closeButton;
    Delegate delegate;
    private final View divider;
    ArrayList<Page> pages;
    BaseFragment parentFragment;
    BasePlaceProvider placeProvider;
    public final AnimatedTextView selectedMessagesCountTextView;
    private final ViewPagerFixed.TabsView tabs;
    ViewPagerFixed viewPagerFixed;

    public interface Delegate {
        void clear();

        void clearSelection();

        default void dismiss() {
        }

        void onItemSelected(CacheControlActivity.DialogFileEntities dialogFileEntities, CacheModel.FileInfo fileInfo, boolean z);
    }

    @Override // org.telegram.ui.Components.NestedSizeNotifierLayout.ChildLayout
    public boolean isAttached() {
        return true;
    }

    public void showActionMode(boolean z) {
    }

    public CachedMediaLayout(Context context, BaseFragment baseFragment) {
        super(context);
        this.actionModeViews = new ArrayList<>();
        this.pages = new ArrayList<>();
        Page[] pageArr = new Page[5];
        this.allPages = pageArr;
        this.parentFragment = baseFragment;
        pageArr[0] = new Page(LocaleController.getString(R.string.FilterChats), 0, new DialogsAdapter());
        this.allPages[1] = new Page(LocaleController.getString(R.string.MediaTab), 1, new MediaAdapter(false));
        this.allPages[2] = new Page(LocaleController.getString(R.string.SharedFilesTab2), 2, new DocumentsAdapter());
        this.allPages[3] = new Page(LocaleController.getString(R.string.Music), 3, new MusicAdapter());
        int i = 0;
        while (true) {
            Page[] pageArr2 = this.allPages;
            if (i < pageArr2.length) {
                Page page = pageArr2[i];
                if (page != null) {
                    this.pages.add(i, page);
                }
                i++;
            } else {
                ViewPagerFixed viewPagerFixed = new ViewPagerFixed(getContext());
                this.viewPagerFixed = viewPagerFixed;
                viewPagerFixed.setAllowDisallowInterceptTouch(false);
                addView(this.viewPagerFixed, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 48.0f, 0.0f, 0.0f));
                ViewPagerFixed.TabsView tabsViewCreateTabsView = this.viewPagerFixed.createTabsView(true, 3);
                this.tabs = tabsViewCreateTabsView;
                addView(tabsViewCreateTabsView, LayoutHelper.createFrame(-1, 48.0f));
                View view = new View(getContext()) { // from class: org.telegram.ui.CachedMediaLayout.1
                    @Override // android.view.View
                    public void onDraw(Canvas canvas) {
                        canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
                    }
                };
                this.divider = view;
                addView(view, LayoutHelper.createFrame(-1, 1.0f, 0, 0.0f, 48.0f, 0.0f, 0.0f));
                view.getLayoutParams().height = 1;
                this.viewPagerFixed.setAdapter(new AnonymousClass2(context, baseFragment));
                LinearLayout linearLayout = new LinearLayout(context);
                this.actionModeLayout = linearLayout;
                linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                linearLayout.setAlpha(0.0f);
                linearLayout.setClickable(true);
                addView(linearLayout, LayoutHelper.createFrame(-1, 48.0f));
                AndroidUtilities.updateViewVisibilityAnimated(linearLayout, false, 1.0f, false);
                ImageView imageView = new ImageView(context);
                this.closeButton = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                BackDrawable backDrawable = new BackDrawable(true);
                this.backDrawable = backDrawable;
                imageView.setImageDrawable(backDrawable);
                int i2 = Theme.key_actionBarActionModeDefaultIcon;
                backDrawable.setColor(Theme.getColor(i2));
                int i3 = Theme.key_actionBarActionModeDefaultSelector;
                imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor(i3), 1));
                imageView.setContentDescription(LocaleController.getString(R.string.Close));
                linearLayout.addView(imageView, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
                this.actionModeViews.add(imageView);
                imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CachedMediaLayout$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.lambda$new$0(view2);
                    }
                });
                AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
                this.selectedMessagesCountTextView = animatedTextView;
                animatedTextView.setTextSize(AndroidUtilities.dp(18.0f));
                animatedTextView.setTypeface(AndroidUtilities.bold());
                animatedTextView.setTextColor(Theme.getColor(i2));
                linearLayout.addView(animatedTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
                this.actionModeViews.add(animatedTextView);
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor(i3), Theme.getColor(i2), false);
                this.clearItem = actionBarMenuItem;
                actionBarMenuItem.setIcon(R.drawable.msg_clear);
                actionBarMenuItem.setContentDescription(LocaleController.getString(R.string.Delete));
                actionBarMenuItem.setDuplicateParentStateEnabled(false);
                linearLayout.addView(actionBarMenuItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
                this.actionModeViews.add(actionBarMenuItem);
                actionBarMenuItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CachedMediaLayout$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.lambda$new$1(view2);
                    }
                });
                return;
            }
        }
    }

    public class AnonymousClass2 extends ViewPagerFixed.Adapter {
        private ActionBarPopupWindow popupWindow;
        final void lambda$createView$0(ItemInner itemInner, BaseAdapter baseAdapter, RecyclerListView recyclerListView, View view, View view2) {
            CachedMediaLayout.this.openPhoto(itemInner, (MediaAdapter) baseAdapter, recyclerListView, (SharedPhotoVideoCell2) view);
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        public void lambda$createView$4(ItemInner itemInner, View view) {
            Delegate delegate = CachedMediaLayout.this.delegate;
            if (delegate != null) {
                delegate.onItemSelected(itemInner.entities, itemInner.file, true);
            }
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }

        @Override // org.telegram.ui.Components.ViewPagerFixed.Adapter
        public void bindView(View view, int i, int i2) {
            RecyclerListView recyclerListView = (RecyclerListView) view;
            recyclerListView.setAdapter(CachedMediaLayout.this.pages.get(i).adapter);
            if (CachedMediaLayout.this.pages.get(i).type == 1 || CachedMediaLayout.this.pages.get(i).type == 4) {
                recyclerListView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            } else {
                recyclerListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            }
            recyclerListView.setTag(Integer.valueOf(CachedMediaLayout.this.pages.get(i).type));
        }
    }

    public public /* synthetic */ void lambda$checkMessageObjectForAudio$3(final CacheModel.FileInfo fileInfo, final TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio) throws Throwable {
        Throwable th;
        String str;
        final String str2;
        String strExtractMetadata = _UrlKt.FRAGMENT_ENCODE_SET;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            try {
                MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                try {
                    try {
                        mediaMetadataRetriever2.setDataSource(getContext(), Uri.fromFile(fileInfo.file));
                        String strExtractMetadata2 = mediaMetadataRetriever2.extractMetadata(7);
                        try {
                            strExtractMetadata = mediaMetadataRetriever2.extractMetadata(2);
                            try {
                                mediaMetadataRetriever2.release();
                            } catch (Throwable unused) {
                            }
                            str2 = strExtractMetadata2;
                        } catch (Exception e) {
                            e = e;
                            str = strExtractMetadata2;
                            mediaMetadataRetriever = mediaMetadataRetriever2;
                            FileLog.e(e);
                            if (mediaMetadataRetriever != null) {
                                try {
                                    mediaMetadataRetriever.release();
                                } catch (Throwable unused2) {
                                }
                            }
                            str2 = str;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                        str = _UrlKt.FRAGMENT_ENCODE_SET;
                        FileLog.e(e);
                        if (mediaMetadataRetriever != null) {
                            mediaMetadataRetriever.release();
                        }
                        str2 = str;
                        final String str3 = strExtractMetadata;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CachedMediaLayout$$ExternalSyntheticLambda3
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$checkMessageObjectForAudio$2(fileInfo, tL_documentAttributeAudio, str2, str3);
                            }
                        });
                    }
                } catch (Throwable th2) {
                    th = th2;
                    mediaMetadataRetriever = mediaMetadataRetriever2;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                            throw th;
                        } catch (Throwable unused3) {
                            throw th;
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e3) {
            e = e3;
        }
        final String str4 = strExtractMetadata;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CachedMediaLayout$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkMessageObjectForAudio$2(fileInfo, tL_documentAttributeAudio, str2, str4);
            }
        });
    }

    public /* synthetic */ void lambda$checkMessageObjectForAudio$2(CacheModel.FileInfo fileInfo, TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio, String str, String str2) {
        CacheModel.FileInfo.FileMetadata fileMetadata = fileInfo.metadata;
        fileMetadata.loading = false;
        fileMetadata.title = str;
        tL_documentAttributeAudio.title = str;
        fileMetadata.author = str2;
        tL_documentAttributeAudio.performer = str2;
        updateRow(fileInfo, 3);
    }

    private void updateRow(CacheModel.FileInfo fileInfo, int i) {
        for (int i2 = 0; i2 < this.viewPagerFixed.getViewPages().length; i2++) {
            RecyclerListView recyclerListView = (RecyclerListView) this.viewPagerFixed.getViewPages()[i2];
            if (recyclerListView != null && ((BaseAdapter) recyclerListView.getAdapter()).type == i) {
                BaseAdapter baseAdapter = (BaseAdapter) recyclerListView.getAdapter();
                for (int i3 = 0; i3 < baseAdapter.itemInners.size(); i3++) {
                    if (baseAdapter.itemInners.get(i3).file == fileInfo) {
                        baseAdapter.notifyItemChanged(i3);
                        break;
                    }
                }
            }
        }
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public class BasePlaceProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        RecyclerListView recyclerListView;

        private BasePlaceProvider() {
        }

        public void setRecyclerListView(RecyclerListView recyclerListView) {
            this.recyclerListView = recyclerListView;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
            SharedPhotoVideoCell2 cellForIndex = CachedMediaLayout.this.getCellForIndex(i);
            if (cellForIndex == null) {
                return null;
            }
            int[] iArr = new int[2];
            cellForIndex.getLocationInWindow(iArr);
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1];
            placeProviderObject.parentView = this.recyclerListView;
            ImageReceiver imageReceiver = cellForIndex.imageReceiver;
            placeProviderObject.imageReceiver = imageReceiver;
            placeProviderObject.thumb = imageReceiver.getBitmapSafe();
            placeProviderObject.scale = cellForIndex.getScaleX();
            return placeProviderObject;
        }
    }

    public class CacheCell extends FrameLayout {
        CheckBox2 checkBox;
        FrameLayout container;
        boolean drawDivider;
        TextView sizeTextView;
        int type;

        public abstract void onCheckBoxPressed();

        public CacheCell(Context context) {
            super(context);
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setDrawBackgroundAsArc(14);
            this.checkBox.setColor(Theme.key_checkbox, Theme.key_radioBackground, Theme.key_checkboxCheck);
            View view = new View(getContext());
            view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CachedMediaLayout$CacheCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.lambda$new$0(view2);
                }
            });
            this.container = new FrameLayout(context);
            TextView textView = new TextView(context);
            this.sizeTextView = textView;
            textView.setTextSize(1, 16.0f);
            this.sizeTextView.setGravity(5);
            this.sizeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            boolean z = LocaleController.isRTL;
            CheckBox2 checkBox3 = this.checkBox;
            if (z) {
                addView(checkBox3, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 0.0f, 18.0f, 0.0f));
                addView(view, LayoutHelper.createFrame(40, 40.0f, 21, 0.0f, 0.0f, 0.0f, 0.0f));
                addView(this.container, LayoutHelper.createFrame(-1, -2.0f, 0, 90.0f, 0.0f, 40.0f, 0.0f));
                addView(this.sizeTextView, LayoutHelper.createFrame(69, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
                return;
            }
            addView(checkBox3, LayoutHelper.createFrame(24, 24.0f, 19, 18.0f, 0.0f, 0.0f, 0.0f));
            addView(view, LayoutHelper.createFrame(40, 40.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
            addView(this.container, LayoutHelper.createFrame(-1, -2.0f, 0, 48.0f, 0.0f, 90.0f, 0.0f));
            addView(this.sizeTextView, LayoutHelper.createFrame(69, -2.0f, 21, 0.0f, 0.0f, 21.0f, 0.0f));
        }

        public /* synthetic */ void lambda$new$0(View view) {
            onCheckBoxPressed();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.drawDivider) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(48.0f), getMeasuredHeight() - 1, Theme.dividerPaint);
                } else {
                    canvas.drawLine(getMeasuredWidth() - AndroidUtilities.dp(90.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
                }
            }
        }
    }

    public static boolean fileIsMedia(File file) {
        String lowerCase = file.getName().toLowerCase();
        return file.getName().endsWith("mp4") || file.getName().endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif");
    }
}
