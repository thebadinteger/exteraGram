package com.exteragram.messenger.export.output.htmlAndJson;

import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.api.ApiWrap$ContactsList;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$DialogsInfo;
import com.exteragram.messenger.export.api.ApiWrap$ExportPersonalInfo;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$MessagesSlice;
import com.exteragram.messenger.export.api.ApiWrap$SessionsList;
import com.exteragram.messenger.export.api.ApiWrap$StoriesSlice;
import com.exteragram.messenger.export.api.ApiWrap$UserpicsInfo;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.Utilities;

public class HtmlAndJsonWriter extends AbstractWriter {
    private final ArrayList<AbstractWriter> _writers;

    public HtmlAndJsonWriter() {
        ArrayList<AbstractWriter> arrayList = new ArrayList<>();
        this._writers = arrayList;
        arrayList.add(AbstractWriter.CreateWriter(AbstractWriter.Format.Html));
        arrayList.add(AbstractWriter.CreateWriter(AbstractWriter.Format.Json));
    }

    private AbstractWriter.Result invoke(Utilities.CallbackReturn<AbstractWriter, AbstractWriter.Result> callbackReturn) {
        AbstractWriter.Result result = new AbstractWriter.Result(AbstractWriter.Result.Type.Success, _UrlKt.FRAGMENT_ENCODE_SET);
        ArrayList<AbstractWriter> arrayList = this._writers;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            AbstractWriter abstractWriter = arrayList.get(i);
            i++;
            AbstractWriter.Result resultRun = callbackReturn.run(abstractWriter);
            if (!resultRun.isSuccess()) {
                result = resultRun;
            }
        }
        return result;
    }

    @Override 
    public String mainFilePath() {
        return _UrlKt.FRAGMENT_ENCODE_SET;
    }

    @Override 
    public AbstractWriter.Result start(final ExportSettings exportSettings, final OutputFile.Stats stats) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).start(exportSettings, stats);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writePersonal(final ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writePersonal(apiWrap$ExportPersonalInfo);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeDialogsStart(final ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeDialogsStart(apiWrap$DialogsInfo);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeDialogStart(final ApiWrap$DialogInfo apiWrap$DialogInfo) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeDialogStart(apiWrap$DialogInfo);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeDialogSlice(final ApiWrap$MessagesSlice apiWrap$MessagesSlice) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeDialogSlice(apiWrap$MessagesSlice);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeDialogEnd() {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeDialogEnd();
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeDialogsEnd() {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeDialogsEnd();
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeSessionsList(final ApiWrap$SessionsList apiWrap$SessionsList) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeSessionsList(apiWrap$SessionsList);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeUserpicsStart(final ApiWrap$UserpicsInfo apiWrap$UserpicsInfo) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeUserpicsStart(apiWrap$UserpicsInfo);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeUserpicsSlice(final ArrayList<HtmlWriter.Photo> arrayList) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeUserpicsSlice(arrayList);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeUserpicsEnd() {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeUserpicsEnd();
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeStoriesStart(final int i) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeStoriesStart(i);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeStoriesSlice(final ApiWrap$StoriesSlice apiWrap$StoriesSlice) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeStoriesSlice(apiWrap$StoriesSlice);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeStoriesEnd() {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeStoriesEnd();
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeContactsList(final ApiWrap$ContactsList apiWrap$ContactsList) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeContactsList(apiWrap$ContactsList);
            }
        });
    }

    @Override 
    public AbstractWriter.Result writeOtherData(final ApiWrap$File apiWrap$File) {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).writeOtherData(apiWrap$File);
            }
        });
    }

    @Override 
    public AbstractWriter.Result finish() {
        return invoke(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return ((AbstractWriter) obj).finish();
            }
        });
    }
}
