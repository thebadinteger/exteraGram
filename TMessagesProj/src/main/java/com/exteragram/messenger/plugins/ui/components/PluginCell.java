package com.exteragram.messenger.plugins.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.plugins.Plugin;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EffectsTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.LaunchActivity;

@Metadata(d1 = {"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002:\u0001HB\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u000e\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u001fJ\u0018\u0010#\u001a\u00020!2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%H\u0014J\b\u0010'\u001a\u00020!H\u0002J\u001a\u0010(\u001a\u00020!2\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dJ\b\u0010)\u001a\u00020!H\u0002J\b\u0010*\u001a\u00020!H\u0002J\b\u0010+\u001a\u00020!H\u0002J\b\u0010,\u001a\u00020!H\u0002J\u0016\u0010-\u001a\u00020!2\u0006\u0010.\u001a\u00020\u001f2\u0006\u0010/\u001a\u00020\u001fJ\u000e\u00100\u001a\u00020!2\u0006\u00101\u001a\u00020\u001fJ*\u00102\u001a\u00020\u00132\u0006\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u00103\u001a\u00020%2\u0006\u00104\u001a\u00020\u001f2\u0006\u00105\u001a\u000206H\u0002J\u0010\u00107\u001a\u00020!2\u0006\u00108\u001a\u000209H\u0002J\b\u0010:\u001a\u00020%H\u0002J\u0010\u0010;\u001a\n =*\u0004\u0018\u00010<0<H\u0002J\b\u0010>\u001a\u00020!H\u0002J\b\u0010?\u001a\u00020!H\u0014J\b\u0010@\u001a\u00020!H\u0014J5\u0010A\u001a\u00020!2\u0006\u0010B\u001a\u00020%2\u0006\u0010C\u001a\u00020%2\u0016\u0010D\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010F0E\"\u0004\u0018\u00010FH\u0016¢\u0006\u0002\u0010GR\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006I"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginCell;", "Landroid/widget/FrameLayout;", "Lorg/telegram/messenger/NotificationCenter$NotificationCenterDelegate;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "imageView", "Lorg/telegram/ui/Components/BackupImageView;", "pluginNameView", "Landroid/widget/TextView;", "subtitleView", "Lorg/telegram/ui/Components/EffectsTextView;", "descriptionView", "requirementsLayout", "Lcom/exteragram/messenger/plugins/ui/components/PluginRequirementsView;", "checkBox", "Lorg/telegram/ui/Components/Switch;", "settingsButton", "Landroid/widget/ImageView;", "shareButton", "openInButton", "deleteButton", "pinButton", "headerLayout", "Landroid/widget/LinearLayout;", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "pluginCellDelegate", "Lcom/exteragram/messenger/plugins/ui/components/PluginCellDelegate;", "compactMode", _UrlKt.FRAGMENT_ENCODE_SET, "setCompact", _UrlKt.FRAGMENT_ENCODE_SET, "compact", "onMeasure", "widthMeasureSpec", _UrlKt.FRAGMENT_ENCODE_SET, "heightMeasureSpec", "updateLayout", "set", "bindNotRespondingState", "updateDeleteButton", "bindErrorState", "bindNormalState", "setChecked", "checked", "animated", "setPinned", "pinned", "createButton", "iconResId", "isRed", "onClickListener", "Landroid/view/View$OnClickListener;", "applyClickAnimation", "view", "Landroid/view/View;", "getPluginIconRadiusDp", "createPluginIconOutlineProvider", "Landroid/view/ViewOutlineProvider;", "kotlin.jvm.PlatformType", "updatePluginIconOutlineProvider", "onAttachedToWindow", "onDetachedFromWindow", "didReceivedNotification", "id", "account", "args", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "(II[Ljava/lang/Object;)V", "Factory", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SuppressLint({"ViewConstructor"})
public final class PluginCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final Switch checkBox;
    private boolean compactMode;
    private final ImageView deleteButton;
    private final EffectsTextView descriptionView;
    private final LinearLayout headerLayout;
    private final BackupImageView imageView;
    private final ImageView openInButton;
    private final ImageView pinButton;
    private Plugin plugin;
    private PluginCellDelegate pluginCellDelegate;
    private final TextView pluginNameView;
    private final PluginRequirementsView requirementsLayout;
    private final ImageView settingsButton;
    private final ImageView shareButton;
    private final EffectsTextView subtitleView;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public PluginCell(Context context) {
        super(context);
        setClickable(false);
        setClipChildren(false);
        setClipToPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 119, 16.0f, 16.0f, 16.0f, 8.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        this.headerLayout = linearLayout2;
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setVisibility(8);
        backupImageView.setOutlineProvider(createPluginIconOutlineProvider());
        backupImageView.setClipToOutline(true);
        backupImageView.getImageReceiver().setAutoRepeat(1);
        this.imageView = backupImageView;
        linearLayout2.addView(backupImageView, LayoutHelper.createLinear(56, 56, 51, 0.0f, 0.0f, 0.0f, 12.0f));
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(1);
        linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-1, -2));
        TextView textView = new TextView(context);
        textView.setGravity(3);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 18.0f);
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView.setEllipsize(truncateAt);
        textView.setTypeface(AndroidUtilities.bold());
        this.pluginNameView = textView;
        linearLayout3.addView(textView, LayoutHelper.createLinear(-1, -2));
        EffectsTextView effectsTextView = new EffectsTextView(context);
        effectsTextView.setGravity(3);
        effectsTextView.setTypeface(AndroidUtilities.regular());
        effectsTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        int i = Theme.key_windowBackgroundWhiteLinkText;
        effectsTextView.setLinkTextColor(Theme.getColor(i));
        effectsTextView.setTextSize(1, 14.0f);
        effectsTextView.setEllipsize(truncateAt);
        effectsTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.subtitleView = effectsTextView;
        linearLayout3.addView(effectsTextView, LayoutHelper.createLinear(-1, -2, 0.0f, 2.0f, 0.0f, 0.0f));
        EffectsTextView effectsTextView2 = new EffectsTextView(context);
        effectsTextView2.setGravity(3);
        effectsTextView2.setTypeface(AndroidUtilities.regular());
        effectsTextView2.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        effectsTextView2.setLinkTextColor(Theme.getColor(i));
        this.descriptionView = effectsTextView2;
        linearLayout.addView(effectsTextView2, LayoutHelper.createLinear(-1, -2, 0, 0, 12, 0, 0));
        PluginRequirementsView pluginRequirementsView = new PluginRequirementsView(context, null, 2, null);
        pluginRequirementsView.setVisibility(8);
        this.requirementsLayout = pluginRequirementsView;
        linearLayout.addView(pluginRequirementsView, LayoutHelper.createLinear(-1, -2, 0, 0, 12, 0, 0));
        View view = new View(context);
        view.setBackgroundColor(Theme.getDividerColor(null));
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 1.0f / AndroidUtilities.density, 0, 0, 12, 0, 8));
        LinearLayout linearLayout4 = new LinearLayout(context);
        linearLayout4.setOrientation(0);
        ImageView imageViewCreateButton = createButton(context, R.drawable.msg_share, false, new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PluginCell.$r8$lambda$gPhyRoTYC0gInhhjd5k0po863gM(PluginCell.this, view2);
            }
        });
        this.shareButton = imageViewCreateButton;
        linearLayout4.addView(imageViewCreateButton, LayoutHelper.createFrame(40, 40.0f, 51, 0.0f, 0.0f, 8.0f, 0.0f));
        ImageView imageViewCreateButton2 = createButton(context, R.drawable.msg_openin, false, new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PluginCell.$r8$lambda$TkTsTkgHS2XoNteC2HxEG9EpXDk(PluginCell.this, view2);
            }
        });
        this.openInButton = imageViewCreateButton2;
        linearLayout4.addView(imageViewCreateButton2, LayoutHelper.createFrame(40, 40.0f, 51, 0.0f, 0.0f, 8.0f, 0.0f));
        ImageView imageViewCreateButton3 = createButton(context, R.drawable.msg_pin, false, new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PluginCell.m1358$r8$lambda$miXUx0RJdhvQyk3TfMBgG7YnBI(PluginCell.this, view2);
            }
        });
        this.pinButton = imageViewCreateButton3;
        linearLayout4.addView(imageViewCreateButton3, LayoutHelper.createFrame(40, 40.0f, 51, 0.0f, 0.0f, 8.0f, 0.0f));
        ImageView imageViewCreateButton4 = createButton(context, R.drawable.msg_settings, false, new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PluginCell.$r8$lambda$vFaf3kpNKKpdG19431rjWCjd68Y(PluginCell.this, view2);
            }
        });
        imageViewCreateButton4.setVisibility(8);
        this.settingsButton = imageViewCreateButton4;
        linearLayout4.addView(imageViewCreateButton4, LayoutHelper.createFrame(40, 40.0f, 51, 0.0f, 0.0f, 8.0f, 0.0f));
        ImageView imageViewCreateButton5 = createButton(context, R.drawable.msg_delete, true, new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PluginCell.m1359$r8$lambda$txZU19WGsUHbCEQUldGESo9Wwc(PluginCell.this, view2);
            }
        });
        this.deleteButton = imageViewCreateButton5;
        addView(imageViewCreateButton5, LayoutHelper.createFrame(40, 40.0f, 85, 0.0f, 0.0f, 16.0f, 8.0f));
        linearLayout.addView(linearLayout4, LayoutHelper.createFrame(-1, 40, 83));
        Switch r3 = new Switch(context);
        int i2 = Theme.key_switchTrack;
        int i3 = Theme.key_switchTrackChecked;
        int i4 = Theme.key_windowBackgroundWhite;
        r3.setColors(i2, i3, i4, i4);
        r3.setFocusable(false);
        this.checkBox = r3;
        addView(r3, LayoutHelper.createFrame(37, 40.0f, 53, 0.0f, 16.0f, 24.0f, 0.0f));
        setCompact(ExteraConfig.getPluginsCompactView());
    }

    public static void $r8$lambda$gPhyRoTYC0gInhhjd5k0po863gM(PluginCell pluginCell, View view) {
        PluginCellDelegate pluginCellDelegate = pluginCell.pluginCellDelegate;
        if (pluginCellDelegate != null) {
            pluginCellDelegate.sharePlugin();
        }
    }

    public static void $r8$lambda$TkTsTkgHS2XoNteC2HxEG9EpXDk(PluginCell pluginCell, View view) {
        PluginCellDelegate pluginCellDelegate = pluginCell.pluginCellDelegate;
        if (pluginCellDelegate != null) {
            pluginCellDelegate.openInExternalApp();
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$miXUx0RJdhvQyk3T-fMBgG7YnBI, reason: not valid java name */
    public static void m1358$r8$lambda$miXUx0RJdhvQyk3TfMBgG7YnBI(PluginCell pluginCell, View view) {
        PluginCellDelegate pluginCellDelegate = pluginCell.pluginCellDelegate;
        if (pluginCellDelegate != null) {
            pluginCellDelegate.pinPlugin(pluginCell);
        }
    }

    public static void $r8$lambda$vFaf3kpNKKpdG19431rjWCjd68Y(PluginCell pluginCell, View view) {
        PluginCellDelegate pluginCellDelegate = pluginCell.pluginCellDelegate;
        if (pluginCellDelegate != null) {
            pluginCellDelegate.openPluginSettings();
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$txZU1-9WGsUHbCEQUldGESo9Wwc, reason: not valid java name */
    public static void m1359$r8$lambda$txZU19WGsUHbCEQUldGESo9Wwc(PluginCell pluginCell, View view) {
        PluginCellDelegate pluginCellDelegate = pluginCell.pluginCellDelegate;
        if (pluginCellDelegate != null) {
            pluginCellDelegate.deletePlugin();
        }
    }

    public final void setCompact(boolean compact) {
        if (this.compactMode == compact) {
            return;
        }
        this.compactMode = compact;
        updateLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), TLObject.FLAG_30), heightMeasureSpec);
    }

    private final void updateLayout() {
        Plugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }
        View childAt = this.headerLayout.getChildAt(1);
        LinearLayout linearLayout = (LinearLayout) childAt;
        this.headerLayout.setOrientation(!this.compactMode ? 1 : 0);
        ViewGroup.LayoutParams layoutParams = this.imageView.getLayoutParams();
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) layoutParams;
        layoutParams2.rightMargin = this.compactMode ? AndroidUtilities.dp(16.0f) : 0;
        layoutParams2.bottomMargin = this.compactMode ? 0 : AndroidUtilities.dp(12.0f);
        layoutParams2.width = this.compactMode ? AndroidUtilities.dp(49.0f) : AndroidUtilities.dp(56.0f);
        layoutParams2.height = this.compactMode ? AndroidUtilities.dp(49.0f) : AndroidUtilities.dp(56.0f);
        ViewGroup.LayoutParams layoutParams3 = linearLayout.getLayoutParams();
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) layoutParams3;
        boolean z = this.compactMode;
        layoutParams4.gravity = z ? 16 : 3;
        this.pluginNameView.setSingleLine(z);
        this.subtitleView.setSingleLine(this.compactMode);
        final int iDp = ((!this.compactMode || plugin.hasError()) && (this.compactMode || (plugin.getPack() != null && plugin.getIndex() >= 0) || plugin.hasError())) ? 0 : AndroidUtilities.dp(61.0f);
        this.pluginNameView.setPadding(0, 0, iDp, 0);
        this.pluginNameView.post(new Runnable() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                PluginCell.$r8$lambda$DDJoJMGrt7paL1pOJFoOkItIZSA(PluginCell.this, iDp);
            }
        });
        requestLayout();
    }

    public static void $r8$lambda$DDJoJMGrt7paL1pOJFoOkItIZSA(PluginCell pluginCell, int i) {
        EffectsTextView effectsTextView = pluginCell.subtitleView;
        if (pluginCell.pluginNameView.getLineCount() > 1) {
            i = 0;
        }
        effectsTextView.setPadding(0, 0, i, 0);
    }

    public final void set(Plugin plugin, final PluginCellDelegate pluginCellDelegate) {
        String string;
        if (plugin == null || pluginCellDelegate == null) {
            return;
        }
        this.pluginCellDelegate = pluginCellDelegate;
        this.plugin = plugin;
        updatePluginIconOutlineProvider();
        PluginsController.Companion companion = PluginsController.INSTANCE;
        setPinned(companion.isPluginPinned(plugin.getId()));
        this.openInButton.setVisibility(pluginCellDelegate.canOpenInExternalApp() ? 0 : 8);
        boolean z = plugin.getPack() != null && plugin.getIndex() >= 0;
        this.imageView.setVisibility(z ? 0 : 8);
        if (z) {
            MediaDataController.getInstance(UserConfig.selectedAccount).setPlaceholderImageByIndex(this.imageView, plugin.getPack(), plugin.getIndex(), "100_100");
        } else {
            this.imageView.setImageDrawable(null);
        }
        this.pluginNameView.setText(plugin.getName());
        EffectsTextView effectsTextView = this.subtitleView;
        if (this.compactMode) {
            string = "v";
        } else {
            string = LocaleController.getString(R.string.PluginVersion) + ' ';
        }
        effectsTextView.setText(new SpannableStringBuilder(string).append((CharSequence) plugin.getVersion()).append((CharSequence) " • ").append(LocaleUtils.formatWithUsernames(plugin.getAuthor())));
        if (plugin.getIsNotResponding()) {
            bindNotRespondingState();
        } else if (plugin.hasError()) {
            bindErrorState();
        } else {
            bindNormalState();
        }
        this.requirementsLayout.setRequirements(plugin.getRequirements());
        updateLayout();
        this.checkBox.setChecked(plugin.isEnabled(), false);
        this.checkBox.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                pluginCellDelegate.togglePlugin(PluginCell.this);
            }
        });
        AndroidUtilities.updateViewVisibilityAnimated(this.settingsButton, plugin.isEnabled() && companion.getInstance().hasPluginSettings(plugin.getId()), 0.5f, true, false);
    }

    private final void bindNotRespondingState() {
        this.descriptionView.setText(LocaleController.getString(R.string.PluginIsNotResponding));
        this.descriptionView.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
        this.descriptionView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        this.descriptionView.setTextSize(1, 12.0f);
        this.descriptionView.setOnClickListener(null);
        this.checkBox.setVisibility(8);
        updateDeleteButton();
    }

    private final void updateDeleteButton() {
        int color;
        Plugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }
        boolean isNotResponding = plugin.getIsNotResponding();
        this.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(!isNotResponding ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        ImageView imageView = this.deleteButton;
        if (!isNotResponding) {
            color = Theme.multAlpha(Theme.getColor(Theme.key_text_RedRegular), 0.12f);
        } else {
            color = Theme.getColor(Theme.key_dialogButtonSelector);
        }
        imageView.setBackground(Theme.createSelectorDrawable(color, 1, AndroidUtilities.dp(20.0f)));
        this.deleteButton.setImageResource(!isNotResponding ? R.drawable.msg_delete : R.drawable.ic_ab_other);
    }

    private final void bindErrorState() {
        final Plugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }
        EffectsTextView effectsTextView = this.descriptionView;
        Throwable error = plugin.getError();
        effectsTextView.setText(error != null ? error.getLocalizedMessage() : null);
        this.descriptionView.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
        this.descriptionView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        this.descriptionView.setTextSize(1, 12.0f);
        this.descriptionView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PluginCell.$r8$lambda$6rpDPmjlQ6jnFtZWLEsa5Bv1A4Q(plugin, view);
            }
        });
        this.checkBox.setVisibility(8);
        updateDeleteButton();
    }

    public static void $r8$lambda$6rpDPmjlQ6jnFtZWLEsa5Bv1A4Q(Plugin plugin, View view) {
        if (AndroidUtilities.addToClipboard(AppUtils.stackTraceToString(plugin.getError()))) {
            BulletinFactory.of(LaunchActivity.getSafeLastFragment()).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
        }
    }

    private final void bindNormalState() {
        Plugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }
        this.descriptionView.setText(LocaleUtils.fullyFormatText(plugin.getDescription()));
        this.descriptionView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionView.setTypeface(AndroidUtilities.regular());
        this.descriptionView.setTextSize(1, 15.0f);
        this.descriptionView.setOnClickListener(null);
        this.checkBox.setVisibility(0);
        updateDeleteButton();
    }

    public final void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public final void setPinned(boolean pinned) {
        this.pinButton.setImageResource(pinned ? R.drawable.msg_unpin : R.drawable.msg_pin);
    }

    private final ImageView createButton(Context context, int iconResId, boolean isRed, View.OnClickListener onClickListener) {
        int color;
        ImageView imageView = new ImageView(context);
        applyClickAnimation(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(iconResId);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(isRed ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        if (isRed) {
            color = Theme.multAlpha(Theme.getColor(Theme.key_text_RedRegular), 0.12f);
        } else {
            color = Theme.getColor(Theme.key_dialogButtonSelector);
        }
        imageView.setBackground(Theme.createSelectorDrawable(color, 1, AndroidUtilities.dp(20.0f)));
        imageView.setOnClickListener(onClickListener);
        return imageView;
    }

    private final void applyClickAnimation(View view) {
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.exteragram.messenger.plugins.ui.components.PluginCell$$ExternalSyntheticLambda5
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return PluginCell.$r8$lambda$GNS5TyJK00U1k_uT5XN4XND2ouo(view2, motionEvent);
            }
        });
    }

    public static boolean $r8$lambda$GNS5TyJK00U1k_uT5XN4XND2ouo(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            view.setPressed(true);
            view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80L).start();
            return true;
        }
        if (action != 1) {
            if (action != 3) {
                return false;
            }
            view.setPressed(false);
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(350L).setInterpolator(new OvershootInterpolator(1.5f)).start();
            return true;
        }
        view.setPressed(false);
        if (motionEvent.getX() >= 0.0f && motionEvent.getX() <= view.getWidth() && motionEvent.getY() >= 0.0f && motionEvent.getY() <= view.getHeight()) {
            view.performClick();
        }
        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(350L).setInterpolator(new OvershootInterpolator(1.5f)).start();
        return true;
    }

    private final int getPluginIconRadiusDp() {
        return Math.max(ExteraConfig.getSectionRadiusDp() - 16, 8);
    }

    private final ViewOutlineProvider createPluginIconOutlineProvider() {
        return ViewOutlineProviderImpl.boundsWithRoundRect(AndroidUtilities.dp(getPluginIconRadiusDp()));
    }

    private final void updatePluginIconOutlineProvider() {
        this.imageView.setOutlineProvider(createPluginIconOutlineProvider());
        this.imageView.invalidateOutline();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pluginSettingsRegistered);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pluginSettingsUnregistered);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pluginSettingsRegistered);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pluginSettingsUnregistered);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        Plugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }
        if (id == NotificationCenter.pluginSettingsRegistered || id == NotificationCenter.pluginSettingsUnregistered) {
            Object objFirstOrNull = ArraysKt.firstOrNull(args);
            String str = objFirstOrNull instanceof String ? (String) objFirstOrNull : null;
            if (str != null && Intrinsics.areEqual(str, plugin.getId())) {
                AndroidUtilities.updateViewVisibilityAnimated(this.settingsButton, id == NotificationCenter.pluginSettingsRegistered && plugin.isEnabled(), 0.5f, true, true);
            }
        }
    }

    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u001f2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001fB\u0007¢\u0006\u0004\b\u0003\u0010\u0004J2\u0010\u0005\u001a\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J0\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\b\u001a\u00020\u0019H\u0016J\u0019\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0014H\u0096\u0002J\u0018\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0014H\u0016J\b\u0010\u001e\u001a\u00020\u0016H\u0016¨\u0006 "}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginCell$Factory;", "Lorg/telegram/ui/Components/UItem$UItemFactory;", "Lcom/exteragram/messenger/plugins/ui/components/PluginCell;", "<init>", "()V", "createView", "context", "Landroid/content/Context;", "listView", "Lorg/telegram/ui/Components/RecyclerListView;", "currentAccount", _UrlKt.FRAGMENT_ENCODE_SET, "classGuid", "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "bindView", _UrlKt.FRAGMENT_ENCODE_SET, "view", "Landroid/view/View;", "item", "Lorg/telegram/ui/Components/UItem;", "divider", _UrlKt.FRAGMENT_ENCODE_SET, "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "Lorg/telegram/ui/Components/UniversalRecyclerView;", "equals", "a", "b", "contentsEquals", "isClickable", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Factory extends UItem.UItemFactory<PluginCell> {

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);

        @JvmStatic
        public static final UItem asPlugin(Plugin plugin, PluginCellDelegate pluginCellDelegate) {
            return INSTANCE.asPlugin(plugin, pluginCellDelegate);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean isClickable() {
            return false;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public PluginCell createView(Context context, RecyclerListView listView, int currentAccount, int classGuid, Theme.ResourcesProvider resourcesProvider) {
            return new PluginCell(context);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem item, boolean divider, UniversalAdapter adapter, UniversalRecyclerView listView) {
            if (view instanceof PluginCell) {
                PluginCell pluginCell = (PluginCell) view;
                Object obj = item.object;
                Plugin plugin = obj instanceof Plugin ? (Plugin) obj : null;
                Object obj2 = item.object2;
                pluginCell.set(plugin, obj2 instanceof PluginCellDelegate ? (PluginCellDelegate) obj2 : null);
            }
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean equals(UItem a2, UItem b2) {
            return a2.id == b2.id;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean contentsEquals(UItem a2, UItem b2) {
            if (a2.id != b2.id || a2.intValue != b2.intValue) {
                return false;
            }
            Object obj = a2.object;
            Plugin plugin = obj instanceof Plugin ? (Plugin) obj : null;
            if (plugin == null) {
                return false;
            }
            Object obj2 = b2.object;
            Plugin plugin2 = obj2 instanceof Plugin ? (Plugin) obj2 : null;
            if (plugin2 == null) {
                return false;
            }
            return Intrinsics.areEqual(plugin, plugin2);
        }

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007¨\u0006\n"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginCell$Factory$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "asPlugin", "Lorg/telegram/ui/Components/UItem;", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "pluginCellDelegate", "Lcom/exteragram/messenger/plugins/ui/components/PluginCellDelegate;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @JvmStatic
            public final UItem asPlugin(Plugin plugin, PluginCellDelegate pluginCellDelegate) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = plugin.getId().hashCode();
                uItemOfFactory.object = plugin;
                uItemOfFactory.object2 = pluginCellDelegate;
                uItemOfFactory.intValue = (plugin.getIsNotResponding() ? 1 : 0) | (plugin.isEnabled() ? 2 : 0) | (plugin.hasError() ? 4 : 0);
                return uItemOfFactory;
            }
        }

        static {
            UItem.UItemFactory.setup(new Factory());
        }
    }
}
