package com.exteragram.messenger.plugins.ui.components;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.components.VerticalImageSpan;
import com.exteragram.messenger.plugins.Plugin;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.PythonPluginsEngine;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EffectsTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.HintView2;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001:\u0001 B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0018\u0010\u0019\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\rH\u0002J\b\u0010\u001e\u001a\u00020\u0018H\u0016J\b\u0010\u001f\u001a\u00020\u0018H\u0014R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006!"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet;", "Lorg/telegram/ui/ActionBar/BottomSheet;", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "validationResult", "Lcom/exteragram/messenger/plugins/PluginsController$PluginValidationResult;", "params", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "<init>", "(Lorg/telegram/ui/ActionBar/BaseFragment;Lcom/exteragram/messenger/plugins/PluginsController$PluginValidationResult;Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;)V", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "enableAfterInstallation", _UrlKt.FRAGMENT_ENCODE_SET, "installing", "cancellationRequested", "delayedLoadingRunnable", "Ljava/lang/Runnable;", "currentHint", "Lorg/telegram/ui/Stories/recorder/HintView2;", "isUpdate", "button", "Lorg/telegram/ui/Stories/recorder/ButtonWithCounterView;", "showSuccessBulletin", _UrlKt.FRAGMENT_ENCODE_SET, "showSimpleSuccessBulletin", "bf", "Lorg/telegram/ui/Components/BulletinFactory;", "restoreButtonText", "animated", "dismiss", "onSwipeStarts", "PluginInstallParams", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nInstallPluginBottomSheet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstallPluginBottomSheet.kt\ncom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,749:1\n1#2:750\n*E\n"})
public final class InstallPluginBottomSheet extends BottomSheet {
    private final ButtonWithCounterView button;
    private volatile boolean cancellationRequested;
    private HintView2 currentHint;
    private Runnable delayedLoadingRunnable;
    private boolean enableAfterInstallation;
    private boolean installing;
    private final boolean isUpdate;
    private final PluginInstallParams params;
    private final Plugin plugin;

    @Override // org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @JvmStatic
            public final PluginInstallParams of(MessageObject messageObject) {
                "messageObject";
                String pathToMessage = ChatUtils.getInstance().getPathToMessage(messageObject);
                boolean z = true;
                boolean z2 = false;
                if (messageObject.isForwarded()) {
                    Long forwardedFromId = messageObject.getForwardedFromId();
                    if (forwardedFromId != null) {
                        BadgesController badgesController = BadgesController.INSTANCE;
                        if (!badgesController.isTrusted(-forwardedFromId.longValue()) && !badgesController.isExtera(-forwardedFromId.longValue())) {
                            z = false;
                        }
                        z2 = z;
                    }
                } else if (messageObject.isFromChannel() && !messageObject.isFromChat()) {
                    long j = -messageObject.getDialogId();
                    BadgesController badgesController2 = BadgesController.INSTANCE;
                    if (!badgesController2.isTrusted(j) && !badgesController2.isExtera(j)) {
                        z = false;
                    }
                    z2 = z;
                }
                return new PluginInstallParams(pathToMessage, z2);
            }
        }
    }
}
