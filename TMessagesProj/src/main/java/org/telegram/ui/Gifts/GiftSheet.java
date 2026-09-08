package org.telegram.ui.Gifts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.export.ui.ExportMapper$$ExternalSyntheticLambda2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.Choreographer60FpsContent;
import org.telegram.messenger.utils.DrawableUtils;
import org.telegram.messenger.utils.tlutils.AmountUtils$Currency;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.AccountFrozenAlert;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BatchParticlesDrawHelper;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CompatDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EffectsTextView;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$GiftTier;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.Shaker;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.blur3.utils.NinePatchBuilder;
import org.telegram.ui.Components.chat.buttons.ChatActivityBlurredRoundButton$$ExternalSyntheticApiModelOutline0;
import org.telegram.ui.Components.chat.buttons.ChatActivityBlurredRoundButton$$ExternalSyntheticApiModelOutline1;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.ExplainStarsSheet;
import org.telegram.ui.Stars.StarGiftPatterns;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.bots.AffiliateProgramFragment;

public class GiftSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private int TAB_ALL;
    private int TAB_COLLECTIBLES;
    private int TAB_IN_STOCK;
    private int TAB_LIMITED;
    private int TAB_MY_GIFTS;
    private int TAB_RESALE;
    private UniversalAdapter adapter;
    private final StarsIntroActivity.StarsBalanceView balanceView;
    private boolean birthday;
    private final Runnable closeParentSheet;
    private final int currentAccount;
    private final long dialogId;
    private final DefaultItemAnimator itemAnimator;
    private final ExtendedGridLayoutManager layoutManager;
    private final StarsController.GiftsList myGifts;
    private final String name;
    private List<TLRPC.TL_premiumGiftCodeOption> options;
    private final FrameLayout premiumHeaderView;
    private final ArrayList<GiftPremiumBottomSheet$GiftTier> premiumTiers;
    private int selectedTab;
    private final boolean self;
    private boolean shownCollectiblesInfo;
    private final LinearLayout starsHeaderView;
    private final LinkSpanDrawable.LinksTextView subtitleCollectiblesStarsView;
    private final LinkSpanDrawable.LinksTextView subtitleStarsView;
    private final ArrayList<CharSequence> tabs;
    private final FrameLayout topView;
    private TLRPC.DisallowedGiftsSettings userSettings;

    public GiftSheet(Context context, int i, long j, Runnable runnable) {
        this(context, i, j, null, runnable);
    }

    public GiftSheet(final Context context, final int i, final long j, List<TLRPC.TL_premiumGiftCodeOption> list, final Runnable runnable) {
        int i2;
        super(context, null, false, false, false, null);
        final Context context2 = context;
        this.premiumTiers = new ArrayList<>();
        this.TAB_ALL = -1;
        this.TAB_MY_GIFTS = -1;
        this.TAB_LIMITED = -1;
        this.TAB_IN_STOCK = -1;
        this.TAB_RESALE = -1;
        this.TAB_COLLECTIBLES = -1;
        this.tabs = new ArrayList<>();
        this.currentAccount = i;
        this.dialogId = j;
        boolean z = UserConfig.getInstance(i).getClientUserId() == j;
        this.self = z;
        this.options = list;
        this.closeParentSheet = runnable;
        int i3 = Theme.key_dialogGiftsBackground;
        setBackgroundColor(Theme.getColor(i3));
        fixNavigationBar(Theme.getColor(i3));
        this.myGifts = StarsController.getInstance(i).getProfileGiftsList(UserConfig.getInstance(i).getClientUserId());
        StarsController.getInstance(i).loadStarGifts();
        BackupImageView backupImageView = new BackupImageView(context2);
        backupImageView.setImportantForAccessibility(2);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        if (j > 0) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
            this.name = UserObject.getForcedFirstName(user);
            avatarDrawable.setInfo(user);
            backupImageView.setForUserOrChat(user, avatarDrawable);
            TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j);
            this.userSettings = (j == UserConfig.getInstance(i).getClientUserId() || userFull == null) ? null : userFull.disallowed_stargifts;
            if (userFull == null) {
                MessagesController.getInstance(i).loadFullUser(user, 0, true);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
            this.name = chat == null ? _UrlKt.FRAGMENT_ENCODE_SET : chat.title;
            avatarDrawable.setInfo(chat);
            backupImageView.setForUserOrChat(chat, avatarDrawable);
        }
        this.topPadding = 0.1f;
        StarsIntroActivity.StarsBalanceView starsBalanceView = new StarsIntroActivity.StarsBalanceView(context2, i, this.resourcesProvider);
        this.balanceView = starsBalanceView;
        ScaleStateListAnimator.apply(starsBalanceView);
        starsBalanceView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$0(view);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.premiumHeaderView = frameLayout;
        FrameLayout frameLayout2 = new FrameLayout(context2) { // from class: org.telegram.ui.Gifts.GiftSheet.1
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i4, int i5) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i4), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), TLObject.FLAG_30));
            }
        };
        this.topView = frameLayout2;
        frameLayout2.setClipChildren(false);
        frameLayout2.setClipToPadding(false);
        frameLayout2.addView(StarsIntroActivity.makeParticlesView(context2, 70, 0), LayoutHelper.createFrame(-1, -1.0f));
        backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(84.0f));
        frameLayout2.addView(backupImageView, LayoutHelper.createFrame(84, 84.0f, 17, 0.0f, 15.0f, 0.0f, 17.0f));
        ScaleStateListAnimator.apply(backupImageView);
        backupImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$1(j, view);
            }
        });
        frameLayout2.addView(starsBalanceView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, -3.0f, -10.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -2, 55));
        TextView textView = new TextView(context2);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i4 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i4, this.resourcesProvider));
        textView.setGravity(17);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 4, 0, 4, 0));
        textView.setMaxWidth(HintView2.cutInFancyHalf(textView.getText(), textView.getPaint()));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context2, this.resourcesProvider);
        int i5 = Theme.key_chat_messageLinkIn;
        linksTextView.setLinkTextColor(Theme.getColor(i5, this.resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setTextColor(Theme.getColor(i4, this.resourcesProvider));
        linksTextView.setGravity(17);
        linksTextView.setLineSpacing(AndroidUtilities.dp(2.33f), 1.0f);
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 1, 4, 4, 4, 12));
        textView.setText(LocaleController.getString(R.string.Gift2Premium));
        linksTextView.setText(TextUtils.concat(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2PremiumInfo, this.name)), " ", AndroidUtilities.replaceArrows(AndroidUtilities.makeClickable(LocaleController.getString(R.string.Gift2PremiumInfoLink), new Runnable() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GiftSheet.$r8$lambda$HSCej1TuvnAyxiD8uWvIfr9SGf0();
            }
        }), true)));
        linksTextView.setMaxWidth(HintView2.cutInFancyHalf(linksTextView.getText(), linksTextView.getPaint()));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.starsHeaderView = linearLayout2;
        linearLayout2.setOrientation(1);
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(1, 20.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setTextColor(Theme.getColor(i4, this.resourcesProvider));
        textView2.setGravity(17);
        linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 4, 0, 4, 0));
        LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context2, this.resourcesProvider) { // from class: org.telegram.ui.Gifts.GiftSheet.2
            @Override // android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (getAlpha() < 0.95f) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.subtitleStarsView = linksTextView2;
        linksTextView2.setLinkTextColor(Theme.getColor(i5, this.resourcesProvider));
        linksTextView2.setTextSize(1, 14.0f);
        linksTextView2.setTextColor(Theme.getColor(i4, this.resourcesProvider));
        linksTextView2.setGravity(17);
        LinkSpanDrawable.LinksTextView linksTextView3 = new LinkSpanDrawable.LinksTextView(context2, this.resourcesProvider) { // from class: org.telegram.ui.Gifts.GiftSheet.3
            @Override // android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (getAlpha() < 0.95f) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.subtitleCollectiblesStarsView = linksTextView3;
        linksTextView3.setLinkTextColor(Theme.getColor(i5, this.resourcesProvider));
        linksTextView3.setTextSize(1, 14.0f);
        linksTextView3.setTextColor(Theme.getColor(i4, this.resourcesProvider));
        linksTextView3.setGravity(17);
        linksTextView3.setAlpha(0.0f);
        linksTextView3.setScaleX(0.85f);
        linksTextView3.setScaleY(0.85f);
        FrameLayout frameLayout3 = new FrameLayout(context2);
        frameLayout3.addView(linksTextView2, LayoutHelper.createFrame(-1, -2.0f, 49, 26.0f, 0.0f, 26.0f, 0.0f));
        frameLayout3.addView(linksTextView3, LayoutHelper.createFrame(-1, -2.0f, 49, 26.0f, 0.0f, 26.0f, 0.0f));
        if (j < 0) {
            i2 = R.string.Gift2StarsChannel;
        } else {
            i2 = z ? R.string.Gift2StarsSelf : R.string.Gift2Stars;
        }
        textView2.setText(LocaleController.getString(i2));
        if (z) {
            linearLayout2.addView(frameLayout3, LayoutHelper.createLinear(-2, -2, 1, 0, 9, 0, 4));
            LinkSpanDrawable.LinksTextView linksTextView4 = new LinkSpanDrawable.LinksTextView(context2, this.resourcesProvider);
            linksTextView4.setLinkTextColor(Theme.getColor(i5, this.resourcesProvider));
            linksTextView4.setTextSize(1, 14.0f);
            linksTextView4.setTextColor(Theme.getColor(i4, this.resourcesProvider));
            linksTextView4.setGravity(17);
            linearLayout2.addView(linksTextView4, LayoutHelper.createLinear(-2, -2, 1, 26, 4, 26, 6));
            linksTextView2.setText(LocaleController.getString(R.string.Gift2StarsSelfInfo1));
            linksTextView4.setText(LocaleController.getString(R.string.Gift2StarsSelfInfo2));
        } else if (j < 0) {
            linearLayout2.addView(frameLayout3, LayoutHelper.createLinear(-2, -2, 1, 0, 9, 0, 4));
            NotificationCenter.listenEmojiLoading(linksTextView2);
            linksTextView2.setText(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2StarsChannelInfo, this.name)), linksTextView2.getPaint().getFontMetricsInt(), false));
        } else {
            linearLayout2.addView(frameLayout3, LayoutHelper.createLinear(-1, -2, 1, 0, 9, 0, 6));
            final StarsController.GiftsList profileGiftsList = StarsController.getInstance(i).getProfileGiftsList(j);
            final Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$5(profileGiftsList, j, runnable, context);
                }
            };
            context2 = context;
            runnable2.run();
            linksTextView2.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.Gifts.GiftSheet.4
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view) {
                    runnable2.run();
                }
            });
            if (profileGiftsList.gifts.size() < 3) {
                profileGiftsList.load();
            }
            NotificationCenter.getInstance(i).listen(linksTextView2, NotificationCenter.starUserGiftsLoaded, new Utilities.Callback() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda7
                @Override 
                public final void run(Object obj) {
                    GiftSheet.$r8$lambda$3dkE23YxP0eT28ZcAWx7zHxtNUw(profileGiftsList, runnable2, (Object[]) obj);
                }
            });
        }
        ExtendedGridLayoutManager extendedGridLayoutManager = new ExtendedGridLayoutManager(context2, 3);
        this.layoutManager = extendedGridLayoutManager;
        extendedGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Gifts.GiftSheet.5
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i6) {
                int i7;
                if (GiftSheet.this.adapter == null || i6 == 0) {
                    return GiftSheet.this.layoutManager.getSpanCount();
                }
                UItem item = GiftSheet.this.adapter.getItem(i6 - 1);
                return (item == null || (i7 = item.spanCount) == -1) ? GiftSheet.this.layoutManager.getSpanCount() : i7;
            }
        });
        this.recyclerListView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setClipChildren(false);
        this.recyclerListView.setLayoutManager(extendedGridLayoutManager);
        this.recyclerListView.setSelectorType(9);
        this.recyclerListView.setSelectorDrawableColor(0);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.Gifts.GiftSheet.6
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public float animateByScale(View view) {
                return 0.3f;
            }
        };
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayIncrement(40L);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i6) {
                this.f$0.lambda$new$19(context2, i, runnable, j, view, i6);
            }
        });
        updatePremiumTiers();
        this.adapter.update(false);
        updateTitle();
        if (BirthdayController.getInstance(i).isToday(j)) {
            setBirthday();
        }
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.starGiftsLoaded);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.starGiftSoldOut);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.starUserGiftsLoaded);
        this.actionBar.setTitle(getTitle());
        NotificationCenter.listenEmojiLoading(this.actionBar.getTitleTextView());
    }

    public void lambda$new$7(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        lambda$new$0();
    }

    public void lambda$new$9(Browser.Progress progress, Runnable runnable, final StarGiftSheet starGiftSheet, final TLRPC.TL_error tL_error) {
        progress.end();
        if (runnable != null) {
            runnable.run();
        }
        lambda$new$0();
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    starGiftSheet.getBulletinFactory().showForError(tL_error);
                }
            });
        } else {
            lambda$new$0();
        }
    }

    public static void lambda$updatePremiumTiers$22(BillingResult billingResult, List list) {
        int i;
        Iterator it = list.iterator();
        long pricePerMonth = 0;
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            ProductDetails productDetails = (ProductDetails) it.next();
            ArrayList<GiftPremiumBottomSheet$GiftTier> arrayList = this.premiumTiers;
            int size = arrayList.size();
            while (i < size) {
                GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier = arrayList.get(i);
                i++;
                GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier2 = giftPremiumBottomSheet$GiftTier;
                if (giftPremiumBottomSheet$GiftTier2.getStoreProduct() != null && giftPremiumBottomSheet$GiftTier2.getStoreProduct().equals(productDetails.getProductId())) {
                    giftPremiumBottomSheet$GiftTier2.setGooglePlayProductDetails(productDetails);
                    if (giftPremiumBottomSheet$GiftTier2.getPricePerMonth() <= pricePerMonth) {
                        break;
                    }
                    pricePerMonth = giftPremiumBottomSheet$GiftTier2.getPricePerMonth();
                    break;
                }
            }
        }
        ArrayList<GiftPremiumBottomSheet$GiftTier> arrayList2 = this.premiumTiers;
        int size2 = arrayList2.size();
        while (i < size2) {
            GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier3 = arrayList2.get(i);
            i++;
            giftPremiumBottomSheet$GiftTier3.setPricePerMonthRegular(pricePerMonth);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Gifts.GiftSheet$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updatePremiumTiers$21();
            }
        });
    }

    public boolean lambda$fillItems$24(TL_stars.StarGift starGift) {
        boolean z;
        if (starGift instanceof TL_stars.TL_starGiftUnique) {
            z = this.userSettings.disallow_unique_stargifts;
        } else {
            boolean z2 = starGift.limited;
            TLRPC.DisallowedGiftsSettings disallowedGiftsSettings = this.userSettings;
            if (z2) {
                return !disallowedGiftsSettings.disallow_limited_stargifts || (starGift.can_upgrade && !disallowedGiftsSettings.disallow_unique_stargifts);
            }
            z = disallowedGiftsSettings.disallow_unlimited_stargifts;
        }
        return !z;
    }

    public static void lambda$attach$0(Boolean bool) {
            checkParticlesAllowed();
        }

        public void detach() {
            if (this.isAttached) {
                this.isAttached = false;
                checkParticlesAllowed();
                LiteMode.removeOnPowerSaverAppliedListener(this.liteModeCallback);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            float fMin = Math.min(rect.width(), rect.height()) / 2.0f;
            this.rectF.set(rect);
            this.path.rewind();
            this.path.addRoundRect(this.rectF, fMin, fMin, Path.Direction.CW);
            StarsReactionsSheet.Particles particles = this.particles;
            if (particles != null) {
                particles.setBounds(this.rectF);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.backgroundPaint.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.backgroundPaint.setColorFilter(colorFilter);
        }
    }

    public static class SharedBackgroundDrawables {
        private Drawable filledNinePatch;
        private Drawable filledWithShadowNinePatch;
        private int lastFillingColor;
        private int lastFillingWithShadowFillingColor;
        private int lastFillingWithShadowShadowColor;
        private int lastShadowColor;
        private final float[] radii;
        private Drawable shadowNinePatch;
        private final Bitmap[] shadowNinePatchBitmap = new Bitmap[1];
        private final Bitmap[] filledNinePatchBitmap = new Bitmap[1];
        private final Bitmap[] filledWithShadowNinePatchBitmap = new Bitmap[1];

        public SharedBackgroundDrawables() {
            float[] fArr = new float[8];
            this.radii = fArr;
            Arrays.fill(fArr, AndroidUtilities.dp(11.0f));
        }

        public Drawable getOrCreateShadowNinePatch(int i) {
            if (this.shadowNinePatch == null || this.lastShadowColor != i) {
                this.lastShadowColor = i;
                this.shadowNinePatch = NinePatchBuilder.createNinePatch(this.shadowNinePatchBitmap, 0, this.radii, AndroidUtilities.dp(1.66f), i, 0.0f, AndroidUtilities.dp(0.33f), 0);
            }
            return this.shadowNinePatch;
        }

        public Drawable getOrCreateFilledNinePatch(int i) {
            if (this.filledNinePatch == null || this.lastFillingColor != i) {
                this.lastFillingColor = i;
                this.filledNinePatch = NinePatchBuilder.createNinePatch(this.filledNinePatchBitmap, i, this.radii, 0.0f, 0, 0.0f, 0.0f, i);
            }
            return this.filledNinePatch;
        }

        public Drawable getOrCreateFilledWithShadowNinePatch(int i, int i2) {
            if (this.filledWithShadowNinePatch == null || (this.lastFillingWithShadowFillingColor != i && this.lastFillingWithShadowShadowColor != i2)) {
                this.lastFillingWithShadowFillingColor = i;
                this.lastFillingWithShadowShadowColor = i2;
                this.filledWithShadowNinePatch = NinePatchBuilder.createNinePatch(this.filledWithShadowNinePatchBitmap, i, this.radii, AndroidUtilities.dp(1.66f), i2, 0.0f, AndroidUtilities.dp(0.33f), i);
            }
            return this.filledWithShadowNinePatch;
        }
    }

    public static class CardBackground extends Drawable {
        private static SharedBackgroundDrawables staticSharedBackgroundDrawables = new SharedBackgroundDrawables();
        private AnimatedFloat animatedSelected;
        private TL_stars.starGiftAttributeBackdrop backdrop;
        private final Path clipPath;
        private RadialGradient gradient;
        private final Matrix gradientMatrix;
        private int gradientRadius;
        private Bitmap lastDrawnBitmap;
        private Paint lastDrawnBitmapPaint;
        private int lastDrawnColor;
        private boolean lastNeedShadow;
        public final Paint paint;
        private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable pattern;
        public long patternDocumentId;
        private float r;
        private final RectF rect;
        private final Theme.ResourcesProvider resourcesProvider;
        private boolean selected;
        public Integer selectedColor;
        public int selectedColorKey;
        private final Paint selectedPaint;
        public int selectionStyle;
        private final Path strokeClipPath;
        private int[] strokeColors;
        private LinearGradient strokeGradient;
        private final Matrix strokeGradientMatrix;
        public final Paint strokePaint;
        private final View view;
        public boolean withPadding;
        private final boolean withShadow;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void setRoundRadius(float f) {
            this.r = f;
        }

        public CardBackground(View view, Theme.ResourcesProvider resourcesProvider, boolean z) {
            Paint paint = new Paint(1);
            this.paint = paint;
            Paint paint2 = new Paint(1);
            this.strokePaint = paint2;
            this.rect = new RectF();
            this.clipPath = new Path();
            this.gradientMatrix = new Matrix();
            this.strokeClipPath = new Path();
            this.strokeGradientMatrix = new Matrix();
            Paint paint3 = new Paint(1);
            this.selectedPaint = paint3;
            this.animatedSelected = new AnimatedFloat(new Runnable() { // from class: org.telegram.ui.Gifts.GiftSheet$CardBackground$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.invalidate();
                }
            }, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.r = AndroidUtilities.dp(11.0f);
            this.withPadding = true;
            this.selectionStyle = 0;
            int i = Theme.key_windowBackgroundWhite;
            this.selectedColorKey = i;
            this.view = view;
            this.resourcesProvider = resourcesProvider;
            this.pattern = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(view, AndroidUtilities.dp(28.0f)) { // from class: org.telegram.ui.Gifts.GiftSheet.CardBackground.1
                @Override // org.telegram.ui.Components.AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable, org.telegram.ui.Components.AnimatedEmojiSpan.InvalidateHolder
                public void invalidate() {
                    super.invalidate();
                    if (CardBackground.this.getCallback() != null) {
                        CardBackground.this.getCallback().invalidateDrawable(CardBackground.this);
                    }
                }
            };
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.Gifts.GiftSheet.CardBackground.2
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view2) {
                    CardBackground.this.pattern.attach();
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view2) {
                    CardBackground.this.pattern.detach();
                }
            });
            if (view.isAttachedToWindow()) {
                this.pattern.attach();
            }
            this.withShadow = z;
            paint.setColor(Theme.getColor(i, resourcesProvider));
            checkShadow(z);
            Paint.Style style = Paint.Style.STROKE;
            paint3.setStyle(style);
            paint2.setStyle(style);
        }

        private void checkShadow(boolean z) {
            if (this.lastNeedShadow != z) {
                this.lastNeedShadow = z;
                Paint paint = this.paint;
                if (z) {
                    paint.setShadowLayer(AndroidUtilities.dp(1.66f), 0.0f, AndroidUtilities.dp(0.33f), Theme.getColor(Theme.key_dialogCardShadow, this.resourcesProvider));
                } else {
                    paint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            draw(canvas, 0.0f);
        }

        public void setPadding(boolean z) {
            this.withPadding = z;
        }

        public void draw(Canvas canvas, float f) {
            Bitmap stableBitmapFromPattern;
            Bitmap bitmap;
            Drawable orCreateFilledNinePatch;
            Canvas canvas2 = canvas;
            Rect bounds = getBounds();
            float f2 = this.animatedSelected.set(this.selected);
            this.rect.set(bounds);
            if (this.withPadding) {
                this.rect.inset(AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f));
            }
            if (this.backdrop != null) {
                int iLerp = AndroidUtilities.lerp(Math.min(bounds.width(), bounds.height()), Math.max(bounds.width(), bounds.height()), 0.35f) / 2;
                if (this.gradient == null || this.gradientRadius != iLerp) {
                    this.gradientRadius = iLerp;
                    float f3 = iLerp;
                    TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = this.backdrop;
                    int i = stargiftattributebackdrop.center_color;
                    this.gradient = new RadialGradient(0.0f, 0.0f, f3, new int[]{i | (-16777216), i | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 0.0f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.gradientMatrix.reset();
                this.gradientMatrix.postTranslate(bounds.centerX(), Math.min(AndroidUtilities.dp(50.0f), bounds.centerY()));
                this.gradient.setLocalMatrix(this.gradientMatrix);
                this.paint.setShader(this.gradient);
            } else {
                this.paint.setShader(null);
            }
            int i2 = Theme.key_dialogCardShadow;
            int color = Theme.getColor(i2, this.resourcesProvider);
            int i3 = Theme.key_windowBackgroundWhite;
            int color2 = Theme.getColor(i3, this.resourcesProvider);
            boolean z = false;
            boolean z2 = this.r == ((float) AndroidUtilities.dp(11.0f)) && color == Theme.getColor(i2) && color2 == Theme.getColor(i3);
            checkShadow(this.withShadow && !z2);
            if (z2) {
                if (staticSharedBackgroundDrawables == null) {
                    staticSharedBackgroundDrawables = new SharedBackgroundDrawables();
                }
                RectF rectF = this.rect;
                Rect rect = AndroidUtilities.rectTmp2;
                rectF.round(rect);
                TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop2 = this.backdrop;
                boolean z3 = this.withShadow;
                if (stargiftattributebackdrop2 != null) {
                    if (z3) {
                        Drawable orCreateShadowNinePatch = staticSharedBackgroundDrawables.getOrCreateShadowNinePatch(color);
                        DrawableUtils.setBoundsIncreasePadding(orCreateShadowNinePatch, rect);
                        orCreateShadowNinePatch.draw(canvas2);
                    }
                    RectF rectF2 = this.rect;
                    float f4 = this.r;
                    canvas2.drawRoundRect(rectF2, f4, f4, this.paint);
                } else {
                    if (z3) {
                        orCreateFilledNinePatch = staticSharedBackgroundDrawables.getOrCreateFilledWithShadowNinePatch(color2, color);
                    } else {
                        orCreateFilledNinePatch = staticSharedBackgroundDrawables.getOrCreateFilledNinePatch(color2);
                    }
                    DrawableUtils.setBoundsIncreasePadding(orCreateFilledNinePatch, rect);
                    orCreateFilledNinePatch.draw(canvas2);
                }
            } else {
                RectF rectF3 = this.rect;
                float f5 = this.r;
                canvas2.drawRoundRect(rectF3, f5, f5, this.paint);
            }
            boolean z4 = (this.strokeColors == null && (this.backdrop == null || this.pattern.isEmpty())) ? false : true;
            if (z4) {
                canvas2.save();
                this.clipPath.rewind();
                Path path = this.clipPath;
                RectF rectF4 = this.rect;
                float f6 = this.r;
                path.addRoundRect(rectF4, f6, f6, Path.Direction.CW);
                canvas2.clipPath(this.clipPath);
            }
            if (this.strokeColors != null) {
                if (this.strokeGradient == null) {
                    this.strokeGradient = new LinearGradient(0.0f, 0.0f, 100.0f, 0.0f, this.strokeColors, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.strokeGradientMatrix.reset();
                this.strokeGradientMatrix.postTranslate(bounds.left, bounds.top);
                this.strokeGradientMatrix.postRotate((float) ((Math.atan2(bounds.height(), bounds.width()) / 3.141592653589793d) * 180.0d));
                float fSqrt = ((float) Math.sqrt(Math.pow(bounds.width(), 2.0d) + Math.pow(bounds.height(), 2.0d))) / 100.0f;
                this.strokeGradientMatrix.postScale(fSqrt, fSqrt);
                this.strokeGradient.setLocalMatrix(this.strokeGradientMatrix);
                this.strokePaint.setShader(this.strokeGradient);
                this.strokePaint.setStrokeWidth(AndroidUtilities.dp(4.66f));
                RectF rectF5 = this.rect;
                float f7 = this.r;
                canvas2.drawRoundRect(rectF5, f7, f7, this.strokePaint);
            }
            if (this.backdrop != null && !this.pattern.isEmpty()) {
                int i4 = this.backdrop.pattern_color | (-16777216);
                canvas2.save();
                canvas2.translate(bounds.centerX(), bounds.centerY());
                if (BatchParticlesDrawHelper.isAvailable() && (stableBitmapFromPattern = getStableBitmapFromPattern(this.pattern)) != null) {
                    if (this.lastDrawnBitmap != stableBitmapFromPattern || this.lastDrawnBitmapPaint == null) {
                        this.lastDrawnBitmap = stableBitmapFromPattern;
                        this.lastDrawnBitmapPaint = BatchParticlesDrawHelper.createBatchParticlesPaint(stableBitmapFromPattern);
                        z = true;
                    }
                    if (this.lastDrawnColor != i4 || z) {
                        this.lastDrawnColor = i4;
                        int i5 = Build.VERSION.SDK_INT;
                        Paint paint = this.lastDrawnBitmapPaint;
                        if (i5 >= 29) {
                            ChatActivityBlurredRoundButton$$ExternalSyntheticApiModelOutline1.m();
                            paint.setColorFilter(ChatActivityBlurredRoundButton$$ExternalSyntheticApiModelOutline0.m(i4, BlendMode.SRC_IN));
                        } else {
                            paint.setColorFilter(new PorterDuffColorFilter(i4, PorterDuff.Mode.SRC_IN));
                        }
                    }
                    if (f < 1.0f) {
                        bitmap = stableBitmapFromPattern;
                        StarGiftPatterns.drawPatternBatch(canvas2, 2, this.lastDrawnBitmapPaint, bitmap, bounds.width(), bounds.height(), 1.0f - f, 1.0f);
                    } else {
                        bitmap = stableBitmapFromPattern;
                    }
                    if (f > 0.0f) {
                        canvas2.translate(0.0f, AndroidUtilities.dp(-31.0f));
                        StarGiftPatterns.drawPatternBatch(canvas2, 0, this.lastDrawnBitmapPaint, bitmap, bounds.width(), bounds.height(), f, 1.0f);
                    }
                    canvas2 = canvas;
                } else {
                    this.pattern.setColor(Integer.valueOf(i4));
                    if (f < 1.0f) {
                        canvas2 = canvas;
                        StarGiftPatterns.drawPattern(canvas2, 2, this.pattern, bounds.width(), bounds.height(), 1.0f - f, 1.0f);
                    } else {
                        canvas2 = canvas;
                    }
                    if (f > 0.0f) {
                        canvas2.translate(0.0f, AndroidUtilities.dp(-31.0f));
                        StarGiftPatterns.drawPattern(canvas2, 0, this.pattern, bounds.width(), bounds.height(), f, 1.0f);
                    }
                }
                canvas2.restore();
            }
            if (z4) {
                canvas2.restore();
            }
            if (f2 > 0.0f) {
                int i6 = this.selectionStyle;
                if (i6 == 0) {
                    Paint paint2 = this.selectedPaint;
                    Integer num = this.selectedColor;
                    paint2.setColor(num != null ? num.intValue() : Theme.getColor(this.selectedColorKey, this.resourcesProvider));
                    this.selectedPaint.setStrokeWidth(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(1.667f), f2));
                    RectF rectF6 = AndroidUtilities.rectTmp;
                    rectF6.set(this.rect);
                    float fLerp = AndroidUtilities.lerp(-AndroidUtilities.dpf2(2.33f), AndroidUtilities.dpf2(3.33f), f2);
                    rectF6.inset(fLerp, fLerp);
                    float fLerp2 = AndroidUtilities.lerp(this.r, AndroidUtilities.dpf2(7.33f), f2);
                    canvas2.drawRoundRect(rectF6, fLerp2, fLerp2, this.selectedPaint);
                    return;
                }
                if (i6 == 1) {
                    Paint paint3 = this.selectedPaint;
                    Integer num2 = this.selectedColor;
                    paint3.setColor(num2 != null ? num2.intValue() : Theme.getColor(this.selectedColorKey, this.resourcesProvider));
                    this.selectedPaint.setStrokeWidth(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(3.0f), f2));
                    RectF rectF7 = AndroidUtilities.rectTmp;
                    rectF7.set(this.rect);
                    float fLerp3 = AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(3.0f) / 2.0f, f2);
                    rectF7.inset(fLerp3, fLerp3);
                    float fLerp4 = AndroidUtilities.lerp(this.r, AndroidUtilities.dpf2(10.0f), f2);
                    canvas2.drawRoundRect(rectF7, fLerp4, fLerp4, this.selectedPaint);
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public boolean getPadding(Rect rect) {
            rect.set(AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f));
            return true;
        }

        public void invalidate() {
            this.view.invalidate();
            if (getCallback() != null) {
                getCallback().invalidateDrawable(this);
            }
        }

        public void setBackdrop(TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop) {
            if (this.backdrop != stargiftattributebackdrop) {
                this.gradient = null;
            }
            this.backdrop = stargiftattributebackdrop;
            invalidate();
        }

        public void setPattern(TL_stars.starGiftAttributePattern stargiftattributepattern) {
            this.patternDocumentId = 0L;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.pattern;
            if (stargiftattributepattern == null) {
                swapAnimatedEmojiDrawable.set((Drawable) null, false);
                return;
            }
            swapAnimatedEmojiDrawable.set(stargiftattributepattern.document, false);
            TLRPC.Document document = stargiftattributepattern.document;
            if (document != null) {
                this.patternDocumentId = document.id;
            }
        }

        public void setStrokeColors(int[] iArr) {
            if (this.strokeColors == iArr) {
                return;
            }
            this.strokeColors = iArr;
            this.strokeGradient = null;
            invalidate();
        }

        public void setSelected(boolean z, boolean z2) {
            if (this.selected == z) {
                return;
            }
            this.selected = z;
            if (!z2) {
                this.animatedSelected.force(z);
            }
            invalidate();
        }

        private Bitmap getStableBitmapFromPattern(AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable) {
            Bitmap bitmap;
            if (!swapAnimatedEmojiDrawable.isStable()) {
                return null;
            }
            Drawable drawable = swapAnimatedEmojiDrawable.getDrawable();
            if (drawable instanceof AnimatedEmojiDrawable) {
                AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) drawable;
                ImageReceiver imageReceiver = animatedEmojiDrawable.getImageReceiver();
                long documentId = animatedEmojiDrawable.getDocumentId();
                if (imageReceiver != null && documentId == this.patternDocumentId && (bitmap = imageReceiver.getBitmap()) != null) {
                    return bitmap;
                }
            }
            return null;
        }
    }

    public static class Tabs extends FrameLayout {
        private AnimatedFloat animatedSelected;
        private final RectF ceiledRect;
        private final RectF flooredRect;
        private int lastId;
        private final LinearLayout layout;
        private final Theme.ResourcesProvider resourcesProvider;
        private final HorizontalScrollView scrollView;
        private int selected;
        private final Paint selectedPaint;
        private final RectF selectedRect;
        private final ArrayList<TextView> tabs;

        public Tabs(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.tabs = new ArrayList<>();
            this.flooredRect = new RectF();
            this.ceiledRect = new RectF();
            this.selectedRect = new RectF();
            this.selectedPaint = new Paint(1);
            this.lastId = Integer.MIN_VALUE;
            this.resourcesProvider = resourcesProvider;
            LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Gifts.GiftSheet.Tabs.1
                @Override // android.view.ViewGroup, android.view.View
                public void dispatchDraw(Canvas canvas) {
                    Tabs.this.selectedPaint.setColor(Theme.multAlpha(Theme.getColor(Theme.key_dialogGiftsTabText), 0.1f));
                    float f = Tabs.this.animatedSelected.set(Tabs.this.selected);
                    double d = f;
                    int iClamp = Utilities.clamp((int) Math.floor(d), Tabs.this.tabs.size() - 1, 0);
                    int iClamp2 = Utilities.clamp((int) Math.ceil(d), Tabs.this.tabs.size() - 1, 0);
                    int size = Tabs.this.tabs.size();
                    Tabs tabs = Tabs.this;
                    if (iClamp < size) {
                        setBounds(tabs.flooredRect, (View) Tabs.this.tabs.get(iClamp));
                    } else {
                        int size2 = tabs.tabs.size();
                        Tabs tabs2 = Tabs.this;
                        if (iClamp2 < size2) {
                            setBounds(tabs2.flooredRect, (View) Tabs.this.tabs.get(iClamp2));
                        } else {
                            tabs2.flooredRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        }
                    }
                    int size3 = Tabs.this.tabs.size();
                    Tabs tabs3 = Tabs.this;
                    if (iClamp2 < size3) {
                        setBounds(tabs3.ceiledRect, (View) Tabs.this.tabs.get(iClamp2));
                    } else {
                        int size4 = tabs3.tabs.size();
                        Tabs tabs4 = Tabs.this;
                        if (iClamp < size4) {
                            setBounds(tabs4.ceiledRect, (View) Tabs.this.tabs.get(iClamp));
                        } else {
                            tabs4.ceiledRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                        }
                    }
                    AndroidUtilities.lerp(Tabs.this.flooredRect, Tabs.this.ceiledRect, f - iClamp, Tabs.this.selectedRect);
                    float fHeight = Tabs.this.selectedRect.height() / 2.0f;
                    canvas.drawRoundRect(Tabs.this.selectedRect, fHeight, fHeight, Tabs.this.selectedPaint);
                    super.dispatchDraw(canvas);
                }

                private final void setBounds(RectF rectF, View view) {
                    rectF.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                }
            };
            this.layout = linearLayout;
            linearLayout.setClipToPadding(false);
            linearLayout.setClipChildren(false);
            linearLayout.setOrientation(0);
            linearLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(10.0f));
            if (z) {
                this.scrollView = null;
                addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
            } else {
                linearLayout.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(3.0f));
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
                this.scrollView = horizontalScrollView;
                horizontalScrollView.setHorizontalScrollBarEnabled(false);
                horizontalScrollView.setClipToPadding(false);
                horizontalScrollView.setClipChildren(false);
                horizontalScrollView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 119));
                addView(horizontalScrollView, LayoutHelper.createFrame(-1, -1, 119));
            }
            setHorizontalScrollBarEnabled(false);
            setClipToPadding(false);
            setClipChildren(false);
            this.animatedSelected = new AnimatedFloat(linearLayout, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        public void setSelected(int i, boolean z) {
            this.selected = i;
            if (!z) {
                this.animatedSelected.set(i, true);
            }
            this.layout.invalidate();
        }

        public void set(int i, ArrayList<CharSequence> arrayList, int i2, final Utilities.Callback<Integer> callback) {
            boolean z = this.lastId == i;
            this.lastId = i;
            if (this.tabs.size() != arrayList.size()) {
                int i3 = 0;
                int i4 = 0;
                while (i3 < this.tabs.size()) {
                    CharSequence charSequence = i4 < arrayList.size() ? arrayList.get(i4) : null;
                    if (charSequence == null) {
                        this.layout.removeView(this.tabs.remove(i3));
                        i3--;
                    } else {
                        this.tabs.get(i3).setText(charSequence);
                    }
                    i4++;
                    i3++;
                }
                while (i4 < arrayList.size()) {
                    LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(getContext());
                    linksTextView.setGravity(17);
                    linksTextView.setText(arrayList.get(i4));
                    linksTextView.setTypeface(AndroidUtilities.bold());
                    linksTextView.setTextColor(Theme.blendOver(Theme.getColor(Theme.key_dialogGiftsBackground), Theme.getColor(Theme.key_dialogGiftsTabText)));
                    linksTextView.setTextSize(1, 14.0f);
                    linksTextView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
                    linksTextView.setEllipsize(TextUtils.TruncateAt.END);
                    linksTextView.setSingleLine();
                    linksTextView.setMaxLines(1);
                    ScaleStateListAnimator.apply(linksTextView, 0.075f, 1.4f);
                    this.layout.addView(linksTextView, LayoutHelper.createLinear(-2, 26));
                    this.tabs.add(linksTextView);
                    i4++;
                }
            }
            this.selected = i2;
            if (!z) {
                this.animatedSelected.set(i2, true);
            }
            this.layout.invalidate();
            for (final int i5 = 0; i5 < this.tabs.size(); i5++) {
                this.tabs.get(i5).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Gifts.GiftSheet$Tabs$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        GiftSheet.Tabs.$r8$lambda$8LUCnuCXeW6XFid7k47_4mlXE7o(callback, i5, view);
                    }
                });
            }
        }

        public static /* synthetic */ void $r8$lambda$8LUCnuCXeW6XFid7k47_4mlXE7o(Utilities.Callback callback, int i, View view) {
            if (callback != null) {
                callback.run(Integer.valueOf(i));
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), i2);
        }

        public void updateColors() {
            for (int i = 0; i < this.tabs.size(); i++) {
                this.tabs.get(i).setTextColor(Theme.blendOver(Theme.getColor(Theme.key_dialogGiftsBackground), Theme.getColor(Theme.key_dialogGiftsTabText)));
            }
            this.layout.invalidate();
        }

        public static class Factory extends UItem.UItemFactory<Tabs> {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public Tabs createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new Tabs(context, true, resourcesProvider);
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
                ((Tabs) view).set(uItem.id, (ArrayList) uItem.object, uItem.intValue, (Utilities.Callback) uItem.object2);
            }

            public static UItem asTabs(int i, ArrayList<CharSequence> arrayList, int i2, Utilities.Callback<Integer> callback) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = i;
                uItemOfFactory.object = arrayList;
                uItemOfFactory.intValue = i2;
                uItemOfFactory.object2 = callback;
                return uItemOfFactory;
            }

            private static boolean eq(ArrayList<CharSequence> arrayList, ArrayList<CharSequence> arrayList2) {
                if (arrayList == arrayList2) {
                    return true;
                }
                if (arrayList == null && arrayList2 == null) {
                    return true;
                }
                if (arrayList == null || arrayList2 == null || arrayList.size() != arrayList2.size()) {
                    return false;
                }
                for (int i = 0; i < arrayList.size(); i++) {
                    if (!TextUtils.equals(arrayList.get(i), arrayList2.get(i))) {
                        return false;
                    }
                }
                return true;
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public boolean equals(UItem uItem, UItem uItem2) {
                return uItem.id == uItem2.id && eq((ArrayList) uItem.object, (ArrayList) uItem2.object);
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public boolean contentsEquals(UItem uItem, UItem uItem2) {
                return uItem.intValue == uItem2.intValue && uItem.object2 == uItem2.object2 && equals(uItem, uItem2);
            }
        }
    }
}
