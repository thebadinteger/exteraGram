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
        builder.setPositiveButton(LocaleController.formatString(R.string.ModelDownload, AndroidUtilities.formatFileSize(recognitionModel.getSize())), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                RecognitionModelDialogs.$r8$lambda$tdBu2yICZgtDsWF_TZy6toVAMk0(baseFragment, str, runnable, alertDialog, i);
            }
        });
        baseFragment.showDialog(builder.create());
    }

    public static /* synthetic */ void $r8$lambda$tdBu2yICZgtDsWF_TZy6toVAMk0(BaseFragment baseFragment, String str, Runnable runnable, AlertDialog alertDialog, int i) {
        baseFragment.dismissCurrentDialog();
        final LoadingModelView loadingModelView = new LoadingModelView(baseFragment.getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getContext());
        builder.setView(loadingModelView);
        final AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.setCancelable(false);
        final boolean[] zArr = {false};
        final float[] fArr = {0.0f};
        Runnable runnable2 = new Runnable() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                loadingModelView.setProgress(fArr[0]);
            }
        };
        final long[] jArr = {-1};
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                RecognitionModelDialogs.$r8$lambda$fjCtNl4Ch8ZiuwuQNilRrGjSNjE(zArr, jArr, alertDialogCreate);
            }
        }, 150L);
        VoiceRecognitionController.getInstance().downloadModel("vosk", str, new AnonymousClass1(fArr, runnable2, zArr, loadingModelView, jArr, alertDialogCreate, runnable, baseFragment));
    }

    public static /* synthetic */ void $r8$lambda$fjCtNl4Ch8ZiuwuQNilRrGjSNjE(boolean[] zArr, long[] jArr, AlertDialog alertDialog) {
        if (zArr[0]) {
            return;
        }
        jArr[0] = System.currentTimeMillis();
        alertDialog.show();
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$1, reason: invalid class name */
    public static class AnonymousClass1 implements VoiceRecognitionController.DownloadModelCallback {
        final /* synthetic */ AlertDialog val$alert;
        final /* synthetic */ boolean[] val$done;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ LoadingModelView val$loadingModelView;
        final /* synthetic */ Runnable val$onDownloaded;
        final /* synthetic */ float[] val$progressValue;
        final /* synthetic */ long[] val$start;
        final /* synthetic */ Runnable val$updateProgress;

        public AnonymousClass1(float[] fArr, Runnable runnable, boolean[] zArr, LoadingModelView loadingModelView, long[] jArr, AlertDialog alertDialog, Runnable runnable2, BaseFragment baseFragment) {
            this.val$progressValue = fArr;
            this.val$updateProgress = runnable;
            this.val$done = zArr;
            this.val$loadingModelView = loadingModelView;
            this.val$start = jArr;
            this.val$alert = alertDialog;
            this.val$onDownloaded = runnable2;
            this.val$fragment = baseFragment;
        }

        @Override // com.exteragram.messenger.speech.VoiceRecognitionController.DownloadModelCallback
        public void onProgress(float f) {
            this.val$progressValue[0] = f;
            AndroidUtilities.cancelRunOnUIThread(this.val$updateProgress);
            AndroidUtilities.runOnUIThread(this.val$updateProgress);
        }

        @Override // com.exteragram.messenger.speech.VoiceRecognitionController.DownloadModelCallback
        public void onCompleted() {
            final boolean[] zArr = this.val$done;
            final LoadingModelView loadingModelView = this.val$loadingModelView;
            final long[] jArr = this.val$start;
            final AlertDialog alertDialog = this.val$alert;
            final Runnable runnable = this.val$onDownloaded;
            final BaseFragment baseFragment = this.val$fragment;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RecognitionModelDialogs.AnonymousClass1.m1464$r8$lambda$zu5DB9QxB0rwh9aFNuA5R184NE(zArr, loadingModelView, jArr, alertDialog, runnable, baseFragment);
                }
            });
        }

        /* JADX INFO: renamed from: $r8$lambda$zu5DB9QxB0rwh9aFNuA5R18-4NE, reason: not valid java name */
        public static /* synthetic */ void m1464$r8$lambda$zu5DB9QxB0rwh9aFNuA5R184NE(boolean[] zArr, LoadingModelView loadingModelView, long[] jArr, AlertDialog alertDialog, Runnable runnable, BaseFragment baseFragment) {
            zArr[0] = true;
            loadingModelView.setProgress(1.0f);
            if (jArr[0] > 0) {
                Objects.requireNonNull(alertDialog);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                }, Math.max(0L, 1000 - (System.currentTimeMillis() - jArr[0])));
            } else {
                alertDialog.dismiss();
            }
            runnable.run();
            BulletinFactory.of(baseFragment).createSuccessBulletin(LocaleController.getString(R.string.ModelDownloaded)).show();
        }

        @Override // com.exteragram.messenger.speech.VoiceRecognitionController.DownloadModelCallback
        public void onError(Exception exc) {
            final AlertDialog alertDialog = this.val$alert;
            final BaseFragment baseFragment = this.val$fragment;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RecognitionModelDialogs.AnonymousClass1.m1463$r8$lambda$wtSpJQDGE_ZiiupiOJe_hPsXUc(alertDialog, baseFragment);
                }
            });
        }

        /* JADX INFO: renamed from: $r8$lambda$wtSpJQ-DGE_ZiiupiOJe_hPsXUc, reason: not valid java name */
        public static /* synthetic */ void m1463$r8$lambda$wtSpJQDGE_ZiiupiOJe_hPsXUc(AlertDialog alertDialog, BaseFragment baseFragment) {
            alertDialog.dismiss();
            BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.ModelError)).show();
        }
    }

    public static void showDeleteFlow(final BaseFragment baseFragment, final List<VoiceRecognitionController.RecognitionModel> list, final Utilities.Callback<VoiceRecognitionController.RecognitionModel> callback) {
        if (baseFragment.getContext() == null || list.isEmpty()) {
            return;
        }
        if (list.size() == 1) {
            showDeleteConfirmDialog(baseFragment, list.get(0), callback);
            return;
        }
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<VoiceRecognitionController.RecognitionModel> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(getRecognitionLanguageOption(it.next().getLanguage()));
        }
        PopupUtils.showDialogWithoutRadio(arrayList, LocaleController.getString(R.string.DeleteRecognitionModel), baseFragment.getContext(), new PopupUtils.OnItemClickListener() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$$ExternalSyntheticLambda0
            @Override // com.exteragram.messenger.utils.ui.PopupUtils.OnItemClickListener
            public final void onClick(int i) {
                RecognitionModelDialogs.$r8$lambda$eBXgfm79L1j0AVJI557h5mvN69s(list, baseFragment, callback, i);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$eBXgfm79L1j0AVJI557h5mvN69s(List list, BaseFragment baseFragment, Utilities.Callback callback, int i) {
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
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$$ExternalSyntheticLambda1
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

    /* JADX INFO: renamed from: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$2, reason: invalid class name */
    public static class AnonymousClass2 implements VoiceRecognitionController.DeleteModelCallback {
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ VoiceRecognitionController.RecognitionModel val$model;
        final /* synthetic */ Utilities.Callback val$onDeleted;

        public AnonymousClass2(Utilities.Callback callback, VoiceRecognitionController.RecognitionModel recognitionModel, BaseFragment baseFragment) {
            this.val$onDeleted = callback;
            this.val$model = recognitionModel;
            this.val$fragment = baseFragment;
        }

        @Override // com.exteragram.messenger.speech.VoiceRecognitionController.DeleteModelCallback
        public void onCompleted() {
            final Utilities.Callback callback = this.val$onDeleted;
            final VoiceRecognitionController.RecognitionModel recognitionModel = this.val$model;
            final BaseFragment baseFragment = this.val$fragment;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RecognitionModelDialogs.AnonymousClass2.$r8$lambda$iCzzdvgxoGUtZCylTyNKqb7oGIc(callback, recognitionModel, baseFragment);
                }
            });
        }

        public static /* synthetic */ void $r8$lambda$iCzzdvgxoGUtZCylTyNKqb7oGIc(Utilities.Callback callback, VoiceRecognitionController.RecognitionModel recognitionModel, BaseFragment baseFragment) {
            callback.run(recognitionModel);
            BulletinFactory.of(baseFragment).createSuccessBulletin(LocaleController.getString(R.string.RecognitionModelDeleted)).show();
        }

        @Override // com.exteragram.messenger.speech.VoiceRecognitionController.DeleteModelCallback
        public void onError(Exception exc) {
            final BaseFragment baseFragment = this.val$fragment;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.ui.RecognitionModelDialogs$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.RecognitionModelDeleteError)).show();
                }
            });
        }
    }
}
