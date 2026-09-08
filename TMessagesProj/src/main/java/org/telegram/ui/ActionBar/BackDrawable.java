, 0);
        if (!alwaysClose) {
            canvas.rotate(currentRotation * (reverseAngle ? -225 : 135));
        } else {
            canvas.rotate(135 + currentRotation * (reverseAngle ? -180 : 180));
            rotation = 1.0f;
        }
        canvas.drawLine(AndroidUtilities.dp(AndroidUtilities.lerp(-6.75f, -8f, rotation)), 0, AndroidUtilities.dp(8) - (paint.getStrokeWidth() / 2f) * (1f - rotation), 0, paint);
        float startYDiff = AndroidUtilities.dp(-0.25f);
        float endYDiff = AndroidUtilities.dp(AndroidUtilities.lerp(7f, 8f, rotation)) - (paint.getStrokeWidth() / 4f) * (1f - rotation);
        float startXDiff = AndroidUtilities.dp(AndroidUtilities.lerp(-7f - 0.25f, 0f, rotation));
        float endXDiff = 0;
        canvas.drawLine(startXDiff, -startYDiff, endXDiff, -endYDiff, paint);
        canvas.drawLine(startXDiff, startYDiff, endXDiff, endYDiff, paint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24);
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24);
    }
}
