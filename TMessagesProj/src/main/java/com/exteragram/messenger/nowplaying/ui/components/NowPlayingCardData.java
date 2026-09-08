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
public final @JvmStatic
        public final void create(final NowPlayingDTO nowPlayingDTO, TLRPC.Document savedMusic, final Callback callback) {
            String coverUrl;
            TLRPC.PhotoSize closestPhotoSizeWithSize;
            ImageLocation forDocument = (!Intrinsics.areEqual(nowPlayingDTO.getPlatform(), "TELEGRAM") || savedMusic == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(savedMusic.thumbs, 1000)) == null) ? null : ImageLocation.getForDocument(closestPhotoSizeWithSize, savedMusic);
            if (forDocument == null && (coverUrl = nowPlayingDTO.getCoverUrl()) != null && coverUrl.length() != 0) {
                forDocument = ImageLocation.getForPath(nowPlayingDTO.getCoverUrl());
            }
            final ImageLocation imageLocation = forDocument;
            if (imageLocation == null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
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
            objectRef.element = new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.create$finish(atomicBoolean, objectRef, imageReceiver, callback, new NowPlayingCardData(nowPlayingDTO, null, null, null, imageLocation, 0L, 32, null));
                }
            };
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.m1272$r8$lambda$AOf_1ZcjMCUyw5uKAO3aSljOk(imageReceiver, objectRef, imageLocation, nowPlayingDTO, atomicBoolean, callback);
                }
            });
        }

        public static final void create$finish(AtomicBoolean atomicBoolean, Ref.ObjectRef<Runnable> objectRef, ImageReceiver imageReceiver, Callback callback, NowPlayingCardData nowPlayingCardData) {
            if (atomicBoolean.compareAndSet(false, true)) {
                Runnable runnable = objectRef.element;
                AndroidUtilities.cancelRunOnUIThread(runnable == null ? null : runnable);
                imageReceiver.setDelegate(null);
                imageReceiver.onDetachedFromWindow();
                callback.onDataLoaded(nowPlayingCardData);
            }
        }

        public static void m1272$r8$lambda$AOf_1ZcjMCUyw5uKAO3aSljOk(final ImageReceiver imageReceiver, final Ref.ObjectRef objectRef, final ImageLocation imageLocation, final NowPlayingDTO nowPlayingDTO, final AtomicBoolean atomicBoolean, final Callback callback) {
            imageReceiver.onAttachedToWindow();
            imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { 
                @Override 
                public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                    NowPlayingCardData.Companion.create$lambda$2$0(nowPlayingDTO, imageLocation, atomicBoolean, objectRef, imageReceiver, callback, imageReceiver2, z, z2, z3);
                }
            });
            T t = objectRef.element;
            AndroidUtilities.runOnUIThread(t == 0 ? null : (Runnable) t, 15000L);
            imageReceiver.setImage(imageLocation, null, null, null, null, 0);
        }

        public static final void create$lambda$2$0(final NowPlayingDTO nowPlayingDTO, final ImageLocation imageLocation, final AtomicBoolean atomicBoolean, final Ref.ObjectRef objectRef, final ImageReceiver imageReceiver, final Callback callback, ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
            if (!z || z2) {
                return;
            }
            final Bitmap bitmap = imageReceiver2.getBitmap();
            Utilities.themeQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.create$lambda$2$0$0(bitmap, nowPlayingDTO, imageLocation, atomicBoolean, objectRef, imageReceiver, callback);
                }
            });
        }

        public static final void create$lambda$2$0$0(final Bitmap bitmap, final NowPlayingDTO nowPlayingDTO, final ImageLocation imageLocation, final AtomicBoolean atomicBoolean, final Ref.ObjectRef objectRef, final ImageReceiver imageReceiver, final Callback callback) {
            Pair<Integer, Integer> pairExtractColors = NowPlayingCardData.INSTANCE.extractColors(bitmap);
            final Integer numComponent1 = pairExtractColors.component1();
            final Integer numComponent2 = pairExtractColors.component2();
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingCardData.Companion.create$lambda$2$0$0$0(nowPlayingDTO, numComponent1, numComponent2, bitmap, imageLocation, atomicBoolean, objectRef, imageReceiver, callback);
                }
            });
        }

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
                f2 = 2.0f;
            } else {
                if (0.25f > f3 || f3 > 0.5f) {
                    if (0.5f > f3 || f3 > 0.75f) {
                        f = 0.5f;
                    } else {
                        f2 = 1.0f;
                    }
                    return TuplesKt.to(Integer.valueOf(i), Integer.valueOf(UIUtil.adjustHsl$default(UIUtil.INSTANCE, i, f, 0.0f, 4, null)));
                }
                f2 = 1.5f;
            }
            f = f2;
            return TuplesKt.to(Integer.valueOf(i), Integer.valueOf(UIUtil.adjustHsl$default(UIUtil.INSTANCE, i, f, 0.0f, 4, null)));
        }
    }
}
