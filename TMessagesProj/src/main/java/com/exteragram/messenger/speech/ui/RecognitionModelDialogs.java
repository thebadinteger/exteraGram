package com.exteragram.messenger.speech.ui;

import android.widget.TextView;
import com.exteragram.messenger.speech.VoiceRecognitionController;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.exteragram.messenger.utils.ui.PopupUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;

public abstract class RecognitionModelDialogs {
    public static CharSequence getRecognitionLanguageOption(String str) {
        String languageTitleSystem = TranslatorUtils.getLanguageTitleSystem(str);
        String languageDisplayName = TranslatorUtils.getLanguageDisplayName(str);
        if (languageDisplayName == null) {
            return languageTitleSystem;
        }
        return languageTitleSystem + " - " + languageDisplayName;
    }

    public static void showDownloadDialog(final BaseFragment baseFragment, final String str, VoiceRecognitionController.RecognitionModel recognitionModel, final Runnable runnable) {
        if (baseFragment.getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getContext());
        builder.setTitle(LocaleController.getString(R.string.MissingLanguageModel));
        builder.setSubtitle(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.ModelDownloadInfo, TranslatorUtils.getLanguageTitleSystem(str))));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.formatString(R.string.ModelDownload, AndroidUtilities.formatFileSize(recognitionModel.getSize())), new AlertDialog.OnButtonClickListener() { 
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                RecognitionModelDialogs.$r8$lambda$tdBu2yICZgtDsWF_TZy6toVAMk0(baseFragment, str, runnable, alertDialog, i);
            }
        });
        baseFragment.showDialog(builder.create());
    }

    public static void $r8$lambda$fjCtNl4Ch8ZiuwuQNilRrGjSNjE(boolean[] zArr, long[] jArr, AlertDialog alertDialog) {
        if (zArr[0]) {
            return;
        }
        jArr[0] = System.currentTimeMillis();
        alertDialog.show();
    }

    public class AnonymousClass1 implements VoiceRecognitionController.DownloadModelCallback {
        final void m1464$r8$lambda$zu5DB9QxB0rwh9aFNuA5R184NE(boolean[] zArr, LoadingModelView loadingModelView, long[] jArr, AlertDialog alertDialog, Runnable runnable, BaseFragment baseFragment) {
            zArr[0] = true;
            loadingModelView.setProgress(1.0f);
            if (jArr[0] > 0) {
                Objects.requireNonNull(alertDialog);
                AndroidUtilities.runOnUIThread(new RecognitionModelDialogs$1$$ExternalSyntheticLambda0(alertDialog), Math.max(0L, 1000 - (System.currentTimeMillis() - jArr[0])));
            } else {
                alertDialog.dismiss();
            }
            runnable.run();
            BulletinFactory.of(baseFragment).createSuccessBulletin(LocaleController.getString(R.string.ModelDownloaded)).show();
        }

        @Override 
        public void onError(Exception exc) {
            final AlertDialog alertDialog = this.val$alert;
            final BaseFragment baseFragment = this.val$fragment;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    RecognitionModelDialogs.AnonymousClass1.m1463$r8$lambda$wtSpJQDGE_ZiiupiOJe_hPsXUc(alertDialog, baseFragment);
                }
            });
        }

        public static void $r8$lambda$eBXgfm79L1j0AVJI557h5mvN69s(List list, BaseFragment baseFragment, Utilities.Callback callback, int i) {
        if (i < 0 || i >= list.size()) {
            return;
        }
        showDeleteConfirmDialog(baseFragment, (VoiceRecognitionController.RecognitionModel) list.get(i), callback);
    }

    private static void showDeleteConfirmDialog(final BaseFragment baseFragment, final VoiceRecognitionController.RecognitionModel recognitionModel, final Utilities.Callback<VoiceRecognitionController.RecognitionModel> callback) {
        if (baseFragment.getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getContext());
        builder.setTitle(LocaleController.getString(R.string.DeleteRecognitionModel));
        builder.setSubtitle(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeleteRecognitionModelInfo, getRecognitionLanguageOption(recognitionModel.getLanguage()))));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { 
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                VoiceRecognitionController.RecognitionModel recognitionModel2 = recognitionModel;
                VoiceRecognitionController.getInstance().deleteModel("vosk", recognitionModel2.getLanguage(), new RecognitionModelDialogs.AnonymousClass2(callback, recognitionModel2, baseFragment));
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        baseFragment.showDialog(alertDialogCreate);
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public class AnonymousClass2 implements VoiceRecognitionController.DeleteModelCallback {
        final void $r8$lambda$iCzzdvgxoGUtZCylTyNKqb7oGIc(Utilities.Callback callback, VoiceRecognitionController.RecognitionModel recognitionModel, BaseFragment baseFragment) {
            callback.run(recognitionModel);
            BulletinFactory.of(baseFragment).createSuccessBulletin(LocaleController.getString(R.string.RecognitionModelDeleted)).show();
        }

        @Override 
        public void onError(Exception exc) {
            final BaseFragment baseFragment = this.val$fragment;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.RecognitionModelDeleteError)).show();
                }
            });
        }
    }
}
