, MeasureSpec.EXACTLY));
                        }
                    };
                    cell.getImageView().setLayerNum(7);
                    view = cell;
                    break;
                case 1:
                    view = new EmptyCell(context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(context, 8, true, false, resourcesProvider);
                    break;
                case TYPE_ADD_STICKER:
                    view = new AddStickerBtnView(context, resourcesProvider);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (stickerSetCovereds != null) {
                switch (holder.getItemViewType()) {
                    case 0:
                        TLRPC.Document sticker = (TLRPC.Document) cache.get(position);
                        StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                        cell.setSticker(sticker, positionsToSets.get(position), false);
                        break;
                    case 1:
                        ((EmptyCell) holder.itemView).setHeight(dp(82));
                        break;
                    case 2:
                        TLRPC.StickerSetCovered stickerSetCovered = stickerSetCovereds.get((Integer) cache.get(position));
                        FeaturedStickerSetInfoCell cell2 = (FeaturedStickerSetInfoCell) holder.itemView;
                        cell2.setStickerSet(stickerSetCovered, false);
                        break;
                }
            } else if (importingStickers != null) {
                ((StickerEmojiCell) holder.itemView).setSticker(importingStickersPaths.get(position));
            } else {
                if (holder.getItemViewType() != TYPE_ADD_STICKER) {
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    if (stickerSet == null) return;
                    cell.setSticker(stickerSet.documents.get(position), null, stickerSet, null, showEmoji, isEditModeEnabled);
                    cell.editModeIcon.setOnClickListener(v -> {
                        ContentPreviewViewer.getInstance().setDelegate(previewDelegate);
                        ContentPreviewViewer.getInstance().showMenuFor(cell);
                    });
                }
            }
        }

        @Override
        public void notifyDataSetChanged() {
            if (stickerSetCovereds != null) {
                int width = gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                stickersPerRow = width / dp(72);
                layoutManager.setSpanCount(stickersPerRow);
                cache.clear();
                positionsToSets.clear();
                totalItems = 0;
                stickersRowCount = 0;
                for (int a = 0; a < stickerSetCovereds.size(); a++) {
                    TLRPC.StickerSetCovered pack = stickerSetCovereds.get(a);
                    List<TLRPC.Document> documents;
                    if (pack instanceof TLRPC.TL_stickerSetFullCovered) {
                        documents = ((TLRPC.TL_stickerSetFullCovered) pack).documents;
                    } else {
                        documents = pack.covers;
                    }
                    if (documents != null) {
                        documents = documents.subList(0, Math.min(documents.size(), stickersPerRow));
                    }
                    if (documents == null || documents.isEmpty() && pack.cover == null) {
                        continue;
                    }
                    stickersRowCount++;
                    positionsToSets.put(totalItems, pack);
                    cache.put(totalItems++, a);
                    int startRow = totalItems / stickersPerRow;
                    int count;
                    if (!documents.isEmpty()) {
                        count = (int) Math.ceil(documents.size() / (float) stickersPerRow);
                        for (int b = 0; b < documents.size(); b++) {
                            cache.put(b + totalItems, documents.get(b));
                        }
                    } else {
                        count = 1;
                        cache.put(totalItems, pack.cover);
                    }
                    for (int b = 0; b < count * stickersPerRow; b++) {
                        positionsToSets.put(totalItems + b, pack);
                    }
                    totalItems += count * stickersPerRow;
                }
            } else if (importingStickersPaths != null) {
                totalItems = importingStickersPaths.size();
            } else {
                totalItems = stickerSet != null ? stickerSet.documents.size() : 0;
                if (stickerSet != null && stickerSet.set.creator && stickerSet.documents.size() < STICKERS_MAX_COUNT && !DISABLE_STICKER_EDITOR && !stickerSet.set.masks && !stickerSet.set.emojis) {
                    totalItems++;
                }
            }
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyItemRemoved(int position) {
            if (importingStickersPaths != null) {
                totalItems = importingStickersPaths.size();
            }
            super.notifyItemRemoved(position);
        }

        public void updateColors() {
            if (stickerSetCovereds != null) {
                for (int i = 0, size = gridView.getChildCount(); i < size; i++) {
                    final View child = gridView.getChildAt(i);
                    if (child instanceof FeaturedStickerSetInfoCell) {
                        ((FeaturedStickerSetInfoCell) child).updateColors();
                    }
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> descriptions, ThemeDescription.ThemeDescriptionDelegate delegate) {
            if (stickerSetCovereds != null) {
                FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, gridView, delegate);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void enableEditMode() {
        if (isEditModeEnabled) {
            return;
        }
        dragAndDropHelper.attachToRecyclerView(gridView);
        isEditModeEnabled = true;
        stickersShaker.startShake();
        AndroidUtilities.forEachViews(gridView, view -> {
            if (view instanceof StickerEmojiCell) {
                ((StickerEmojiCell) view).enableEditMode(true);
            }
        });
        optionsButton.postDelayed(() -> adapter.notifyDataSetChanged(), 200);
//        optionsButton.animate().alpha(0f).start();
        pickerBottomLayout.setText(LocaleController.getString(R.string.Done), true);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void disableEditMode() {
        if (!isEditModeEnabled) {
            return;
        }
        dragAndDropHelper.attachToRecyclerView(null);
        isEditModeEnabled = false;
        stickersShaker.stopShake(true);
        AndroidUtilities.forEachViews(gridView, view -> {
            if (view instanceof StickerEmojiCell) {
                ((StickerEmojiCell) view).disableEditMode(true);
            }
        });
        optionsButton.postDelayed(() -> adapter.notifyDataSetChanged(), 200);
//        optionsButton.animate().alpha(1f).start();
        pickerBottomLayout.setText(LocaleController.getString(R.string.EditStickers), true);
    }

    @Override
    public void onBackPressed() {
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().closeWithMenu();
            return;
        }
        super.onBackPressed();
    }

    private boolean ignoreMasterDismiss;
    private Runnable masterDismissListener;
    public void setOnMasterDismiss(Runnable listener) {
        masterDismissListener = listener;
    }

    private static class AddStickerBtnView extends FrameLayout {

        public AddStickerBtnView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            View btnView = new View(context);
            Drawable circle = Theme.createRoundRectDrawable(dp(28), Theme.multAlpha(Theme.getColor(Theme.key_chat_emojiPanelIcon, resourcesProvider), .12f));
            Drawable drawable = context.getResources().getDrawable(R.drawable.filled_add_sticker).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelIcon, resourcesProvider), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(dp(56), dp(56));
            combinedDrawable.setIconSize(dp(24), dp(24));
            btnView.setBackground(combinedDrawable);
            ScaleStateListAnimator.apply(btnView);
            addView(btnView, LayoutHelper.createFrame(56, 56, Gravity.CENTER));
        }
    }

    private static class StickersShaker {
        private static final int MAX_SHAKERS = 6;
        private final List<ValueAnimator> rotateAnimators = new ArrayList<>();
        private final List<ValueAnimator> translateXAnimators = new ArrayList<>();
        private final List<ValueAnimator> translateYAnimators = new ArrayList<>();

        private final List<Float> imageRotations = new ArrayList<>();
        private final List<Float> imageTranslationsX = new ArrayList<>();
        private final List<Float> imageTranslationsY = new ArrayList<>();

        private void init() {
            if (!imageRotations.isEmpty()) return;
            for (int i = 0; i < MAX_SHAKERS; i++) {
                imageRotations.add(0f);
                imageTranslationsX.add(0f);
                imageTranslationsY.add(0f);
            }
        }

        public float getRotationValueForPos(int pos) {
            if (imageRotations.isEmpty()) return 0;
            pos = pos - ((pos / MAX_SHAKERS) * MAX_SHAKERS);
            return imageRotations.get(pos);
        }

        public float getTranslateXValueForPos(int pos) {
            if (imageTranslationsX.isEmpty()) return 0;
            pos = pos - ((pos / MAX_SHAKERS) * MAX_SHAKERS);
            return imageTranslationsX.get(pos);
        }

        public float getTranslateYValueForPos(int pos) {
            if (imageTranslationsY.isEmpty()) return 0;
            pos = pos - ((pos / MAX_SHAKERS) * MAX_SHAKERS);
            return imageTranslationsY.get(pos);
        }

        public void startShake() {
            stopShake(false);
            init();
            for (int i = 0; i < MAX_SHAKERS; i++) {
                final int pos = i;
                int duration = 300;
                long currentTime = (long) (Utilities.random.nextFloat() * duration);

                ValueAnimator rotateAnimator = ValueAnimator.ofFloat(0, -2f, 0, 2f, 0f);
                rotateAnimator.addUpdateListener(animation -> {
                    imageRotations.set(pos, (float) animation.getAnimatedValue());
                });
                rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
                rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
                rotateAnimator.setInterpolator(new LinearInterpolator());
                rotateAnimator.setCurrentPlayTime(currentTime);
                rotateAnimator.setDuration(duration);
                rotateAnimator.start();

                float max = dp(0.5f);
                ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, max, 0, -max, 0);
                translateXAnimator.addUpdateListener(animation -> {
                    imageTranslationsX.set(pos, (float) animation.getAnimatedValue());
                });
                translateXAnimator.setRepeatCount(ValueAnimator.INFINITE);
                translateXAnimator.setRepeatMode(ValueAnimator.RESTART);
                translateXAnimator.setInterpolator(new LinearInterpolator());
                translateXAnimator.setCurrentPlayTime(currentTime);
                translateXAnimator.setDuration((long) (duration * 1.2));
                translateXAnimator.start();

                ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, max, 0 - max, 0);
                translateYAnimator.addUpdateListener(animation -> {
                    imageTranslationsY.set(pos, (float) animation.getAnimatedValue());
                });
                translateYAnimator.setRepeatCount(ValueAnimator.INFINITE);
                translateYAnimator.setRepeatMode(ValueAnimator.RESTART);
                translateYAnimator.setInterpolator(new LinearInterpolator());
                translateYAnimator.setCurrentPlayTime(currentTime);
                translateYAnimator.setDuration(duration);
                translateYAnimator.start();

                rotateAnimators.add(rotateAnimator);
                translateXAnimators.add(translateXAnimator);
                translateYAnimators.add(translateYAnimator);
            }
        }

        public void stopShake(boolean animate) {
            for (int i = 0; i < rotateAnimators.size(); i++) {
                final int pos = i;
                rotateAnimators.get(i).cancel();
                if (animate) {
                    ValueAnimator animator = ValueAnimator.ofFloat(imageRotations.get(i), 0f);
                    animator.addUpdateListener(animation -> {
                        imageRotations.set(pos, (float) animation.getAnimatedValue());
                    });
                    animator.setDuration(100);
                    animator.start();
                }
            }

            for (int i = 0; i < translateXAnimators.size(); i++) {
                final int pos = i;
                translateXAnimators.get(i).cancel();
                if (animate) {
                    ValueAnimator animator = ValueAnimator.ofFloat(imageTranslationsX.get(i), 0f);
                    animator.addUpdateListener(animation -> {
                        imageTranslationsX.set(pos, (float) animation.getAnimatedValue());
                    });
                    animator.setDuration(100);
                    animator.start();
                }
            }

            for (int i = 0; i < translateYAnimators.size(); i++) {
                final int pos = i;
                translateYAnimators.get(i).cancel();
                if (animate) {
                    ValueAnimator animator = ValueAnimator.ofFloat(imageTranslationsY.get(i), 0f);
                    animator.addUpdateListener(animation -> {
                        imageTranslationsY.set(pos, (float) animation.getAnimatedValue());
                    });
                    animator.setDuration(100);
                    animator.start();
                }
            }

            translateYAnimators.clear();
            translateXAnimators.clear();
            rotateAnimators.clear();
        }
    }
}
