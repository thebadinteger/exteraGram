package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dx.AppDataDirGuesser;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Consumer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.PaddedListAdapter;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.PhotoViewer;
import org.webrtc.MediaStreamTrack;

public abstract class MentionsContainerView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private MentionsAdapter adapter;
    private int animationIndex;
    private BlurredBackgroundDrawable backgroundDrawable;
    WeakReference<BaseFragment> baseFragment;
    private PhotoViewer.PhotoViewerProvider botContextProvider;
    private ArrayList<Object> botContextResults;
    private final RectF clipBounds;
    private final Path clipPath;
    private Integer color;
    private float containerBottom;
    private float containerPadding;
    private float containerTop;
    private WeakReference<Delegate> delegate;
    private ExtendedGridLayoutManager gridLayoutManager;
    private float hideT;
    private boolean ignoreLayout;
    private LinearLayoutManager linearLayoutManager;
    private MentionsListView listView;
    private boolean listViewHiding;
    private float listViewPadding;
    private SpringAnimation listViewTranslationAnimator;
    private RecyclerListView.OnItemClickListener mentionsOnItemClickListener;
    private PaddedListAdapter paddedAdapter;
    private Paint paint;
    private Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private int scrollRangeUpdateTries;
    private boolean scrollToFirst;
    private boolean shouldLiftMentions;
    private boolean shown;
    private boolean switchLayoutManagerOnEnd;
    private Runnable updateVisibilityRunnable;

    public interface Delegate {
        default void addEmojiToRecent(String str) {
        }

        Paint.FontMetricsInt getFontMetrics();

        default void onStickerSelected(TLRPC.TL_document tL_document, String str, Object obj) {
        }

        void replaceText(int i, int i2, CharSequence charSequence, boolean z);

        default void sendBotInlineResult(TLRPC.BotInlineResult botInlineResult, boolean z, int i) {
        }
    }

    public static void $r8$lambda$P0RBUas03aNl8uv9eCnt3Tgs_hs(View view) {
        if (view instanceof MentionCell) {
            ((MentionCell) view).invalidateEmojis();
        } else if (view instanceof QuickRepliesActivity.QuickReplyView) {
            ((QuickRepliesActivity.QuickReplyView) view).invalidateEmojis();
        } else {
            view.invalidate();
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        checkBackgroundBounds();
    }

    public void setBackgroundDrawable(BlurredBackgroundDrawable blurredBackgroundDrawable) {
        this.backgroundDrawable = blurredBackgroundDrawable;
        blurredBackgroundDrawable.setRadius(AndroidUtilities.dp(22.0f));
        this.backgroundDrawable.setPadding(AndroidUtilities.dp(5.0f));
        checkListViewPadding();
    }

    public void checkBackgroundBounds() {
        if (this.listView == null || this.linearLayoutManager == null) {
            return;
        }
        boolean zIsReversed = isReversed();
        this.containerPadding = 0.0f;
        PaddedListAdapter paddedListAdapter = this.paddedAdapter;
        if (zIsReversed) {
            float fMin = Math.min(Math.max(0.0f, (paddedListAdapter.paddingViewAttached ? paddedListAdapter.paddingView.getTop() : getHeight()) + this.listView.getTranslationY()) + this.containerPadding, (1.0f - this.hideT) * getHeight());
            this.containerTop = 0.0f;
            this.containerBottom = fMin;
        } else {
            this.containerTop = Math.max(Math.max(0.0f, (paddedListAdapter.paddingViewAttached ? paddedListAdapter.paddingView.getBottom() : 0) + this.listView.getTranslationY()) - this.containerPadding, this.hideT * getHeight());
            this.containerBottom = getMeasuredHeight();
        }
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.backgroundDrawable;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.setBounds(0, ((int) this.containerTop) - AndroidUtilities.dp(5.0f), getMeasuredWidth(), ((int) this.containerBottom) + AndroidUtilities.dp(5.0f));
            this.clipPath.rewind();
            this.clipBounds.set(this.backgroundDrawable.getPaddedBounds());
            if (isGif()) {
                this.clipBounds.inset(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                this.clipPath.addRoundRect(this.clipBounds, AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), Path.Direction.CW);
            } else {
                this.clipPath.addRoundRect(this.clipBounds, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f), Path.Direction.CW);
            }
            this.clipPath.close();
            invalidate();
        }
    }

    private boolean isGif() {
        MentionsAdapter mentionsAdapter;
        MentionsListView mentionsListView = this.listView;
        return (mentionsListView == null || this.gridLayoutManager == null || mentionsListView.getLayoutManager() != this.gridLayoutManager || (mentionsAdapter = this.adapter) == null || !mentionsAdapter.isBotContext()) ? false : true;
    }

    private void checkListViewPadding() {
        if (this.listView == null || this.linearLayoutManager == null) {
            return;
        }
        boolean zIsGif = isGif();
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.backgroundDrawable;
        MentionsListView mentionsListView = this.listView;
        if (blurredBackgroundDrawable == null) {
            mentionsListView.setPadding(0, 0, 0, 0);
        } else {
            mentionsListView.setPadding(AndroidUtilities.dp(zIsGif ? 7.0f : 5.0f), zIsGif ? AndroidUtilities.dp(2.0f) : 0, AndroidUtilities.dp(zIsGif ? 7.0f : 5.0f), zIsGif ? AndroidUtilities.dp(2.0f) : 0);
        }
    }
}
