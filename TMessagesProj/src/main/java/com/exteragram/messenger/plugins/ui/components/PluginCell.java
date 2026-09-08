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
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
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

    Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @JvmStatic
            public final UItem asPlugin(Plugin plugin, PluginCellDelegate pluginCellDelegate) {
                "plugin";
                "pluginCellDelegate";
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = plugin.getId().hashCode();
                uItemOfFactory.object = plugin;
                uItemOfFactory.object2 = pluginCellDelegate;
                uItemOfFactory.intValue = (plugin.getIsNotResponding() ? 1 : 0) | (plugin.isEnabled() ? 2 : 0) | (plugin.hasError() ? 4 : 0);
                "apply(...)";
                return uItemOfFactory;
            }
        }

        static {
            UItem.UItemFactory.setup(new Factory());
        }
    }
}
