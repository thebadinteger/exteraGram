}

    public void setActualAreaSize(float width, float height) {
        actualAreaSize.width = width;
        actualAreaSize.height = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PointF centerPoint = getActualCenterPoint();
        float innerRadius = getActualInnerRadius();
        float outerRadius = getActualOuterRadius();
        canvas.translate(centerPoint.x, centerPoint.y);

        if (type == 0) {
            canvas.rotate(angle);

            float space = AndroidUtilities.dp(6.0f);
            float length = AndroidUtilities.dp(12.0f);
            float thickness = AndroidUtilities.dp(1.5f);
            for (int i = 0; i < 30; i++) {
                canvas.drawRect(i * (length + space), -innerRadius, i * (length + space) + length, thickness - innerRadius, paint);
                float left = -i * (length + space) - space - length;
                float right = -i * (length + space) - space;
                canvas.drawRect(left, -innerRadius, right, thickness - innerRadius, paint);

                canvas.drawRect(i * (length + space), innerRadius, length + i * (length + space), thickness + innerRadius, paint);
                canvas.drawRect(left, innerRadius, right, thickness + innerRadius, paint);
            }

            length = AndroidUtilities.dp(6.0f);
            for (int i = 0; i < 64; i++) {
                canvas.drawRect(i * (length + space), -outerRadius, length + i * (length + space), thickness - outerRadius, paint);
                float left = -i * (length + space) - space - length;
                float right = -i * (length + space) - space;
                canvas.drawRect(left, -outerRadius, right, thickness - outerRadius, paint);

                canvas.drawRect(i * (length + space), outerRadius, length + i * (length + space), thickness + outerRadius, paint);
                canvas.drawRect(left, outerRadius, right, thickness + outerRadius, paint);
            }
        } else if (type == 1) {
            float radSpace = 6.15f;
            float radLen = 10.2f;
            arcRect.set(-innerRadius, -innerRadius, innerRadius, innerRadius);
            for (int i = 0; i < 22; i++) {
                canvas.drawArc(arcRect, i * (radSpace + radLen), radLen, false, arcPaint);
            }

            radSpace = 2.02f;
            radLen = 3.6f;
            arcRect.set(-outerRadius, -outerRadius, outerRadius, outerRadius);
            for (int i = 0; i < 64; i++) {
                canvas.drawArc(arcRect, i * (radSpace + radLen), radLen, false, arcPaint);
            }
        }
        canvas.drawCircle(0, 0, AndroidUtilities.dp(8), paint);
    }

    private PointF getActualCenterPoint() {
        float x = (getWidth() - actualAreaSize.width) / 2 + centerPoint.x * actualAreaSize.width;
        float y = (Build.VERSION.SDK_INT >= 21 && !inBubbleMode ? AndroidUtilities.statusBarHeight : 0) + (getHeight() - actualAreaSize.height) / 2 - (actualAreaSize.width - actualAreaSize.height) / 2 + centerPoint.y * actualAreaSize.width;
        return new PointF(x, y);
    }

    private float getActualInnerRadius() {
        return (Math.min(actualAreaSize.width, actualAreaSize.height)) * falloff;
    }

    private float getActualOuterRadius() {
        return (Math.min(actualAreaSize.width, actualAreaSize.height)) * size;
    }
}
