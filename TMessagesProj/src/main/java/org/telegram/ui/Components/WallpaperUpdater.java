}

    public String getCurrentPicturePath() {
        return currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        currentPicturePath = value;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                AndroidUtilities.addMediaToGallery(currentPicturePath);
                FileOutputStream stream = null;
                try {
                    currentWallpaperPath = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.random.nextInt() + ".jpg");
                    Point screenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap = ImageLoader.loadBitmap(currentPicturePath, null, screenSize.x, screenSize.y, true);
                    stream = new FileOutputStream(currentWallpaperPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    delegate.didSelectWallpaper(currentWallpaperPath, bitmap, false);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                currentPicturePath = null;
            } else if (requestCode == 11) {
                if (data == null || data.getData() == null) {
                    return;
                }
                try {
                    currentWallpaperPath = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.random.nextInt() + ".jpg");
                    Point screenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap = ImageLoader.loadBitmap(null, data.getData(), screenSize.x, screenSize.y, true);
                    FileOutputStream stream = new FileOutputStream(currentWallpaperPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    delegate.didSelectWallpaper(currentWallpaperPath, bitmap, false);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }
}
