package com.exteragram.messenger.nowplaying.ui.components;

import android.graphics.Bitmap;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import com.exteragram.messenger.api.dto.NowPlayingDTO;
import com.exteragram.messenger.utils.ui.UIUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0017\b\u0086\b\u0018\u0000 *2\u00020\u0001:\u0002+*BC\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b¢\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u000fHÖ\u0001¢\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u0004HÖ\u0001¢\u0006\u0004\b\u0012\u0010\u0013J\u001a\u0010\u0016\u001a\u00020\u00152\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0018\u001a\u0004\b\u0019\u0010\u001aR\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u001b\u001a\u0004\b\u001c\u0010\u001dR\u0019\u0010\u0006\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001e\u0010\u001dR\u0019\u0010\b\u001a\u0004\u0018\u00010\u00078\u0006¢\u0006\f\n\u0004\b\b\u0010\u001f\u001a\u0004\b \u0010!R\u0019\u0010\n\u001a\u0004\u0018\u00010\t8\u0006¢\u0006\f\n\u0004\b\n\u0010\"\u001a\u0004\b#\u0010$R\"\u0010\f\u001a\u00020\u000b8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\b\f\u0010%\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)¨\u0006,"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "nowPlayingDTO", _UrlKt.FRAGMENT_ENCODE_SET, "backgroundColor", "accentColor", "Landroid/graphics/Bitmap;", "coverBitmap", "Lorg/telegram/messenger/ImageLocation;", "imageLocation", _UrlKt.FRAGMENT_ENCODE_SET, "userEmoji", "<init>", "(Lcom/exteragram/messenger/api/dto/NowPlayingDTO;Ljava/lang/Integer;Ljava/lang/Integer;Landroid/graphics/Bitmap;Lorg/telegram/messenger/ImageLocation;J)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "getNowPlayingDTO", "()Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "Ljava/lang/Integer;", "getBackgroundColor", "()Ljava/lang/Integer;", "getAccentColor", "Landroid/graphics/Bitmap;", "getCoverBitmap", "()Landroid/graphics/Bitmap;", "Lorg/telegram/messenger/ImageLocation;", "getImageLocation", "()Lorg/telegram/messenger/ImageLocation;", "J", "getUserEmoji", "()J", "setUserEmoji", "(J)V", "Companion", "Callback", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class NowPlayingCardData {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final Integer accentColor;
    private final Integer backgroundColor;
    private final Bitmap coverBitmap;
    private final ImageLocation imageLocation;
    private final NowPlayingDTO nowPlayingDTO;
    private long userEmoji;

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData$Callback;", _UrlKt.FRAGMENT_ENCODE_SET, "onDataLoaded", _UrlKt.FRAGMENT_ENCODE_SET, "data", "Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface Callback {
        void onDataLoaded(NowPlayingCardData data);
    }

    @JvmStatic
    public static final void create(NowPlayingDTO nowPlayingDTO, TLRPC.Document document, Callback callback) {
        INSTANCE.create(nowPlayingDTO, document, callback);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NowPlayingCardData)) {
            return false;
        }
        NowPlayingCardData nowPlayingCardData = (NowPlayingCardData) other;
        return Intrinsics.areEqual(this.nowPlayingDTO, nowPlayingCardData.nowPlayingDTO) && Intrinsics.areEqual(this.backgroundColor, nowPlayingCardData.backgroundColor) && Intrinsics.areEqual(this.accentColor, nowPlayingCardData.accentColor) && Intrinsics.areEqual(this.coverBitmap, nowPlayingCardData.coverBitmap) && Intrinsics.areEqual(this.imageLocation, nowPlayingCardData.imageLocation) && this.userEmoji == nowPlayingCardData.userEmoji;
    }

    public int hashCode() {
        int iHashCode = this.nowPlayingDTO.hashCode() * 31;
        Integer num = this.backgroundColor;
        int iHashCode2 = (iHashCode + (num == null ? 0 : num.hashCode())) * 31;
        Integer num2 = this.accentColor;
        int iHashCode3 = (iHashCode2 + (num2 == null ? 0 : num2.hashCode())) * 31;
        Bitmap bitmap = this.coverBitmap;
        int iHashCode4 = (iHashCode3 + (bitmap == null ? 0 : bitmap.hashCode())) * 31;
        ImageLocation imageLocation = this.imageLocation;
        return ((iHashCode4 + (imageLocation != null ? imageLocation.hashCode() : 0)) * 31) + Long.hashCode(this.userEmoji);
    }

    public String toString() {
        return "NowPlayingCardData(nowPlayingDTO=" + this.nowPlayingDTO + ", backgroundColor=" + this.backgroundColor + ", accentColor=" + this.accentColor + ", coverBitmap=" + this.coverBitmap + ", imageLocation=" + this.imageLocation + ", userEmoji=" + this.userEmoji + ')';
    }

    public NowPlayingCardData(NowPlayingDTO nowPlayingDTO, Integer num, Integer num2, Bitmap bitmap, ImageLocation imageLocation, long j) {
        this.nowPlayingDTO = nowPlayingDTO;
        this.backgroundColor = num;
        this.accentColor = num2;
        this.coverBitmap = bitmap;
        this.imageLocation = imageLocation;
        this.userEmoji = j;
    }

    public /* synthetic */ NowPlayingCardData(NowPlayingDTO nowPlayingDTO, Integer num, Integer num2, Bitmap bitmap, ImageLocation imageLocation, long j, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(nowPlayingDTO, num, num2, bitmap, (i & 16) != 0 ? null : imageLocation, (i & 32) != 0 ? -1L : j);
    }

    public final NowPlayingDTO getNowPlayingDTO() {
        return this.nowPlayingDTO;
    }

    public final Integer getBackgroundColor() {
        return this.backgroundColor;
    }

    public final Integer getAccentColor() {
        return this.accentColor;
    }

    public final Bitmap getCoverBitmap() {
        return this.coverBitmap;
    }

    public final ImageLocation getImageLocation() {
        return this.imageLocation;
    }

    public final long getUserEmoji() {
        return this.userEmoji;
    }

    public final void setUserEmoji(long j) {
        this.userEmoji = j;
    }

    @Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J\"\u0010\f\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0002¨\u0006\u0011"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "create", _UrlKt.FRAGMENT_ENCODE_SET, "nowPlayingDTO", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "savedMusic", "Lorg/telegram/tgnet/TLRPC$Document;", com.sun.jna.Callback.METHOD_NAME, "Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData$Callback;", "extractColors", "Lkotlin/Pair;", _UrlKt.FRAGMENT_ENCODE_SET, "bitmap", "Landroid/graphics/Bitmap;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [T, com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda1] */
        @JvmStatic
        public final void create(final NowPlayingDTO nowPlayingDTO, TLRPC.Document savedMusic, final Callback callback) {
            String coverUrl;
            TLRPC.PhotoSize closestPhotoSizeWithSize;
            ImageLocation forDocument = (!Intrinsics.areEqual(nowPlayingDTO.getPlatform(), "TELEGRAM") || savedMusic == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(savedMusic.thumbs, 1000)) == null) ? null : ImageLocation.getForDocument(closestPhotoSizeWithSize, savedMusic);
            if (forDocument == null && (coverUrl = nowPlayingDTO.getCoverUrl()) != null && coverUrl.length() != 0) {
                forDocument = ImageLocation.getForPath(nowPlayingDTO.getCoverUrl());
            }
            final ImageLocation imageLocation = forDocument;
            if (imageLocation == null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.onDataLoaded(new NowPlayingCardData(nowPlayingDTO, null, null, null, null, 0L, 32, null));
                    }
                });
                return;
            }
            final ImageReceiver imageReceiver = new ImageReceiver(null);
            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            final Ref.ObjectRef objectRef = new Ref.ObjectRef();
            objectRef.element = new Runnable() { // from class: com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.create$finish(atomicBoolean, objectRef, imageReceiver, callback, new NowPlayingCardData(nowPlayingDTO, null, null, null, imageLocation, 0L, 32, null));
                }
            };
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.m1272$r8$lambda$AOf_1ZcjMCUyw5uKAO3aSljOk(imageReceiver, objectRef, imageLocation, nowPlayingDTO, atomicBoolean, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void create$finish(AtomicBoolean atomicBoolean, Ref.ObjectRef<Runnable> objectRef, ImageReceiver imageReceiver, Callback callback, NowPlayingCardData nowPlayingCardData) {
            if (atomicBoolean.compareAndSet(false, true)) {
                Runnable runnable = objectRef.element;
                AndroidUtilities.cancelRunOnUIThread(runnable == null ? null : runnable);
                imageReceiver.setDelegate(null);
                imageReceiver.onDetachedFromWindow();
                callback.onDataLoaded(nowPlayingCardData);
            }
        }

        /* JADX INFO: renamed from: $r8$lambda$AOf_1Z-cj-MCUyw5uKAO3aSljOk, reason: not valid java name */
        public static void m1272$r8$lambda$AOf_1ZcjMCUyw5uKAO3aSljOk(final ImageReceiver imageReceiver, final Ref.ObjectRef objectRef, final ImageLocation imageLocation, final NowPlayingDTO nowPlayingDTO, final AtomicBoolean atomicBoolean, final Callback callback) {
            imageReceiver.onAttachedToWindow();
            imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda3
                @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                    NowPlayingCardData.Companion.create$lambda$2$0(nowPlayingDTO, imageLocation, atomicBoolean, objectRef, imageReceiver, callback, imageReceiver2, z, z2, z3);
                }
            });
            Runnable runnable = (Runnable) objectRef.element;
            AndroidUtilities.runOnUIThread(runnable, 15000L);
            imageReceiver.setImage(imageLocation, null, null, null, null, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void create$lambda$2$0(final NowPlayingDTO nowPlayingDTO, final ImageLocation imageLocation, final AtomicBoolean atomicBoolean, final Ref.ObjectRef objectRef, final ImageReceiver imageReceiver, final Callback callback, ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
            if (!z || z2) {
                return;
            }
            final Bitmap bitmap = imageReceiver2.getBitmap();
            Utilities.themeQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.create$lambda$2$0$0(bitmap, nowPlayingDTO, imageLocation, atomicBoolean, objectRef, imageReceiver, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void create$lambda$2$0$0(final Bitmap bitmap, final NowPlayingDTO nowPlayingDTO, final ImageLocation imageLocation, final AtomicBoolean atomicBoolean, final Ref.ObjectRef objectRef, final ImageReceiver imageReceiver, final Callback callback) {
            Pair<Integer, Integer> pairExtractColors = NowPlayingCardData.INSTANCE.extractColors(bitmap);
            final Integer numComponent1 = pairExtractColors.component1();
            final Integer numComponent2 = pairExtractColors.component2();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData$Companion$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.create$lambda$2$0$0$0(nowPlayingDTO, numComponent1, numComponent2, bitmap, imageLocation, atomicBoolean, objectRef, imageReceiver, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void create$lambda$2$0$0$0(NowPlayingDTO nowPlayingDTO, Integer num, Integer num2, Bitmap bitmap, ImageLocation imageLocation, AtomicBoolean atomicBoolean, Ref.ObjectRef objectRef, ImageReceiver imageReceiver, Callback callback) {
            create$finish(atomicBoolean, objectRef, imageReceiver, callback, new NowPlayingCardData(nowPlayingDTO, num, num2, bitmap, imageLocation, 0L, 32, null));
        }

        private final Pair<Integer, Integer> extractColors(Bitmap bitmap) {
            int iIntValue;
            float f;
            float f2;
            if (bitmap == null) {
                return TuplesKt.to(null, null);
            }
            Palette paletteGenerate = Palette.from(bitmap).generate();
            Palette.Swatch darkVibrantSwatch = paletteGenerate.getDarkVibrantSwatch();
            if (darkVibrantSwatch != null) {
                iIntValue = darkVibrantSwatch.getRgb();
            } else {
                Palette.Swatch mutedSwatch = paletteGenerate.getMutedSwatch();
                if (mutedSwatch != null) {
                    iIntValue = mutedSwatch.getRgb();
                } else {
                    Palette.Swatch darkMutedSwatch = paletteGenerate.getDarkMutedSwatch();
                    Integer numValueOf = darkMutedSwatch != null ? Integer.valueOf(darkMutedSwatch.getRgb()) : null;
                    if (numValueOf != null) {
                        iIntValue = numValueOf.intValue();
                    } else {
                        Palette.Swatch dominantSwatch = paletteGenerate.getDominantSwatch();
                        Integer numValueOf2 = dominantSwatch != null ? Integer.valueOf(dominantSwatch.getRgb()) : null;
                        iIntValue = numValueOf2 != null ? numValueOf2.intValue() : AndroidUtilities.getDominantColor(bitmap);
                    }
                }
            }
            int iAdjustHsl$default = iIntValue;
            double dCalculateContrast = ColorUtils.calculateContrast(-1, iAdjustHsl$default);
            if (dCalculateContrast > 15.0d) {
                iAdjustHsl$default = UIUtil.adjustHsl$default(UIUtil.INSTANCE, iAdjustHsl$default, 2.0f, 0.0f, 4, null);
            } else if (dCalculateContrast < 10.0d) {
                iAdjustHsl$default = UIUtil.adjustHsl$default(UIUtil.INSTANCE, iAdjustHsl$default, 0.5f, 0.0f, 4, null);
            }
            if (ColorUtils.calculateContrast(-1, iAdjustHsl$default) < 3.0d) {
                iAdjustHsl$default = ColorUtils.blendARGB(iAdjustHsl$default, -16777216, 0.3f);
            }
            int i = iAdjustHsl$default;
            float[] fArr = new float[3];
            ColorUtils.colorToHSL(i, fArr);
            float f3 = fArr[2];
            if (0.0f <= f3 && f3 <= 0.25f) {
                f = 2.0f;
            } else if (0.25f <= f3 && f3 <= 0.5f) {
                f = 1.5f;
            } else if (0.5f <= f3 && f3 <= 0.75f) {
                f = 1.0f;
            } else {
                f = 0.5f;
            }
            return TuplesKt.to(Integer.valueOf(i), Integer.valueOf(UIUtil.adjustHsl$default(UIUtil.INSTANCE, i, f, 0.0f, 4, null)));
        }
    }
}
