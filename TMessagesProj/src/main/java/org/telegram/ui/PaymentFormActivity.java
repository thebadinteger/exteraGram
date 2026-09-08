PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequest.toString());
                if (request != null) {
                    AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request), getParentActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
                }
            } catch (JSONException e) {
                FileLog.e(e);
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setWeightSum(2);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setDuplicateParentStateEnabled(true);
        googlePayButton.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setDuplicateParentStateEnabled(true);
        imageView.setImageResource(R.drawable.buy_with_googlepay_button_content);
        linearLayout.addView(imageView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 0, 1.0f));

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setDuplicateParentStateEnabled(true);
        imageView.setImageResource(R.drawable.googlepay_button_overlay);
        googlePayButton.addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void updatePasswordFields() {
        if (currentStep != STEP_SET_PASSWORD_EMAIL || bottomCell[2] == null) {
            return;
        }
        doneItem.setVisibility(View.VISIBLE);
        if (currentPassword == null) {
            showEditDoneProgress(true, true);
            bottomCell[2].setVisibility(View.GONE);
            settingsCell[0].setVisibility(View.GONE);
            settingsCell[1].setVisibility(View.GONE);
            codeFieldCell.setVisibility(View.GONE);
            headerCell[0].setVisibility(View.GONE);
            headerCell[1].setVisibility(View.GONE);
            bottomCell[0].setVisibility(View.GONE);
            for (int a = 0; a < FIELDS_COUNT_PASSWORD; a++) {
                ((View) inputFields[a].getParent()).setVisibility(View.GONE);
            }
            for (int a = 0; a < dividers.size(); a++) {
                dividers.get(a).setVisibility(View.GONE);
            }
        } else {
            showEditDoneProgress(true, false);
            if (waitingForEmail) {
                bottomCell[2].setText(LocaleController.formatString("EmailPasswordConfirmText2", R.string.EmailPasswordConfirmText2, currentPassword.email_unconfirmed_pattern != null ? currentPassword.email_unconfirmed_pattern : ""));
                bottomCell[2].setVisibility(View.VISIBLE);
                settingsCell[0].setVisibility(View.VISIBLE);
                settingsCell[1].setVisibility(View.VISIBLE);
                codeFieldCell.setVisibility(View.VISIBLE);
                bottomCell[1].setText("");

                headerCell[0].setVisibility(View.GONE);
                headerCell[1].setVisibility(View.GONE);
                bottomCell[0].setVisibility(View.GONE);
                for (int a = 0; a < FIELDS_COUNT_PASSWORD; a++) {
                    ((View) inputFields[a].getParent()).setVisibility(View.GONE);
                }
                for (int a = 0; a < dividers.size(); a++) {
                    dividers.get(a).setVisibility(View.GONE);
                }
            } else {
                bottomCell[2].setVisibility(View.GONE);
                settingsCell[0].setVisibility(View.GONE);
                settingsCell[1].setVisibility(View.GONE);
                bottomCell[1].setText(LocaleController.getString(R.string.PaymentPasswordEmailInfo));
                codeFieldCell.setVisibility(View.GONE);

                headerCell[0].setVisibility(View.VISIBLE);
                headerCell[1].setVisibility(View.VISIBLE);
                bottomCell[0].setVisibility(View.VISIBLE);
                for (int a = 0; a < FIELDS_COUNT_PASSWORD; a++) {
                    ((View) inputFields[a].getParent()).setVisibility(View.VISIBLE);
                }
                for (int a = 0; a < dividers.size(); a++) {
                    dividers.get(a).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void loadPasswordInfo() {
        if (loadingPasswordInfo) {
            return;
        }
        loadingPasswordInfo = true;
        TL_account.getPassword req = new TL_account.getPassword();
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            loadingPasswordInfo = false;
            if (error == null) {
                currentPassword = (TL_account.Password) response;
                if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword, false)) {
                    AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                    return;
                }
                if (paymentForm != null && currentPassword.has_password) {
                    paymentForm.password_missing = false;
                    paymentForm.can_save_credentials = true;
                    updateSavePaymentField();
                }
                TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
                if (passwordFragment != null) {
                    passwordFragment.setCurrentPassword(currentPassword);
                }
                if (!currentPassword.has_password && shortPollRunnable == null) {
                    shortPollRunnable = () -> {
                        if (shortPollRunnable == null) {
                            return;
                        }
                        loadPasswordInfo();
                        shortPollRunnable = null;
                    };
                    AndroidUtilities.runOnUIThread(shortPollRunnable, 5000);
                }
            }
        }), ConnectionsManager.RequestFlagFailOnServerErrors | ConnectionsManager.RequestFlagWithoutLogin);
    }

    private void showAlertWithText(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void showPayAlert(final String totalPrice) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.PaymentTransactionReview));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("PaymentTransactionMessage2", R.string.PaymentTransactionMessage2, totalPrice, currentBotName, currentItemName)));
        builder.setPositiveButton(LocaleController.getString(R.string.Continue), (dialogInterface, i) -> {
            setDonePressed(true);
            sendData();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    private JSONObject getBaseCardPaymentMethod() throws JSONException {
        List<String> SUPPORTED_NETWORKS = Arrays.asList(
                "AMEX",
                "DISCOVER",
                "JCB",
                "MASTERCARD",
                "VISA");

        List<String> SUPPORTED_METHODS = Arrays.asList(
                "PAN_ONLY",
                "CRYPTOGRAM_3DS");

        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", new JSONArray(SUPPORTED_METHODS));
        parameters.put("allowedCardNetworks", new JSONArray(SUPPORTED_NETWORKS));

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }

    public Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    private void initGooglePay(Context context) {
        if (Build.VERSION.SDK_INT < 19 || getParentActivity() == null) {
            return;
        }
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(paymentForm.invoice.test ? WalletConstants.ENVIRONMENT_TEST : WalletConstants.ENVIRONMENT_PRODUCTION)
                .setTheme(WalletConstants.THEME_LIGHT)
                .build();
        paymentsClient = Wallet.getPaymentsClient(context, walletOptions);

        final Optional<JSONObject> isReadyToPayJson = getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(getParentActivity(),
                task1 -> {
                    if (task1.isSuccessful()) {
                        if (googlePayContainer != null) {
                            googlePayContainer.setVisibility(View.VISIBLE);
                        }
                    } else {
                        FileLog.e("isReadyToPay failed", task1.getException());
                    }
                });
    }

    private String getTotalPriceString(ArrayList<TLRPC.TL_labeledPrice> prices) {
        long amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount += prices.get(a).amount;
        }
        if (tipAmount != null) {
            amount += tipAmount;
        }
        return LocaleController.getInstance().formatCurrencyString(amount, paymentForm.invoice.currency);
    }

    private String getTotalPriceDecimalString(ArrayList<TLRPC.TL_labeledPrice> prices) {
        long amount = 0;
        for (int a = 0; a < prices.size(); a++) {
            amount += prices.get(a).amount;
        }
        return LocaleController.getInstance().formatCurrencyDecimalString(amount, paymentForm.invoice.currency, false);
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (currentStep != STEP_CHECKOUT || isCheckoutPreview) {
            NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.paymentFinished);
        }
        return super.onFragmentCreate();
    }

    public int getOtherSameFragmentDiff() {
        if (parentLayout == null || parentLayout.getFragmentStack() == null) {
            return 0;
        }
        int cur = parentLayout.getFragmentStack().indexOf(this);
        if (cur == -1) {
            cur = parentLayout.getFragmentStack().size();
        }
        int i = cur;
        for (int a = 0; a < parentLayout.getFragmentStack().size(); a++) {
            BaseFragment fragment = parentLayout.getFragmentStack().get(a);
            if (fragment instanceof PaymentFormActivity) {
                i = a;
                break;
            }
        }
        return i - cur;
    }

    @Override
    public void onFragmentDestroy() {
        if (delegate != null) {
            delegate.onFragmentDestroyed();
        }
        AndroidUtilities.checkAndroidTheme(getContext(), false);
        if (!paymentStatusSent) {
            invoiceStatus = InvoiceStatus.CANCELLED;
            if (paymentFormCallback != null && getOtherSameFragmentDiff() == 0) {
                paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
            }
        }
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        if (currentStep != STEP_CHECKOUT || isCheckoutPreview) {
            NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
        }
        if (webView != null) {
            try {
                ViewParent parent = webView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(webView);
                }
                webView.stopLoading();
                webView.loadUrl("about:blank");
                webViewUrl = null;
                webView.destroy();
                webView = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            if ((currentStep == STEP_PAYMENT_INFO || currentStep == STEP_SET_PASSWORD_EMAIL) && Build.VERSION.SDK_INT >= 23 && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                getParentActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                AndroidUtilities.logFlagSecure();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        super.onFragmentDestroy();
        canceled = true;
    }

    @Override
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();

        if (currentStep == STEP_CHECKOUT) {
            if (needPayAfterTransition) {
                needPayAfterTransition = false;
                bottomLayout.callOnClick();
            }
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            if (webView != null) {
                if (currentStep != STEP_CHECKOUT) {
                    if (paymentFormMethod != null) {
                        webView.loadUrl(webViewUrl = paymentFormMethod.url);
                    } else {
                        webView.loadUrl(webViewUrl = paymentForm.url);
                    }
                }
            } else if (currentStep == STEP_PAYMENT_INFO) {
                AndroidUtilities.runOnUIThread(() -> {
                    inputFields[FIELD_CARD].requestFocus();
                    AndroidUtilities.showKeyboard(inputFields[FIELD_CARD]);
                }, 100);
            } else if (currentStep == STEP_CONFIRM_PASSWORD) {
                inputFields[FIELD_SAVEDPASSWORD].requestFocus();
                AndroidUtilities.showKeyboard(inputFields[FIELD_SAVEDPASSWORD]);
            } else if (currentStep == STEP_CHECKOUT) {
                if (inputFields != null) {
                    inputFields[0].requestFocus();
                }
            } else if (currentStep == STEP_SET_PASSWORD_EMAIL) {
                if (!waitingForEmail) {
                    inputFields[FIELD_ENTERPASSWORD].requestFocus();
                    AndroidUtilities.showKeyboard(inputFields[FIELD_ENTERPASSWORD]);
                }
            }
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.twoStepPasswordChanged) {
            paymentForm.password_missing = false;
            paymentForm.can_save_credentials = true;
            updateSavePaymentField();
        } else if (id == NotificationCenter.didRemoveTwoStepPassword) {
            paymentForm.password_missing = true;
            paymentForm.can_save_credentials = false;
            updateSavePaymentField();
        } else if (id == NotificationCenter.paymentFinished) {
            paymentStatusSent = true;
            removeSelfFromStack();
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            AndroidUtilities.runOnUIThread(() -> {
                if (resultCode == Activity.RESULT_OK) {
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    if (paymentData == null) {
                        return;
                    }
                    final String paymentInfo = paymentData.toJson();
                    if (paymentInfo == null) {
                        return;
                    }
                    try {
                        JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
                        final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
                        final String tokenizationType = tokenizationData.getString("type");
                        final String token = tokenizationData.getString("token");

                        if (googlePayPublicKey != null || googlePayParameters != null) {
                            googlePayCredentials = new TLRPC.TL_inputPaymentCredentialsGooglePay();
                            googlePayCredentials.payment_token = new TLRPC.TL_dataJSON();
                            googlePayCredentials.payment_token.data = tokenizationData.toString();
                            String descriptions = paymentMethodData.optString("description");
                            if (!TextUtils.isEmpty(descriptions)) {
                                cardName = descriptions;
                            } else {
                                cardName = "Android Pay";
                            }
                        } else {
                            Token t = TokenParser.parseToken(token);
                            paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", t.getType(), t.getId());
                            Card card = t.getCard();
                            cardName = card.getBrand() + " *" + card.getLast4();
                        }
                        goToNextStep();
                    } catch (JSONException e) {
                        FileLog.e(e);
                    }
                } else {
                    if (resultCode == AutoResolveHelper.RESULT_ERROR) {
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        FileLog.e("android pay error " + (status != null ? status.getStatusMessage() : ""));
                    }
                }
                showEditDoneProgress(true, false);
                setDonePressed(false);
                if (googlePayButton != null) {
                    googlePayButton.setClickable(true);
                }
            });
        }
    }

    private void goToNextStep() {
        switch (currentStep) {
            case STEP_SHIPPING_INFORMATION:
                if (delegate != null) {
                    delegate.didSelectNewAddress(validateRequest);
                    finishFragment();
                } else {
                    int nextStep;
                    if (paymentForm.invoice.flexible) {
                        nextStep = STEP_SHIPPING_METHODS;
                    } else if (savedCredentialsCard != null || paymentJson != null) {
                        if (UserConfig.getInstance(currentAccount).tmpPassword != null) {
                            if (UserConfig.getInstance(currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(currentAccount).getCurrentTime() + 60) {
                                UserConfig.getInstance(currentAccount).tmpPassword = null;
                                UserConfig.getInstance(currentAccount).saveConfig(false);
                            }
                        }
                        if (UserConfig.getInstance(currentAccount).tmpPassword != null) {
                            nextStep = STEP_CHECKOUT;
                        } else {
                            nextStep = STEP_CONFIRM_PASSWORD;
                        }
                    } else {
                        nextStep = STEP_PAYMENT_INFO;
                    }
                    if (nextStep == STEP_PAYMENT_INFO && savedCredentialsCard == null && paymentJson == null && !paymentForm.additional_methods.isEmpty()) {
                        showChoosePaymentMethod(this::goToNextStep);
                    } else {
                        presentFragment(
                            new PaymentFormActivity(invoiceInput, paymentForm, messageObject, invoiceSlug, nextStep, requestedInfo, null, null, paymentJson, cardName, validateRequest, saveCardInfo, googlePayCredentials, parentFragment, allowUnregistered)
                                .setCustomResultReceiver(customResultReceiver)
                                .setCustomAnyResultReceiver(customAnyResultReceiver),
                            isWebView
                        );
                    }
                }
                break;
            case STEP_SHIPPING_METHODS: {
                int nextStep;
                if (paymentJson != null || cardName != null) {
                    nextStep = STEP_CHECKOUT;
                } else if (savedCredentialsCard != null) {
                    if (UserConfig.getInstance(currentAccount).tmpPassword != null) {
                        if (UserConfig.getInstance(currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(currentAccount).getCurrentTime() + 60) {
                            UserConfig.getInstance(currentAccount).tmpPassword = null;
                            UserConfig.getInstance(currentAccount).saveConfig(false);
                        }
                    }
                    if (UserConfig.getInstance(currentAccount).tmpPassword != null) {
                        nextStep = STEP_CHECKOUT;
                    } else {
                        nextStep = STEP_CONFIRM_PASSWORD;
                    }
                } else {
                    nextStep = STEP_PAYMENT_INFO;
                }
                if (nextStep == STEP_PAYMENT_INFO && cardName == null && savedCredentialsCard == null && paymentJson == null && !paymentForm.additional_methods.isEmpty()) {
                    showChoosePaymentMethod(this::goToNextStep);
                } else {
                    presentFragment(
                        new PaymentFormActivity(invoiceInput, paymentForm, messageObject, invoiceSlug, nextStep, requestedInfo, shippingOption, tipAmount, paymentJson, cardName, validateRequest, saveCardInfo, googlePayCredentials, parentFragment, allowUnregistered)
                            .setCustomResultReceiver(customResultReceiver)
                            .setCustomAnyResultReceiver(customAnyResultReceiver),
                        isWebView
                    );
                }
                break;
            }
            case STEP_PAYMENT_INFO:
                if (paymentForm.password_missing && saveCardInfo) {
                    passwordFragment = new PaymentFormActivity(invoiceInput, paymentForm, messageObject, invoiceSlug, STEP_SET_PASSWORD_EMAIL, requestedInfo, shippingOption, tipAmount, paymentJson, cardName, validateRequest, saveCardInfo, googlePayCredentials, parentFragment, allowUnregistered);
                    passwordFragment.setCustomResultReceiver(customResultReceiver);
                    passwordFragment.setCustomAnyResultReceiver(customAnyResultReceiver);
                    passwordFragment.setCurrentPassword(currentPassword);
                    passwordFragment.setDelegate(new PaymentFormActivityDelegate() {
                        @Override
                        public boolean didSelectNewCard(String tokenJson, String card, boolean saveCard, TLRPC.TL_inputPaymentCredentialsGooglePay googlePay, TLRPC.TL_paymentSavedCredentialsCard credentialsCard) {
                            if (delegate != null) {
                                delegate.didSelectNewCard(tokenJson, card, saveCard, googlePay, credentialsCard);
                            }
                            if (isWebView) {
                                removeSelfFromStack();
                            }
                            return delegate != null;
                        }

                        @Override
                        public void onFragmentDestroyed() {
                            passwordFragment = null;
                        }

                        @Override
                        public void currentPasswordUpdated(TL_account.Password password) {
                            currentPassword = password;
                        }
                    });
                    presentFragment(passwordFragment, isWebView);
                } else {
                    if (delegate != null) {
                        delegate.didSelectNewCard(paymentJson, cardName, saveCardInfo, googlePayCredentials, null);
                        finishFragment();
                    } else {
                        presentFragment(
                            new PaymentFormActivity(invoiceInput, paymentForm, messageObject, invoiceSlug, STEP_CHECKOUT, requestedInfo, shippingOption, tipAmount, paymentJson, cardName, validateRequest, saveCardInfo, googlePayCredentials, parentFragment, allowUnregistered)
                                .setCustomResultReceiver(customResultReceiver)
                                .setCustomAnyResultReceiver(customAnyResultReceiver),
                            isWebView
                        );
                    }
                }
                break;
            case STEP_CONFIRM_PASSWORD: {
                int nextStep;
                if (passwordOk) {
                    nextStep = STEP_CHECKOUT;
                } else {
                    nextStep = STEP_PAYMENT_INFO;
                }
                presentFragment(
                    new PaymentFormActivity(invoiceInput, paymentForm, messageObject, invoiceSlug, nextStep, requestedInfo, shippingOption, tipAmount, paymentJson, cardName, validateRequest, saveCardInfo, googlePayCredentials, parentFragment, allowUnregistered)
                        .setCustomResultReceiver(customResultReceiver)
                        .setCustomAnyResultReceiver(customAnyResultReceiver),
                    true
                );
                break;
            }
            case STEP_CHECKOUT:
                if (isCheckoutPreview) {
                    NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.paymentFinished);
                }
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.paymentFinished);

                if (getMessagesController().newMessageCallback == null) {
                    if (!onCheckoutSuccess(getParentLayout(), getParentActivity()) && !isFinishing()) {
                        finishFragment();
                    }
                } else {
                    AndroidUtilities.runOnUIThread(()-> {
                        getMessagesController().newMessageCallback = null;
                        if (invoiceStatus == InvoiceStatus.PENDING && !isFinishing()) {
                            invoiceStatus = InvoiceStatus.FAILED;
                            if (paymentFormCallback != null) {
                                paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                            }
                            finishFragment();
                        } else if (invoiceStatus == InvoiceStatus.PAID && !isFinishing()) {
                            finishFragment();
                        }
                    }, 500);
                }
                break;
            case STEP_SET_PASSWORD_EMAIL:
                if (!delegate.didSelectNewCard(paymentJson, cardName, saveCardInfo, googlePayCredentials, savedCredentialsCard)) {
                    presentFragment(
                        new PaymentFormActivity(invoiceInput, paymentForm, messageObject, invoiceSlug, STEP_CHECKOUT, requestedInfo, shippingOption, tipAmount, paymentJson, cardName, validateRequest, saveCardInfo, googlePayCredentials, parentFragment, false)
                            .setCustomResultReceiver(customResultReceiver)
                            .setCustomAnyResultReceiver(customAnyResultReceiver),
                        true
                    );
                } else {
                    finishFragment();
                }
                break;
        }
    }

    private boolean onCheckoutSuccess(INavigationLayout parentLayout, Activity parentActivity) {
        if (invoiceInput != null) {
            if (parentLayout != null) {
                for (BaseFragment fragment : new ArrayList<>(parentLayout.getFragmentStack())) {
                    if (fragment instanceof PaymentFormActivity) {
                        fragment.removeSelfFromStack();
                    }
                }
                return true;
            }
            return false;
        }
        if (botUser.username != null && botUser.username.equalsIgnoreCase(getMessagesController().premiumBotUsername) && invoiceSlug == null || invoiceSlug != null && getMessagesController().premiumInvoiceSlug != null && Objects.equals(invoiceSlug, getMessagesController().premiumInvoiceSlug)) {
            if (parentLayout != null) {
                for (BaseFragment fragment : new ArrayList<>(parentLayout.getFragmentStack())) {
                    if (fragment instanceof ChatActivity || fragment instanceof PremiumPreviewFragment) {
                        fragment.removeSelfFromStack();
                    }
                }

                parentLayout.presentFragment(new PremiumPreviewFragment(null).setForcePremium(), !isFinishing());
                if (parentActivity instanceof LaunchActivity) {
                    try {
                        fragmentView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    } catch (Exception ignored) {
                    }
                    ((LaunchActivity) parentActivity).getFireworksOverlay().start();
                }
                return true;
            }
        }
        return false;
    }

    private void updateSavePaymentField() {
        if (bottomCell[0] == null || sectionCell[2] == null) {
            return;
        }
        if ((paymentForm.password_missing || paymentForm.can_save_credentials) && (webView == null || !webviewLoading)) {
            SpannableStringBuilder text = new SpannableStringBuilder(LocaleController.getString(R.string.PaymentCardSavePaymentInformationInfoLine1));
            if (paymentForm.password_missing) {
                loadPasswordInfo();
                text.append("\n");
                int len = text.length();
                String str2 = LocaleController.getString(R.string.PaymentCardSavePaymentInformationInfoLine2);
                int index1 = str2.indexOf('*');
                int index2 = str2.lastIndexOf('*');
                text.append(str2);
                if (index1 != -1 && index2 != -1) {
                    index1 += len;
                    index2 += len;
                    bottomCell[0].getTextView().setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                    text.replace(index2, index2 + 1, "");
                    text.replace(index1, index1 + 1, "");
                    text.setSpan(new LinkSpan(), index1, index2 - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            checkCell1.setEnabled(true);
            bottomCell[0].setText(text);
            checkCell1.setVisibility(View.VISIBLE);
            bottomCell[0].setVisibility(View.VISIBLE);
            sectionCell[2].setBackground(Theme.getThemedDrawableByKey(sectionCell[2].getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        } else {
            checkCell1.setVisibility(View.GONE);
            bottomCell[0].setVisibility(View.GONE);
            sectionCell[2].setBackground(Theme.getThemedDrawableByKey(sectionCell[2].getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
    }

    @SuppressLint("HardwareIds")
    public void fillNumber(String number) {
        try {
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
            boolean allowCall = true;
            boolean allowSms = true;
            if (number != null || tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT && tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
                if (Build.VERSION.SDK_INT >= 23) {
                    allowCall = getParentActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
                }
                if (number != null || allowCall) {
                    if (number == null) {
                        number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                    }
                    String textToSet = null;
                    boolean ok = false;
                    if (!TextUtils.isEmpty(number)) {
                        if (number.length() > 4) {
                            for (int a = 4; a >= 1; a--) {
                                String sub = number.substring(0, a);
                                String country = codesMap.get(sub);
                                if (country != null) {
                                    ok = true;
                                    textToSet = number.substring(a);
                                    inputFields[FIELD_PHONECODE].setText(sub);
                                    break;
                                }
                            }
                            if (!ok) {
                                textToSet = number.substring(1);
                                inputFields[FIELD_PHONECODE].setText(number.substring(0, 1));
                            }
                        }
                        if (textToSet != null) {
                            inputFields[FIELD_PHONE].setText(textToSet);
                            inputFields[FIELD_PHONE].setSelection(inputFields[FIELD_PHONE].length());
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void sendSavePassword(final boolean clear) {
        if (!clear && codeFieldCell.getVisibility() == View.VISIBLE) {
            String code = codeFieldCell.getText();
            if (code.length() == 0) {
                shakeView(codeFieldCell);
                return;
            }
            showEditDoneProgress(true, true);
            TL_account.confirmPasswordEmail req = new TL_account.confirmPasswordEmail();
            req.code = code;
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                showEditDoneProgress(true, false);
                if (error == null) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    if (shortPollRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(shortPollRunnable);
                        shortPollRunnable = null;
                    }
                    goToNextStep();
                } else {
                    if (error.text.startsWith("CODE_INVALID")) {
                        shakeView(codeFieldCell);
                        codeFieldCell.setText("", false);
                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                        int time = Utilities.parseInt(error.text);
                        String timeString;
                        if (time < 60) {
                            timeString = LocaleController.formatPluralString("Seconds", time);
                        } else {
                            timeString = LocaleController.formatPluralString("Minutes", time / 60);
                        }
                        showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                    } else {
                        showAlertWithText(LocaleController.getString(R.string.AppName), error.text);
                    }
                }
            }), ConnectionsManager.RequestFlagFailOnServerErrors | ConnectionsManager.RequestFlagWithoutLogin);
        } else {
            final TL_account.updatePasswordSettings req = new TL_account.updatePasswordSettings();
            final String email;
            final String firstPassword;
            if (clear) {
                doneItem.setVisibility(View.VISIBLE);
                email = null;
                firstPassword = null;
                req.new_settings = new TL_account.passwordInputSettings();
                req.new_settings.flags = 2;
                req.new_settings.email = "";
                req.password = new TLRPC.TL_inputCheckPasswordEmpty();
            } else {
                firstPassword = inputFields[FIELD_ENTERPASSWORD].getText().toString();
                if (TextUtils.isEmpty(firstPassword)) {
                    shakeField(FIELD_ENTERPASSWORD);
                    return;
                }
                String secondPassword = inputFields[FIELD_REENTERPASSWORD].getText().toString();
                if (!firstPassword.equals(secondPassword)) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString(R.string.PasswordDoNotMatch), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    shakeField(FIELD_REENTERPASSWORD);
                    return;
                }
                email = inputFields[FIELD_ENTERPASSWORDEMAIL].getText().toString();
                if (email.length() < 3) {
                    shakeField(FIELD_ENTERPASSWORDEMAIL);
                    return;
                }
                int dot = email.lastIndexOf('.');
                int dog = email.lastIndexOf('@');
                if (dog < 0 || dot < dog) {
                    shakeField(FIELD_ENTERPASSWORDEMAIL);
                    return;
                }

                req.password = new TLRPC.TL_inputCheckPasswordEmpty();
                req.new_settings = new TL_account.passwordInputSettings();
                req.new_settings.flags |= 1;
                req.new_settings.hint = "";
                req.new_settings.new_algo = currentPassword.new_algo;

                req.new_settings.flags |= 2;
                req.new_settings.email = email.trim();
            }
            showEditDoneProgress(true, true);
            Utilities.globalQueue.postRunnable(() -> {
                RequestDelegate requestDelegate = (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                        TL_account.getPassword getPasswordReq = new TL_account.getPassword();
                        ConnectionsManager.getInstance(currentAccount).sendRequest(getPasswordReq, (response2, error2) -> AndroidUtilities.runOnUIThread(() -> {
                            if (error2 == null) {
                                currentPassword = (TL_account.Password) response2;
                                TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
                                sendSavePassword(clear);
                            }
                        }), ConnectionsManager.RequestFlagWithoutLogin);
                        return;
                    }
                    showEditDoneProgress(true, false);
                    if (clear) {
                        currentPassword.has_password = false;
                        currentPassword.current_algo = null;
                        delegate.currentPasswordUpdated(currentPassword);
                        finishFragment();
                    } else {
                        if (error == null && response instanceof TLRPC.TL_boolTrue) {
                            if (getParentActivity() == null) {
                                return;
                            }
                            goToNextStep();
                        } else if (error != null) {
                            if (error.text.equals("EMAIL_UNCONFIRMED") || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                                emailCodeLength = Utilities.parseInt(error.text);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                builder.setPositiveButton(LocaleController.getString(R.string.OK), (dialogInterface, i) -> {
                                    waitingForEmail = true;
                                    currentPassword.email_unconfirmed_pattern = email;
                                    updatePasswordFields();
                                });
                                builder.setMessage(LocaleController.getString(R.string.YourEmailAlmostThereText));
                                builder.setTitle(LocaleController.getString(R.string.YourEmailAlmostThere));
                                Dialog dialog = showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            } else {
                                if (error.text.equals("EMAIL_INVALID")) {
                                    showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.getString(R.string.PasswordEmailInvalid));
                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                    int time = Utilities.parseInt(error.text);
                                    String timeString;
                                    if (time < 60) {
                                        timeString = LocaleController.formatPluralString("Seconds", time);
                                    } else {
                                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                    }
                                    showAlertWithText(LocaleController.getString(R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                } else {
                                    showAlertWithText(LocaleController.getString(R.string.AppName), error.text);
                                }
                            }
                        }
                    }
                });

                if (!clear) {
                    byte[] newPasswordBytes = AndroidUtilities.getStringBytes(firstPassword);
                    if (currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                        TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword.new_algo;
                        req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo);
                        if (req.new_settings.new_password_hash == null) {
                            TLRPC.TL_error error = new TLRPC.TL_error();
                            error.text = "ALGO_INVALID";
                            requestDelegate.run(null, error);
                        }
                        ConnectionsManager.getInstance(currentAccount).sendRequest(req, requestDelegate, ConnectionsManager.RequestFlagFailOnServerErrors | ConnectionsManager.RequestFlagWithoutLogin);
                    } else {
                        TLRPC.TL_error error = new TLRPC.TL_error();
                        error.text = "PASSWORD_HASH_INVALID";
                        requestDelegate.run(null, error);
                    }
                } else {
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req, requestDelegate, ConnectionsManager.RequestFlagFailOnServerErrors | ConnectionsManager.RequestFlagWithoutLogin);
                }
            });
        }
    }

    private boolean sendCardData() {
        Integer month;
        Integer year;
        String date = inputFields[FIELD_EXPIRE_DATE].getText().toString();
        String[] args = date.split("/");
        if (args.length == 2) {
            month = Utilities.parseInt(args[0]);
            year = Utilities.parseInt(args[1]);
        } else {
            month = null;
            year = null;
        }
        Card card = new Card(
                inputFields[FIELD_CARD].getText().toString(),
                month,
                year,
                inputFields[FIELD_CVV].getText().toString(),
                inputFields[FIELD_CARDNAME].getText().toString(),
                null, null, null, null,
                inputFields[FIELD_CARD_POSTCODE].getText().toString(),
                inputFields[FIELD_CARD_COUNTRY].getText().toString(),
                null);
        cardName = card.getBrand() + " *" + card.getLast4();

        boolean skipDateCheck = false;
        if (month != null && year != null) {
            if (UserConfig.getInstance(currentAccount).getClientPhone().startsWith("7")) {
                if ("smartglocal".equals(paymentForm.native_provider)) {
                    if (year > 22 || year == 22 && month > 1) {
                        skipDateCheck = true;
                    }
                }
            }
        }

        if (!card.validateNumber()) {
            shakeField(FIELD_CARD);
            return false;
        } else if (!skipDateCheck && (!card.validateExpMonth() || !card.validateExpYear() || !card.validateExpiryDate())) {
            shakeField(FIELD_EXPIRE_DATE);
            return false;
        } else if (need_card_name && inputFields[FIELD_CARDNAME].length() == 0) {
            shakeField(FIELD_CARDNAME);
            return false;
        } else if (!card.validateCVC()) {
            shakeField(FIELD_CVV);
            return false;
        } else if (need_card_country && inputFields[FIELD_CARD_COUNTRY].length() == 0) {
            shakeField(FIELD_CARD_COUNTRY);
            return false;
        } else if (need_card_postcode && inputFields[FIELD_CARD_POSTCODE].length() == 0) {
            shakeField(FIELD_CARD_POSTCODE);
            return false;
        }
        showEditDoneProgress(true, true);
        try {
            if ("stripe".equals(paymentForm.native_provider)) {
                Stripe stripe = new Stripe(providerApiKey);
                stripe.createToken(card, new TokenCallback() {
                            public void onSuccess(Token token) {
                                if (canceled) {
                                    return;
                                }
                                paymentJson = String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", token.getType(), token.getId());
                                AndroidUtilities.runOnUIThread(() -> {
                                    goToNextStep();
                                    showEditDoneProgress(true, false);
                                    setDonePressed(false);
                                });
                            }

                            public void onError(Exception error) {
                                if (canceled) {
                                    return;
                                }
                                showEditDoneProgress(true, false);
                                setDonePressed(false);
                                if (error instanceof APIConnectionException || error instanceof APIException) {
                                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString(R.string.PaymentConnectionFailed));
                                } else {
                                    AlertsCreator.showSimpleToast(PaymentFormActivity.this, error.getMessage());
                                }
                            }
                        }
                );
            } else if ("smartglocal".equals(paymentForm.native_provider)) {
                AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
                    @Override
                    protected String doInBackground(Object... objects) {
                        HttpURLConnection conn = null;
                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject cardObject = new JSONObject();
                            cardObject.put("number", card.getNumber());
                            cardObject.put("expiration_month", String.format(Locale.US, "%02d", card.getExpMonth()));
                            cardObject.put("expiration_year", "" + card.getExpYear());
                            cardObject.put("security_code", "" + card.getCVC());
                            jsonObject.put("card", cardObject);

                            String overrideSmartGlocalConnectionUrl = null;
                            if (paymentForm.native_params != null) {
                                try {
                                    JSONObject jsonObject2 = new JSONObject(paymentForm.native_params.data);
                                    overrideSmartGlocalConnectionUrl = jsonObject2.getString("tokenize_url");
                                    if (overrideSmartGlocalConnectionUrl != null && !(
                                        overrideSmartGlocalConnectionUrl.startsWith("https://") &&
                                        overrideSmartGlocalConnectionUrl.endsWith(".smart-glocal.com/cds/v1/tokenize/card")
                                    )) {
                                        overrideSmartGlocalConnectionUrl = null;
                                    }
                                } catch (Exception e) {}
                            }
                            URL connectionUrl;
                            if (overrideSmartGlocalConnectionUrl != null) {
                                connectionUrl = new URL(overrideSmartGlocalConnectionUrl);
                            } else if (paymentForm.invoice.test) {
                                connectionUrl = new URL("https://tgb-playground.smart-glocal.com/cds/v1/tokenize/card");
                            } else {
                                connectionUrl = new URL("https://tgb.smart-glocal.com/cds/v1/tokenize/card");
                            }
                            conn = (HttpURLConnection) connectionUrl.openConnection();
                            conn.setConnectTimeout(30 * 1000);
                            conn.setReadTimeout(80 * 1000);
                            conn.setUseCaches(false);
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("X-PUBLIC-TOKEN", providerApiKey);

                            try (OutputStream output = conn.getOutputStream()) {
                                output.write(jsonObject.toString().getBytes("UTF-8"));
                            }

                            int code = conn.getResponseCode();
                            if (code >= 200 && code < 300) {
                                JSONObject result = new JSONObject();
                                JSONObject jsonObject1 = new JSONObject(getResponseBody(conn.getInputStream()));
                                String token = jsonObject1.getJSONObject("data").getString("token");
                                result.put("token", token);
                                result.put("type", "card");
                                return result.toString();
                            } else {
                                if (BuildVars.DEBUG_VERSION) {
                                    FileLog.e("" + getResponseBody(conn.getErrorStream()));
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        } finally {
                            if (conn != null) {
                                conn.disconnect();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (canceled) {
                            return;
                        }
                        if (result == null) {
                            AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString(R.string.PaymentConnectionFailed));
                        } else {
                            paymentJson = result;
                            goToNextStep();
                        }
                        showEditDoneProgress(true, false);
                        setDonePressed(false);
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return true;
    }

    private static String getResponseBody(InputStream responseStream) throws IOException {
        String rBody = new Scanner(responseStream, "UTF-8")
                .useDelimiter("\\A")
                .next();
        responseStream.close();
        return rBody;
    }

    private void sendSavedForm(Runnable callback) {
        if (canceled) {
            return;
        }
        showEditDoneProgress(true, true);
        validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
        if (invoiceInput != null) {
            validateRequest.invoice = invoiceInput;
        } else if (messageObject != null) {
            TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
            inputInvoice.peer = getMessagesController().getInputPeer(messageObject.messageOwner.peer_id);
            inputInvoice.msg_id = messageObject.getId();
            validateRequest.invoice = inputInvoice;
        } else {
            TLRPC.TL_inputInvoiceSlug inputInvoice = new TLRPC.TL_inputInvoiceSlug();
            inputInvoice.slug = invoiceSlug;
            validateRequest.invoice = inputInvoice;
        }
        validateRequest.save = true;
        validateRequest.info = paymentForm.saved_info;

        TLObject req = validateRequest;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {
            if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
                AndroidUtilities.runOnUIThread(() -> {
                    requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
                    callback.run();
                    setDonePressed(false);
                    showEditDoneProgress(true, false);
                });
            } else {
                AndroidUtilities.runOnUIThread(() -> {
                    setDonePressed(false);
                    showEditDoneProgress(true, false);
                    if (error != null) {
                        AlertsCreator.processError(currentAccount, error, PaymentFormActivity.this, req);
                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors | (allowUnregistered ? ConnectionsManager.RequestFlagWithoutLogin : 0));
    }

    private void sendForm() {
        if (canceled) {
            return;
        }
        showEditDoneProgress(true, true);
        validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
        if (invoiceInput != null) {
            validateRequest.invoice = invoiceInput;
        } else if (messageObject != null) {
            TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
            inputInvoice.peer = getMessagesController().getInputPeer(messageObject.messageOwner.peer_id);
            inputInvoice.msg_id = messageObject.getId();
            validateRequest.invoice = inputInvoice;
        } else {
            TLRPC.TL_inputInvoiceSlug inputInvoice = new TLRPC.TL_inputInvoiceSlug();
            inputInvoice.slug = invoiceSlug;
            validateRequest.invoice = inputInvoice;
        }
        validateRequest.save = saveShippingInfo;
        validateRequest.info = new TLRPC.TL_paymentRequestedInfo();
        if (paymentForm.invoice.name_requested) {
            validateRequest.info.name = inputFields[FIELD_NAME].getText().toString();
            validateRequest.info.flags |= 1;
        }
        if (paymentForm.invoice.phone_requested) {
            validateRequest.info.phone = "+" + inputFields[FIELD_PHONECODE].getText().toString() + inputFields[FIELD_PHONE].getText().toString();
            validateRequest.info.flags |= 2;
        }
        if (paymentForm.invoice.email_requested) {
            validateRequest.info.email = inputFields[FIELD_EMAIL].getText().toString().trim();
            validateRequest.info.flags |= 4;
        }
        if (paymentForm.invoice.shipping_address_requested) {
            validateRequest.info.shipping_address = new TLRPC.TL_postAddress();
            validateRequest.info.shipping_address.street_line1 = inputFields[FIELD_STREET1].getText().toString();
            validateRequest.info.shipping_address.street_line2 = inputFields[FIELD_STREET2].getText().toString();
            validateRequest.info.shipping_address.city = inputFields[FIELD_CITY].getText().toString();
            validateRequest.info.shipping_address.state = inputFields[FIELD_STATE].getText().toString();
            validateRequest.info.shipping_address.country_iso2 = countryName != null ? countryName : "";
            validateRequest.info.shipping_address.post_code = inputFields[FIELD_POSTCODE].getText().toString();
            validateRequest.info.flags |= 8;
        }
        TLObject req = validateRequest;
        ConnectionsManager.getInstance(currentAccount).sendRequest(validateRequest, (response, error) -> {
            if (response instanceof TLRPC.TL_payments_validatedRequestedInfo) {
                AndroidUtilities.runOnUIThread(() -> {
                    requestedInfo = (TLRPC.TL_payments_validatedRequestedInfo) response;
                    if (paymentForm.saved_info != null && !saveShippingInfo) {
                        TLRPC.TL_payments_clearSavedInfo req1 = new TLRPC.TL_payments_clearSavedInfo();
                        req1.info = true;
                        ConnectionsManager.getInstance(currentAccount).sendRequest(req1, (response1, error1) -> {

                        }, (allowUnregistered ? ConnectionsManager.RequestFlagWithoutLogin : 0));
                    }
                    goToNextStep();
                    setDonePressed(false);
                    showEditDoneProgress(true, false);
                });
            } else {
                AndroidUtilities.runOnUIThread(() -> {
                    setDonePressed(false);
                    showEditDoneProgress(true, false);
                    if (error != null) {
                        switch (error.text) {
                            case "REQ_INFO_NAME_INVALID":
                                shakeField(FIELD_NAME);
                                break;
                            case "REQ_INFO_PHONE_INVALID":
                                shakeField(FIELD_PHONE);
                                break;
                            case "REQ_INFO_EMAIL_INVALID":
                                shakeField(FIELD_EMAIL);
                                break;
                            case "ADDRESS_COUNTRY_INVALID":
                                shakeField(FIELD_COUNTRY);
                                break;
                            case "ADDRESS_CITY_INVALID":
                                shakeField(FIELD_CITY);
                                break;
                            case "ADDRESS_POSTCODE_INVALID":
                                shakeField(FIELD_POSTCODE);
                                break;
                            case "ADDRESS_STATE_INVALID":
                                shakeField(FIELD_STATE);
                                break;
                            case "ADDRESS_STREET_LINE1_INVALID":
                                shakeField(FIELD_STREET1);
                                break;
                            case "ADDRESS_STREET_LINE2_INVALID":
                                shakeField(FIELD_STREET2);
                                break;
                            default:
                                AlertsCreator.processError(currentAccount, error, PaymentFormActivity.this, req);
                                break;
                        }
                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors | (allowUnregistered ? ConnectionsManager.RequestFlagWithoutLogin : 0));
    }

    private Utilities.Callback<TLRPC.TL_payments_paymentResult> customResultReceiver;
    public PaymentFormActivity setCustomResultReceiver(Utilities.Callback<TLRPC.TL_payments_paymentResult> receiver) {
        this.customResultReceiver = receiver;
        return this;
    }
    private Utilities.Callback<TLRPC.payments_PaymentResult> customAnyResultReceiver;
    public PaymentFormActivity setCustomAnyResultReceiver(Utilities.Callback<TLRPC.payments_PaymentResult> receiver) {
        this.customAnyResultReceiver = receiver;
        return this;
    }
    private Utilities.CallbackReturn<TLRPC.TL_error, Boolean> customErrorReceiver;
    public void setCustomErrorReceiver(Utilities.CallbackReturn<TLRPC.TL_error, Boolean> receiver) {
        this.customErrorReceiver = receiver;
    }

    private void sendData() {
        if (canceled) {
            return;
        }
        showEditDoneProgress(false, true);
        final TLRPC.TL_payments_sendPaymentForm req = new TLRPC.TL_payments_sendPaymentForm();
        if (invoiceInput != null) {
            req.invoice = invoiceInput;
        } else if (messageObject != null) {
            final TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
            inputInvoice.peer = getMessagesController().getInputPeer(messageObject.messageOwner.peer_id);
            inputInvoice.msg_id = messageObject.getId();
            req.invoice = inputInvoice;
        } else {
            final TLRPC.TL_inputInvoiceSlug inputInvoice = new TLRPC.TL_inputInvoiceSlug();
            inputInvoice.slug = invoiceSlug;
            req.invoice = inputInvoice;
        }

        req.form_id = paymentForm.form_id;
        if (UserConfig.getInstance(currentAccount).tmpPassword != null && savedCredentialsCard != null) {
            req.credentials = new TLRPC.TL_inputPaymentCredentialsSaved();
            req.credentials.id = savedCredentialsCard.id;
            req.credentials.tmp_password = UserConfig.getInstance(currentAccount).tmpPassword.tmp_password;
        } else if (googlePayCredentials != null) {
            req.credentials = googlePayCredentials;
        } else {
            req.credentials = new TLRPC.TL_inputPaymentCredentials();
            req.credentials.save = saveCardInfo;
            req.credentials.data = new TLRPC.TL_dataJSON();
            req.credentials.data.data = paymentJson;
        }
        if (requestedInfo != null && requestedInfo.id != null) {
            req.requested_info_id = requestedInfo.id;
            req.flags |= 1;
        }
        if (shippingOption != null) {
            req.shipping_option_id = shippingOption.id;
            req.flags |= 2;
        }
        if ((paymentForm.invoice.flags & 256) != 0) {
            req.tip_amount = tipAmount != null ? tipAmount : 0;
            req.flags |= 4;
        }
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {
            if (response != null) {
                if (response instanceof TLRPC.TL_payments_paymentResult) {
                    if (this.customResultReceiver != null) {
                        this.customResultReceiver.run((TLRPC.TL_payments_paymentResult) response);
                        return;
                    }

                    final TLRPC.Updates updates = ((TLRPC.TL_payments_paymentResult) response).updates;
                    final TLRPC.Message[] message = new TLRPC.Message[1];
                    for (int a = 0, N = updates.updates.size(); a < N; a++) {
                        final TLRPC.Update update = updates.updates.get(a);
                        if (update instanceof TL_update.TL_updateNewMessage) {
                            message[0] = ((TL_update.TL_updateNewMessage) update).message;
                            break;
                        } else if (update instanceof TL_update.TL_updateNewChannelMessage) {
                            message[0] = ((TL_update.TL_updateNewChannelMessage) update).message;
                            break;
                        }
                    }
                    getMessagesController().processUpdates(updates, false);
                    AndroidUtilities.runOnUIThread(() -> {
                        Context context = getContext();
                        if (context == null) context = ApplicationLoader.applicationContext;
                        if (context == null) context = LaunchActivity.instance;
                        if (context == null) return;

                        paymentStatusSent = true;
                        invoiceStatus = InvoiceStatus.PAID;
                        final boolean isStars = invoiceInput instanceof TLRPC.TL_inputInvoiceStars;
                        final boolean isStarsGift = isStars && ((TLRPC.TL_inputInvoiceStars) invoiceInput).purpose instanceof TLRPC.TL_inputStorePaymentStarsGift;
                        final boolean isStarsGiveaway = isStars && ((TLRPC.TL_inputInvoiceStars) invoiceInput).purpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway;
                        if (!isStars && paymentFormCallback != null) {
                            paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                        }

                        goToNextStep();
                        if (isStars && paymentFormCallback != null) {
                            paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                        }
                        final long giftUserId = getStarsGiftUserId();
                        String giftUser = "";
                        if (giftUserId > 0) {
                            giftUser = UserObject.getForcedFirstName(getMessagesController().getUser(giftUserId));
                        } else if (giftUserId < 0) {
                            TLRPC.Chat chat = getMessagesController().getChat(-giftUserId);
                            giftUser = chat != null ? chat.title : "";
                        }
                        long stars = getStars();
                        int icon = isStars ? (isStarsGift || isStarsGiveaway ? R.raw.stars_send : R.raw.stars_topup) : R.raw.payment_success;
                        CharSequence bulletinTitle = !isStars ? null : (isStarsGiveaway ? getString(R.string.StarsGiveawaySentPopup) : isStarsGift ? getString(R.string.StarsGiftSentPopup) : getString(R.string.StarsAcquired));
                        CharSequence bulletinText = AndroidUtilities.replaceTags(
                            isStars ?
                                isStarsGiveaway ? LocaleController.formatPluralStringComma("StarsGiveawaySentPopupInfo", (int) stars) : LocaleController.formatPluralStringComma(isStarsGift ? "StarsGiftSentPopupInfo" : "StarsAcquiredInfo", (int) stars, giftUser) :
                                LocaleController.formatString(R.string.PaymentInfoHint, totalPrice[0], currentItemName)
                        );
                        BaseFragment lastFragment = LaunchActivity.getSafeLastFragment();
                        if (lastFragment == null) return;
                        BulletinFactory factory = BulletinFactory.of(lastFragment);
                        Bulletin bulletin;
                        if (giftUserId != 0 && bulletinTitle != null && !isStarsGiveaway) {
                            bulletin = factory.createSimpleBulletin(icon, bulletinTitle, bulletinText, getString(R.string.ViewInChat), () -> {
                                BaseFragment lastFragment2 = LaunchActivity.getSafeLastFragment();
                                if (lastFragment2 != null) {
                                    lastFragment2.presentFragment(ChatActivity.of(giftUserId));
                                }
                            });
                        } else if (bulletinTitle != null) {
                            bulletin = factory.createSimpleBulletin(icon, bulletinTitle, bulletinText);
                        } else {
                            bulletin = factory.createSimpleBulletin(icon, bulletinText);
                        }
                        bulletin.hideAfterBottomSheet = false;
                        bulletin.setDuration(Bulletin.DURATION_PROLONG);
                        if (message[0] != null) {
                            bulletin.setOnClickListener(v -> {
                                bulletin.hide();
                                if (isStarsGift) {
                                    BaseFragment fragment = LaunchActivity.getSafeLastFragment();
                                    if (fragment != null) {
                                        fragment.presentFragment(ChatActivity.of(MessageObject.getDialogId(message[0]), message[0].id));
                                    }
                                } else {
                                    TLRPC.TL_payments_getPaymentReceipt req2 = new TLRPC.TL_payments_getPaymentReceipt();
                                    req2.msg_id = message[0].id;
                                    req2.peer = MessagesController.getInstance(currentAccount).getInputPeer(message[0].peer_id);
                                    ConnectionsManager.getInstance(currentAccount).sendRequest(req2, (response2, error2) -> AndroidUtilities.runOnUIThread(() -> {
                                        if (response2 instanceof TLRPC.TL_payments_paymentReceiptStars) {
                                            StarsIntroActivity.showTransactionSheet(getContext(), false, currentAccount, (TLRPC.TL_payments_paymentReceiptStars) response2, getResourceProvider());
                                        } else if (response2 instanceof TLRPC.PaymentReceipt) {
                                            BaseFragment lastFragment3 = LaunchActivity.getLastFragment();
                                            if (lastFragment3 != null) {
                                                BaseFragment.BottomSheetParams params = new BaseFragment.BottomSheetParams();
                                                params.transitionFromLeft = true;
                                                params.allowNestedScroll = false;
                                                lastFragment3.showAsSheet(
                                                    new PaymentFormActivity((TLRPC.PaymentReceipt) response2)
                                                        .setCustomResultReceiver(customResultReceiver)
                                                        .setCustomAnyResultReceiver(customAnyResultReceiver),
                                                    params
                                                );
                                            }
                                        }
                                    }), ConnectionsManager.RequestFlagFailOnServerErrors);
                                }
                            });
                        }
                        bulletin.show(isStarsGiveaway);
                    });
                } else if (response instanceof TLRPC.TL_payments_paymentVerificationNeeded) {
                    AndroidUtilities.runOnUIThread(() -> {
                        if (this.customAnyResultReceiver != null) {
                            this.customAnyResultReceiver.run((TLRPC.TL_payments_paymentVerificationNeeded) response);
                        }

                        setDonePressed(false);
                        webviewLoading = true;
                        showEditDoneProgress(true, true);
                        if (progressView != null) {
                            progressView.setVisibility(View.VISIBLE);
                        }
                        if (doneItem != null) {
                            doneItem.setEnabled(false);
                            doneItem.getContentView().setVisibility(View.INVISIBLE);
                        }

                        INavigationLayout parentLayout = getParentLayout();
                        Activity parentActivity = getParentActivity();
                        getMessagesController().newMessageCallback = message -> {
                            if (MessageObject.getPeerId(message.peer_id) == botUser.id && message.action instanceof TLRPC.TL_messageActionPaymentSent) {
                                AndroidUtilities.runOnUIThread(() -> {

                                    paymentStatusSent = true;
                                    invoiceStatus = InvoiceStatus.PAID;

                                    onCheckoutSuccess(parentLayout, parentActivity);

                                    final boolean isStars = invoiceInput instanceof TLRPC.TL_inputInvoiceStars;
                                    final boolean isStarsGift = isStars && ((TLRPC.TL_inputInvoiceStars) invoiceInput).purpose instanceof TLRPC.TL_inputStorePaymentStarsGift;
                                    final boolean isStarsGiveaway = isStars && ((TLRPC.TL_inputInvoiceStars) invoiceInput).purpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway;
                                    if (!isStars && paymentFormCallback != null) {
                                        paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                                    }

                                    goToNextStep();
                                    if (isStars && paymentFormCallback != null) {
                                        paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                                    }
                                    final long giftUserId = getStarsGiftUserId();
                                    String giftUser = "";
                                    if (giftUserId > 0) {
                                        giftUser = UserObject.getForcedFirstName(getMessagesController().getUser(giftUserId));
                                    } else if (giftUserId < 0) {
                                        TLRPC.Chat chat = getMessagesController().getChat(-giftUserId);
                                        giftUser = chat != null ? chat.title : "";
                                    }
                                    long stars = getStars();
                                    int icon = isStars ? (isStarsGift || isStarsGiveaway ? R.raw.stars_send : R.raw.stars_topup) : R.raw.payment_success;
                                    CharSequence bulletinTitle = !isStars ? null : (isStarsGiveaway ? getString(R.string.StarsGiveawaySentPopup) : isStarsGift ? getString(R.string.StarsGiftSentPopup) : getString(R.string.StarsAcquired));
                                    CharSequence bulletinText = AndroidUtilities.replaceTags(
                                        isStars ?
                                            isStarsGiveaway ? LocaleController.formatPluralStringComma("StarsGiveawaySentPopupInfo", (int) stars) : LocaleController.formatPluralStringComma(isStarsGift ? "StarsGiftSentPopupInfo" : "StarsAcquiredInfo", (int) stars, giftUser) :
                                            LocaleController.formatString(R.string.PaymentInfoHint, totalPrice[0], currentItemName)
                                    );
                                    BaseFragment lastFragment = LaunchActivity.getSafeLastFragment();
                                    if (lastFragment == null) return;
                                    BulletinFactory factory = BulletinFactory.of(lastFragment);
                                    Bulletin bulletin;
                                    if (giftUserId != 0 && bulletinTitle != null && !isStarsGiveaway) {
                                        bulletin = factory.createSimpleBulletin(icon, bulletinTitle, bulletinText, getString(R.string.ViewInChat), () -> {
                                            BaseFragment lastFragment2 = LaunchActivity.getSafeLastFragment();
                                            if (lastFragment2 != null) {
                                                lastFragment2.presentFragment(ChatActivity.of(giftUserId));
                                            }
                                        });
                                    } else if (bulletinTitle != null) {
                                        bulletin = factory.createSimpleBulletin(icon, bulletinTitle, bulletinText);
                                    } else {
                                        bulletin = factory.createSimpleBulletin(icon, bulletinText);
                                    }
                                    bulletin.hideAfterBottomSheet = false;
                                    bulletin.setDuration(Bulletin.DURATION_PROLONG);
                                    if (message != null) {
                                        bulletin.setOnClickListener(v -> {
                                            bulletin.hide();
                                            if (isStarsGift) {
                                                BaseFragment fragment = LaunchActivity.getSafeLastFragment();
                                                if (fragment != null) {
                                                    fragment.presentFragment(ChatActivity.of(MessageObject.getDialogId(message), message.id));
                                                }
                                            } else {
                                                TLRPC.TL_payments_getPaymentReceipt req2 = new TLRPC.TL_payments_getPaymentReceipt();
                                                req2.msg_id = message.id;
                                                req2.peer = MessagesController.getInstance(currentAccount).getInputPeer(message.peer_id);
                                                ConnectionsManager.getInstance(currentAccount).sendRequest(req2, (response2, error2) -> AndroidUtilities.runOnUIThread(() -> {
                                                    if (response2 instanceof TLRPC.TL_payments_paymentReceiptStars) {
                                                        StarsIntroActivity.showTransactionSheet(getContext(), false, currentAccount, (TLRPC.TL_payments_paymentReceiptStars) response2, getResourceProvider());
                                                    } else if (response2 instanceof TLRPC.PaymentReceipt) {
                                                        BaseFragment lastFragment3 = LaunchActivity.getLastFragment();
                                                        if (lastFragment3 != null) {
                                                            BaseFragment.BottomSheetParams params = new BaseFragment.BottomSheetParams();
                                                            params.transitionFromLeft = true;
                                                            params.allowNestedScroll = false;
                                                            lastFragment3.showAsSheet(
                                                                new PaymentFormActivity((TLRPC.PaymentReceipt) response2)
                                                                    .setCustomResultReceiver(customResultReceiver)
                                                                    .setCustomAnyResultReceiver(customAnyResultReceiver),
                                                                params
                                                            );
                                                        }
                                                    }
                                                }), ConnectionsManager.RequestFlagFailOnServerErrors);
                                            }
                                        });
                                    }
                                    bulletin.show(isStarsGiveaway);
                                });
                                return true;
                            }
                            return false;
                        };

                        if (webView != null) {
                            webView.setVisibility(View.VISIBLE);
                            webView.loadUrl(webViewUrl = ((TLRPC.TL_payments_paymentVerificationNeeded) response).url);
                        }

                        paymentStatusSent = true;
                        invoiceStatus = InvoiceStatus.PENDING;
                        if (paymentFormCallback != null) {
                            paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                        }
                    });
                }
            } else {
                if (this.customErrorReceiver != null && this.customErrorReceiver.run(error)) {
                    return;
                }
                AndroidUtilities.runOnUIThread(() -> {
                    AlertsCreator.processError(currentAccount, error, PaymentFormActivity.this, req);
                    setDonePressed(false);
                    showEditDoneProgress(false, false);

                    paymentStatusSent = true;
                    invoiceStatus = InvoiceStatus.FAILED;
                    if (paymentFormCallback != null) {
                        paymentFormCallback.onInvoiceStatusChanged(invoiceStatus);
                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors | (allowUnregistered ? ConnectionsManager.RequestFlagWithoutLogin : 0));
    }

    private long getStars() {
        if (invoiceInput instanceof TLRPC.TL_inputInvoiceStars) {
            TLRPC.TL_inputInvoiceStars invoiceInputStars = (TLRPC.TL_inputInvoiceStars) invoiceInput;
            final TLRPC.InputStorePaymentPurpose purpose = invoiceInputStars.purpose;
            if (purpose instanceof TLRPC.TL_inputStorePaymentStarsGift) {
                return ((TLRPC.TL_inputStorePaymentStarsGift) purpose).stars;
            } else if (purpose instanceof TLRPC.TL_inputStorePaymentStarsTopup) {
                return ((TLRPC.TL_inputStorePaymentStarsTopup) purpose).stars;
            } else if (purpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway) {
                return ((TLRPC.TL_inputStorePaymentStarsGiveaway) purpose).stars;
            }
        }
        return 0;
    }

    private long getStarsGiftUserId() {
        if (invoiceInput instanceof TLRPC.TL_inputInvoiceStars) {
            TLRPC.TL_inputInvoiceStars invoiceInputStars = (TLRPC.TL_inputInvoiceStars) invoiceInput;
            final TLRPC.InputStorePaymentPurpose purpose = invoiceInputStars.purpose;
            if (purpose instanceof TLRPC.TL_inputStorePaymentStarsGift) {
                TLRPC.TL_inputStorePaymentStarsGift p = (TLRPC.TL_inputStorePaymentStarsGift) purpose;
                if (p.user_id != null) return p.user_id.user_id;
            } else if (purpose instanceof TLRPC.TL_inputStorePaymentStarsGiveaway) {
                TLRPC.TL_inputStorePaymentStarsGiveaway p = (TLRPC.TL_inputStorePaymentStarsGiveaway) purpose;
                if (p.boost_peer != null) return DialogObject.getPeerDialogId(p.boost_peer);
            }
        }
        return 0;
    }

    private void shakeField(int field) {
        shakeView(inputFields[field]);
    }

    private void shakeView(View view) {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        } catch (Exception ignored) {}
        AndroidUtilities.shakeViewSpring(view, 2.5f);
    }

    private void setDonePressed(boolean value) {
        donePressed = value;
        swipeBackEnabled = !value;
        if (actionBar != null && actionBar.getBackButton() != null) {
            actionBar.getBackButton().setEnabled(!donePressed);
        }
        if (detailSettingsCell[0] != null) {
            detailSettingsCell[0].setEnabled(!donePressed);
        }
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return swipeBackEnabled;
    }

    private void checkPassword() {
        if (UserConfig.getInstance(currentAccount).tmpPassword != null) {
            if (UserConfig.getInstance(currentAccount).tmpPassword.valid_until < ConnectionsManager.getInstance(currentAccount).getCurrentTime() + 60) {
                UserConfig.getInstance(currentAccount).tmpPassword = null;
                UserConfig.getInstance(currentAccount).saveConfig(false);
            }
        }
        if (UserConfig.getInstance(currentAccount).tmpPassword != null) {
            sendData();
            return;
        }
        if (inputFields[FIELD_SAVEDPASSWORD].length() == 0) {
            try {
                inputFields[FIELD_SAVEDPASSWORD].performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            } catch (Exception ignored) {}
            AndroidUtilities.shakeViewSpring(inputFields[FIELD_SAVEDPASSWORD], 2.5f);
            return;
        }
        final String password = inputFields[FIELD_SAVEDPASSWORD].getText().toString();
        showEditDoneProgress(true, true);
        setDonePressed(true);
        final TL_account.getPassword req = new TL_account.getPassword();
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                TL_account.Password currentPassword = (TL_account.Password) response;
                if (!TwoStepVerificationActivity.canHandleCurrentPassword(currentPassword, false)) {
                    AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString(R.string.UpdateAppAlert), true);
                    return;
                }
                if (!currentPassword.has_password) {
                    passwordOk = false;
                    goToNextStep();
                } else {
                    byte[] passwordBytes = AndroidUtilities.getStringBytes(password);

                    Utilities.globalQueue.postRunnable(() -> {
                        final byte[] x_bytes;
                        if (currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword.current_algo;
                            x_bytes = SRPHelper.getX(passwordBytes, algo);
                        } else {
                            x_bytes = null;
                        }

                        final TL_account.getTmpPassword req1 = new TL_account.getTmpPassword();
                        req1.period = 60 * 30;

                        RequestDelegate requestDelegate = (response1, error1) -> AndroidUtilities.runOnUIThread(() -> {
                            showEditDoneProgress(true, false);
                            setDonePressed(false);
                            if (response1 != null) {
                                passwordOk = true;
                                UserConfig.getInstance(currentAccount).tmpPassword = (TL_account.tmpPassword) response1;
                                UserConfig.getInstance(currentAccount).saveConfig(false);
                                goToNextStep();
                            } else {
                                if (error1.text.equals("PASSWORD_HASH_INVALID")) {
                                    try {
                                        inputFields[FIELD_SAVEDPASSWORD].performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                                    } catch (Exception ignored) {}
                                    AndroidUtilities.shakeViewSpring(inputFields[FIELD_SAVEDPASSWORD], 3.25f);
                                    inputFields[FIELD_SAVEDPASSWORD].setText("");
                                } else {
                                    AlertsCreator.processError(currentAccount, error1, PaymentFormActivity.this, req1);
                                }
                            }
                        });

                        if (currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) currentPassword.current_algo;
                            req1.password = SRPHelper.startCheck(x_bytes, currentPassword.srp_id, currentPassword.srp_B, algo);
                            if (req1.password == null) {
                                TLRPC.TL_error error2 = new TLRPC.TL_error();
                                error2.text = "ALGO_INVALID";
                                requestDelegate.run(null, error2);
                                return;
                            }
                            ConnectionsManager.getInstance(currentAccount).sendRequest(req1, requestDelegate, ConnectionsManager.RequestFlagFailOnServerErrors | ConnectionsManager.RequestFlagWithoutLogin);
                        } else {
                            TLRPC.TL_error error2 = new TLRPC.TL_error();
                            error2.text = "PASSWORD_HASH_INVALID";
                            requestDelegate.run(null, error2);
                        }
                    });
                }
            } else {
                AlertsCreator.processError(currentAccount, error, PaymentFormActivity.this, req);
                showEditDoneProgress(true, false);
                setDonePressed(false);
            }
        }), ConnectionsManager.RequestFlagFailOnServerErrors | (allowUnregistered ? ConnectionsManager.RequestFlagWithoutLogin : 0));
    }

    private void showEditDoneProgress(final boolean animateDoneItem, final boolean show) {
        if (doneItemAnimation != null) {
            doneItemAnimation.cancel();
        }
        if (animateDoneItem && doneItem != null) {
            doneItemAnimation = new AnimatorSet();
            if (show) {
                progressView.setVisibility(View.VISIBLE);
                doneItem.setEnabled(false);
                doneItemAnimation.playTogether(
                        ObjectAnimator.ofFloat(doneItem.getContentView(), View.SCALE_X, 0.1f),
                        ObjectAnimator.ofFloat(doneItem.getContentView(), View.SCALE_Y, 0.1f),
                        ObjectAnimator.ofFloat(doneItem.getContentView(), View.ALPHA, 0.0f),
                        ObjectAnimator.ofFloat(progressView, View.SCALE_X, 1.0f),
                        ObjectAnimator.ofFloat(progressView, View.SCALE_Y, 1.0f),
                        ObjectAnimator.ofFloat(progressView, View.ALPHA, 1.0f));
            } else {
                if (webView != null) {
                    doneItemAnimation.playTogether(
                            ObjectAnimator.ofFloat(progressView, View.SCALE_X, 0.1f),
                            ObjectAnimator.ofFloat(progressView, View.SCALE_Y, 0.1f),
                            ObjectAnimator.ofFloat(progressView, View.ALPHA, 0.0f));
                } else {
                    doneItem.getContentView().setVisibility(View.VISIBLE);
                    doneItem.setEnabled(true);
                    doneItemAnimation.playTogether(
                            ObjectAnimator.ofFloat(progressView, View.SCALE_X, 0.1f),
                            ObjectAnimator.ofFloat(progressView, View.SCALE_Y, 0.1f),
                            ObjectAnimator.ofFloat(progressView, View.ALPHA, 0.0f));

                    if (!isFinishing()) {
                        doneItemAnimation.playTogether(ObjectAnimator.ofFloat(doneItem.getContentView(), View.SCALE_X, 1.0f),
                                ObjectAnimator.ofFloat(doneItem.getContentView(), View.SCALE_Y, 1.0f),
                                ObjectAnimator.ofFloat(doneItem.getContentView(), View.ALPHA, 1.0f));
                    }
                }
            }
            doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (doneItemAnimation != null && doneItemAnimation.equals(animation)) {
                        if (!show) {
                            progressView.setVisibility(View.INVISIBLE);
                        } else {
                            doneItem.getContentView().setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (doneItemAnimation != null && doneItemAnimation.equals(animation)) {
                        doneItemAnimation = null;
                    }
                }
            });
            doneItemAnimation.setDuration(150);
            doneItemAnimation.start();
        } else if (payTextView != null) {
            doneItemAnimation = new AnimatorSet();
            if (show) {
                progressViewButton.setVisibility(View.VISIBLE);
                bottomLayout.setEnabled(false);
                doneItemAnimation.playTogether(
                        ObjectAnimator.ofFloat(payTextView, View.SCALE_X, 0.1f),
                        ObjectAnimator.ofFloat(payTextView, View.SCALE_Y, 0.1f),
                        ObjectAnimator.ofFloat(payTextView, View.ALPHA, 0.0f),
                        ObjectAnimator.ofFloat(progressViewButton, View.SCALE_X, 1.0f),
                        ObjectAnimator.ofFloat(progressViewButton, View.SCALE_Y, 1.0f),
                        ObjectAnimator.ofFloat(progressViewButton, View.ALPHA, 1.0f));
            } else {
                payTextView.setVisibility(View.VISIBLE);
                bottomLayout.setEnabled(true);
                doneItemAnimation.playTogether(
                        ObjectAnimator.ofFloat(progressViewButton, View.SCALE_X, 0.1f),
                        ObjectAnimator.ofFloat(progressViewButton, View.SCALE_Y, 0.1f),
                        ObjectAnimator.ofFloat(progressViewButton, View.ALPHA, 0.0f),
                        ObjectAnimator.ofFloat(payTextView, View.SCALE_X, 1.0f),
                        ObjectAnimator.ofFloat(payTextView, View.SCALE_Y, 1.0f),
                        ObjectAnimator.ofFloat(payTextView, View.ALPHA, 1.0f));

            }
            doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (doneItemAnimation != null && doneItemAnimation.equals(animation)) {
                        if (!show) {
                            progressViewButton.setVisibility(View.INVISIBLE);
                        } else {
                            payTextView.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (doneItemAnimation != null && doneItemAnimation.equals(animation)) {
                        doneItemAnimation = null;
                    }
                }
            });
            doneItemAnimation.setDuration(150);
            doneItemAnimation.start();
        }
    }

    @Override
    public boolean presentFragment(BaseFragment fragment) {
        onPresentFragment(fragment);
        return super.presentFragment(fragment);
    }

    @Override
    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        onPresentFragment(fragment);
        return super.presentFragment(fragment, removeLast);
    }

    private void onPresentFragment(BaseFragment fragment) {
        AndroidUtilities.hideKeyboard(fragmentView);
        if (fragment instanceof PaymentFormActivity) {
            ((PaymentFormActivity) fragment).paymentFormCallback = paymentFormCallback;
            ((PaymentFormActivity) fragment).resourcesProvider = resourcesProvider;
            ((PaymentFormActivity) fragment).needPayAfterTransition = needPayAfterTransition;
            ((PaymentFormActivity) fragment).savedCredentialsCard = savedCredentialsCard;
        }
    }

    @Override
    public boolean onBackPressed(boolean invoked) {
        if (webView != null && shouldNavigateBack) {
            if (invoked) {
                webView.loadUrl(webViewUrl);
                shouldNavigateBack = false;
            }
            return false;
        }
        return !donePressed;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();

        arrayList.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2));
        arrayList.add(new ThemeDescription(progressViewButton, 0, null, null, null, null, Theme.key_contextProgressInner2));
        arrayList.add(new ThemeDescription(progressViewButton, 0, null, null, null, null, Theme.key_contextProgressOuter2));

        if (inputFields != null) {
            for (int a = 0; a < inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        if (radioCells != null) {
            for (int a = 0; a < radioCells.length; a++) {
                arrayList.add(new ThemeDescription(radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(radioCells[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
                arrayList.add(new ThemeDescription(radioCells[a], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(radioCells[a], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
                arrayList.add(new ThemeDescription(radioCells[a], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
            }
        } else {
            arrayList.add(new ThemeDescription(null, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked));
        }
        for (int a = 0; a < headerCell.length; a++) {
            arrayList.add(new ThemeDescription(headerCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(headerCell[a], 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        }
        for (int a = 0; a < sectionCell.length; a++) {
            arrayList.add(new ThemeDescription(sectionCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        for (int a = 0; a < bottomCell.length; a++) {
            arrayList.add(new ThemeDescription(bottomCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(bottomCell[a], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(bottomCell[a], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        }
        for (int a = 0; a < dividers.size(); a++) {
            arrayList.add(new ThemeDescription(dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        }

        arrayList.add(new ThemeDescription(codeFieldCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(codeFieldCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(codeFieldCell, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteHintText));

        arrayList.add(new ThemeDescription(textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));

        arrayList.add(new ThemeDescription(checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));

        for (int a = 0; a < settingsCell.length; a++) {
            arrayList.add(new ThemeDescription(settingsCell[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(settingsCell[a], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(settingsCell[a], 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        }

        arrayList.add(new ThemeDescription(payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));

        arrayList.add(new ThemeDescription(linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextPriceCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextPriceCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        arrayList.add(new ThemeDescription(detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));

        for (int a = 1; a < detailSettingsCell.length; a++) {
            arrayList.add(new ThemeDescription(detailSettingsCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(detailSettingsCell[a], 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        }

        arrayList.add(new ThemeDescription(paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(paymentInfoCell, 0, new Class[]{PaymentInfoCell.class}, new String[]{"detailExTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        arrayList.add(new ThemeDescription(bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));

        return arrayList;
    }

    private class BottomFrameLayout extends FrameLayout {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float progress;
        SpringAnimation springAnimation;

        public BottomFrameLayout(@NonNull Context context, TLRPC.PaymentForm paymentForm) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawColor(getThemedColor(Theme.key_switchTrackBlue));
            paint.setColor(getThemedColor(Theme.key_contacts_inviteBackground));
            canvas.drawCircle(LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(28) : AndroidUtilities.dp(28), -AndroidUtilities.dp(28), Math.max(getWidth(), getHeight()) * progress, paint);
        }

        public void setChecked(boolean checked, boolean animated) {
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            float to = checked ? 1f : 0f;
            if (animated) {
                if (progress == to) {
                    return;
                }
                springAnimation = new SpringAnimation(new FloatValueHolder(progress * 100f))
                        .setSpring(new SpringForce(to * 100f)
                                .setStiffness(checked ? 500f : 650f)
                                .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY));
                springAnimation.addUpdateListener((animation, value, velocity) -> {
                    progress = value / 100f;
                    if (payTextView != null) {
                        payTextView.setAlpha(0.8f + 0.2f * progress);
                    }
                    invalidate();
                });
                springAnimation.addEndListener((animation, canceled1, value, velocity) -> {
                    if (animation == springAnimation) {
                        springAnimation = null;
                    }
                });
                springAnimation.start();
            } else {
                progress = to;
                if (payTextView != null) {
                    payTextView.setAlpha(0.8f + 0.2f * progress);
                }
                invalidate();
            }
        }
    }
}
