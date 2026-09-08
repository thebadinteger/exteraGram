;
//    }

    public boolean onClick() {
        if (shouldExpand && expandT <= 0) {
            updateCollapse(true, true);
            return true;
        }
        return false;
    }
    private float easeInOutCubic(float x) {
        return x < 0.5 ? 4 * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 3) / 2;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (textLayout != null) {
            final CharSequence text = buildAccessibilityText();
            final CharSequence valueText = valueTextView.getText();
            info.setClassName("android.widget.TextView");
            if (TextUtils.isEmpty(valueText)) {
                info.setText(text);
            } else {
                info.setText(TextUtils.concat(valueText, ": ", text));
            }
        }
    }

    private CharSequence buildAccessibilityText() {
        if (accessibilityText != null) return accessibilityText;
        if (stringBuilder == null) return null;
        ClickableSpan[] spans = stringBuilder.getSpans(0, stringBuilder.length(), ClickableSpan.class);
        if (spans == null || spans.length == 0) {
            return accessibilityText = stringBuilder;
        }
        SpannableStringBuilder sb = new SpannableStringBuilder(stringBuilder);
        for (ClickableSpan span : spans) {
            int start = sb.getSpanStart(span);
            int end = sb.getSpanEnd(span);
            if (start < 0 || end <= start) continue;
            sb.removeSpan(span);
            final ClickableSpan original = span;
            sb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onLinkClick(original, textLayout, 0);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return accessibilityText = sb;
    }

    public void setMoreButtonDisabled(boolean moreButtonDisabled) {
        this.moreButtonDisabled = moreButtonDisabled;
    }
}
