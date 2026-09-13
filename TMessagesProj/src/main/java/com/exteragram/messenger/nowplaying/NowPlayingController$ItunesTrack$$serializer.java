package com.exteragram.messenger.nowplaying;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.builtins.BuiltinSerializersKt;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.LongSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0003\u0010\u0004J\u0019\u0010\u0005\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0006H\u0086\u0080\u0004¢\u0006\u0002\u0010\bJ\u0012\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u000bH\u0086\u0080\u0004J\u001a\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002H\u0086\u0080\u0004R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015"}, d2 = {"com/exteragram/messenger/nowplaying/NowPlayingController.ItunesTrack.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;", "<init>", "()V", "childSerializers", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", _UrlKt.FRAGMENT_ENCODE_SET, "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@Deprecated(level = DeprecationLevel.HIDDEN, message = "This synthesized declaration should not be used directly")
public final /* synthetic */ class NowPlayingController$ItunesTrack$$serializer implements GeneratedSerializer<NowPlayingController.ItunesTrack> {
    public static final NowPlayingController$ItunesTrack$$serializer INSTANCE;
    private static final SerialDescriptor descriptor;

    @Override // kotlinx.serialization.KSerializer, kotlinx.serialization.SerializationStrategy, kotlinx.serialization.DeserializationStrategy
    public final SerialDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        NowPlayingController$ItunesTrack$$serializer nowPlayingController$ItunesTrack$$serializer = new NowPlayingController$ItunesTrack$$serializer();
        INSTANCE = nowPlayingController$ItunesTrack$$serializer;
        PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("com.exteragram.messenger.nowplaying.NowPlayingController.ItunesTrack", nowPlayingController$ItunesTrack$$serializer, 6);
        pluginGeneratedSerialDescriptor.addElement("trackName", true);
        pluginGeneratedSerialDescriptor.addElement("artistName", true);
        pluginGeneratedSerialDescriptor.addElement("collectionName", true);
        pluginGeneratedSerialDescriptor.addElement("artworkUrl100", true);
        pluginGeneratedSerialDescriptor.addElement("previewUrl", true);
        pluginGeneratedSerialDescriptor.addElement("trackTimeMillis", true);
        descriptor = pluginGeneratedSerialDescriptor;
    }

    private NowPlayingController$ItunesTrack$$serializer() {
    }

    @Override // kotlinx.serialization.internal.GeneratedSerializer
    public final KSerializer<?>[] childSerializers() {
        StringSerializer stringSerializer = StringSerializer.INSTANCE;
        return new KSerializer[]{BuiltinSerializersKt.getNullable(stringSerializer), BuiltinSerializersKt.getNullable(stringSerializer), BuiltinSerializersKt.getNullable(stringSerializer), BuiltinSerializersKt.getNullable(stringSerializer), BuiltinSerializersKt.getNullable(stringSerializer), BuiltinSerializersKt.getNullable(LongSerializer.INSTANCE)};
    }

    @Override // kotlinx.serialization.internal.GeneratedSerializer
    public KSerializer<?>[] typeParametersSerializers() {
        return GeneratedSerializer.DefaultImpls.typeParametersSerializers(this);
    }

    @Override // kotlinx.serialization.DeserializationStrategy
    public final NowPlayingController.ItunesTrack deserialize(Decoder decoder) {
        int i;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        Long l;
        SerialDescriptor serialDescriptor = descriptor;
        CompositeDecoder compositeDecoderBeginStructure = decoder.beginStructure(serialDescriptor);
        int i2 = 5;
        String str6 = null;
        if (compositeDecoderBeginStructure.decodeSequentially()) {
            StringSerializer stringSerializer = StringSerializer.INSTANCE;
            String str7 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 0, stringSerializer, null);
            String str8 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 1, stringSerializer, null);
            String str9 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 2, stringSerializer, null);
            String str10 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 3, stringSerializer, null);
            str5 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 4, stringSerializer, null);
            l = (Long) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 5, LongSerializer.INSTANCE, null);
            str4 = str10;
            i = 63;
            str3 = str9;
            str2 = str8;
            str = str7;
        } else {
            boolean z = true;
            int i3 = 0;
            String str11 = null;
            String str12 = null;
            String str13 = null;
            String str14 = null;
            Long l2 = null;
            while (z) {
                int iDecodeElementIndex = compositeDecoderBeginStructure.decodeElementIndex(serialDescriptor);
                switch (iDecodeElementIndex) {
                    case -1:
                        z = false;
                        i2 = 5;
                        break;
                    case 0:
                        str6 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 0, StringSerializer.INSTANCE, str6);
                        i3 |= 1;
                        i2 = 5;
                        break;
                    case 1:
                        str11 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 1, StringSerializer.INSTANCE, str11);
                        i3 |= 2;
                        break;
                    case 2:
                        str12 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 2, StringSerializer.INSTANCE, str12);
                        i3 |= 4;
                        break;
                    case 3:
                        str13 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 3, StringSerializer.INSTANCE, str13);
                        i3 |= 8;
                        break;
                    case 4:
                        str14 = (String) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, 4, StringSerializer.INSTANCE, str14);
                        i3 |= 16;
                        break;
                    case 5:
                        l2 = (Long) compositeDecoderBeginStructure.decodeNullableSerializableElement(serialDescriptor, i2, LongSerializer.INSTANCE, l2);
                        i3 |= 32;
                        break;
                    default:
                        throw new UnknownFieldException(iDecodeElementIndex);
                }
            }
            i = i3;
            str = str6;
            str2 = str11;
            str3 = str12;
            str4 = str13;
            str5 = str14;
            l = l2;
        }
        compositeDecoderBeginStructure.endStructure(serialDescriptor);
        return new NowPlayingController.ItunesTrack(i, str, str2, str3, str4, str5, l, null);
    }

    @Override // kotlinx.serialization.SerializationStrategy
    public final void serialize(Encoder encoder, NowPlayingController.ItunesTrack value) {
        SerialDescriptor serialDescriptor = descriptor;
        CompositeEncoder compositeEncoderBeginStructure = encoder.beginStructure(serialDescriptor);
        NowPlayingController.ItunesTrack.write$Self$TMessagesProj(value, compositeEncoderBeginStructure, serialDescriptor);
        compositeEncoderBeginStructure.endStructure(serialDescriptor);
    }

}
