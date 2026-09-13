package com.exteragram.messenger.plugins.hooks;

import com.exteragram.messenger.utils.AppUtils;
import de.robv.android.xposed.XC_MethodHook;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;

@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u0000 22\u00020\u0001:\u00012B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020)J\u0010\u0010-\u001a\u00020\u000b2\u0006\u0010.\u001a\u00020\u0003H\u0002J\u001c\u0010/\u001a\u00020)2\b\u00100\u001a\u0004\u0018\u00010\u00012\b\u00101\u001a\u0004\u0018\u00010\u0001H\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\r\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u0012\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R.\u0010\u0013\u001a\u0016\u0012\u0004\u0012\u00020\u0000\u0018\u00010\u0014j\n\u0012\u0004\u0012\u00020\u0000\u0018\u0001`\u0015X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001c\u0010\u001a\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0007\"\u0004\b\u001c\u0010\u0005R \u0010\u001d\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u001eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001c\u0010#\u001a\u0004\u0018\u00010\u0001X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'¨\u00063"}, d2 = {"Lcom/exteragram/messenger/plugins/hooks/HookFilter;", _UrlKt.FRAGMENT_ENCODE_SET, "filterType", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "getFilterType", "()Ljava/lang/String;", "typeId", _UrlKt.FRAGMENT_ENCODE_SET, "compiledExpression", "Ljava/io/Serializable;", "compiledExpressionKey", "argIndex", "getArgIndex", "()Ljava/lang/Integer;", "setArgIndex", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "orFilters", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "getOrFilters", "()Ljava/util/ArrayList;", "setOrFilters", "(Ljava/util/ArrayList;)V", "mvelExpression", "getMvelExpression", "setMvelExpression", "instanceOf", "Ljava/lang/Class;", "getInstanceOf", "()Ljava/lang/Class;", "setInstanceOf", "(Ljava/lang/Class;)V", "object", "getObject", "()Ljava/lang/Object;", "setObject", "(Ljava/lang/Object;)V", "execute", _UrlKt.FRAGMENT_ENCODE_SET, "param", "Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;", "isBefore", "getCompiledExpression", "expression", "valuesEqual", "a", "b", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class HookFilter {
    private static final int TYPE_ARGUMENT_EQUAL = 3;
    private static final int TYPE_ARGUMENT_IS_FALSE = 6;
    private static final int TYPE_ARGUMENT_IS_INSTANCE_OF = 5;
    private static final int TYPE_ARGUMENT_IS_NULL = 7;
    private static final int TYPE_ARGUMENT_IS_TRUE = 8;
    private static final int TYPE_ARGUMENT_NOT_EQUAL = 4;
    private static final int TYPE_ARGUMENT_NOT_NULL = 9;
    private static final int TYPE_CONDITION = 2;
    private static final int TYPE_OR = 1;
    private static final int TYPE_RESULT_EQUAL = 10;
    private static final int TYPE_RESULT_IS_FALSE = 12;
    private static final int TYPE_RESULT_IS_INSTANCE_OF = 13;
    private static final int TYPE_RESULT_IS_NULL = 14;
    private static final int TYPE_RESULT_IS_TRUE = 15;
    private static final int TYPE_RESULT_NOT_EQUAL = 11;
    private static final int TYPE_RESULT_NOT_NULL = 16;
    private static final int TYPE_UNKNOWN = 0;
    private Integer argIndex;
    private volatile Serializable compiledExpression;
    private volatile String compiledExpressionKey;
    private final String filterType;
    private Class<?> instanceOf;
    private String mvelExpression;
    private Object object;
    private ArrayList<HookFilter> orFilters;
    private final int typeId;

    public static final Companion INSTANCE = new Companion(null);
    private static final ConcurrentHashMap<String, Serializable> mvelExpressionCache = new ConcurrentHashMap<>();

    public HookFilter(String str) {
        this.filterType = str;
        this.typeId = INSTANCE.typeIdFor(str);
    }

    public final String getFilterType() {
        return this.filterType;
    }

    public final Integer getArgIndex() {
        return this.argIndex;
    }

    public final void setArgIndex(Integer num) {
        this.argIndex = num;
    }

    public final ArrayList<HookFilter> getOrFilters() {
        return this.orFilters;
    }

    public final void setOrFilters(ArrayList<HookFilter> arrayList) {
        this.orFilters = arrayList;
    }

    public final String getMvelExpression() {
        return this.mvelExpression;
    }

    public final void setMvelExpression(String str) {
        this.mvelExpression = str;
    }

    public final Class<?> getInstanceOf() {
        return this.instanceOf;
    }

    public final void setInstanceOf(Class<?> cls) {
        this.instanceOf = cls;
    }

    public final Object getObject() {
        return this.object;
    }

    public final void setObject(Object obj) {
        this.object = obj;
    }

    public final boolean execute(XC_MethodHook.MethodHookParam param, boolean isBefore) {
        Boolean bool;
        try {
            switch (this.typeId) {
                case 1:
                    ArrayList<HookFilter> arrayList = this.orFilters;
                    if (arrayList != null) {
                        for (HookFilter hookFilter : arrayList) {
                            if (hookFilter.execute(param, isBefore)) {
                                return true;
                            }
                        }
                    }
                    return false;
                case 2:
                    HashMap<String, Object> map = new HashMap<>(4);
                    map.put("param", param);
                    map.put("result", isBefore ? null : param.getResult());
                    map.put("object", this.object);
                    String str = this.mvelExpression;
                    if (str == null || (bool = (Boolean) MVEL.executeExpression(getCompiledExpression(str), param.thisObject, map, Boolean.TYPE)) == null) {
                        return false;
                    }
                    return bool.booleanValue();
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    Integer num = this.argIndex;
                    Object[] objArr = param.args;
                    if (num != null) {
                        int length = objArr.length;
                        int iIntValue = num.intValue();
                        if (iIntValue >= 0 && iIntValue < length) {
                            Object obj = objArr[num.intValue()];
                            switch (this.typeId) {
                                case 3:
                                    return valuesEqual(obj, this.object);
                                case 4:
                                    return !valuesEqual(obj, this.object);
                                case 5:
                                    Class<?> cls = this.instanceOf;
                                    return cls != null && cls.isInstance(obj);
                                case 6:
                                    return (obj instanceof Boolean) && !((Boolean) obj).booleanValue();
                                case 7:
                                    return obj == null;
                                case 8:
                                    return (obj instanceof Boolean) && ((Boolean) obj).booleanValue();
                                case 9:
                                    return obj != null;
                                default:
                                    return false;
                            }
                        }
                    }
                    return false;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                    if (isBefore) {
                        return false;
                    }
                    Object result = param.getResult();
                    switch (this.typeId) {
                        case 10:
                            return valuesEqual(result, this.object);
                        case 11:
                            return !valuesEqual(result, this.object);
                        case 12:
                            return (result instanceof Boolean) && !((Boolean) result).booleanValue();
                        case 13:
                            Class<?> cls2 = this.instanceOf;
                            return cls2 != null && cls2.isInstance(result);
                        case 14:
                            return result == null;
                        case 15:
                            return (result instanceof Boolean) && ((Boolean) result).booleanValue();
                        case 16:
                            return result != null;
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        } catch (Exception e) {
            AppUtils.log(e);
            return false;
        }
    }

    private final Serializable getCompiledExpression(String expression) {
        Serializable serializable = this.compiledExpression;
        if (serializable != null && Intrinsics.areEqual(this.compiledExpressionKey, expression)) {
            return serializable;
        }
        ConcurrentHashMap<String, Serializable> concurrentHashMap = mvelExpressionCache;
        Serializable serializableComputeIfAbsent = concurrentHashMap.computeIfAbsent(expression, s -> MVEL.compileExpression(s));
        Serializable serializable2 = serializableComputeIfAbsent;
        this.compiledExpression = serializable2;
        this.compiledExpressionKey = expression;
        return serializable2;
    }

    private final boolean valuesEqual(Object a2, Object b2) {
        if (Intrinsics.areEqual(a2, b2)) {
            return true;
        }
        if ((a2 instanceof Number) && (b2 instanceof Number)) {
            if (!(a2 instanceof Double) && !(a2 instanceof Float) && !(b2 instanceof Double) && !(b2 instanceof Float)) {
                return ((Number) a2).longValue() == ((Number) b2).longValue();
            }
            if (((Number) a2).doubleValue() == ((Number) b2).doubleValue()) {
                return true;
            }
        }
        return false;
    }

    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final int typeIdFor(String filterType) {
            switch (filterType.hashCode()) {
                case -1842277382:
                    return !filterType.equals("argument_is_null") ? 0 : 7;
                case -1842101247:
                    return !filterType.equals("argument_is_true") ? 0 : 8;
                case -1369450155:
                    return !filterType.equals("result_not_null") ? 0 : 16;
                case -1284007664:
                    return !filterType.equals("argument_is_false") ? 0 : 6;
                case -1248106702:
                    return !filterType.equals("argument_equal") ? 0 : 3;
                case -861311717:
                    return !filterType.equals("condition") ? 0 : 2;
                case -170795378:
                    return !filterType.equals("result_is_instance_of") ? 0 : 13;
                case 3555:
                    return !filterType.equals("or") ? 0 : 1;
                case 180205237:
                    return !filterType.equals("argument_not_null") ? 0 : 9;
                case 488295718:
                    return !filterType.equals("result_not_equal") ? 0 : 11;
                case 516769938:
                    return !filterType.equals("result_equal") ? 0 : 10;
                case 1282972614:
                    return !filterType.equals("argument_not_equal") ? 0 : 4;
                case 1461304240:
                    return !filterType.equals("result_is_false") ? 0 : 12;
                case 1877750766:
                    return !filterType.equals("argument_is_instance_of") ? 0 : 5;
                case 1987059034:
                    return !filterType.equals("result_is_null") ? 0 : 14;
                case 1987235169:
                    return !filterType.equals("result_is_true") ? 0 : 15;
                default:
                    return 0;
            }
        }
    }
}
