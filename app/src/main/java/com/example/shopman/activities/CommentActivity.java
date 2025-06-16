package com.example.shopman.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.example.shopman.R;
import com.example.shopman.adapters.CommentAdapter;
import com.example.shopman.models.Comments.Comment;
import com.example.shopman.models.Comments.CommentResponse;
import com.example.shopman.models.Comments.RepliesResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private static final int COMMENT_PAGE_SIZE = 10;
    private static final int MAX_IMAGES = 3;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1000;
    private static final int REQUEST_CODE_SELECT_IMAGE_DIALOG = 1001;

    // Views
    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView commentsRecyclerView;
    private ProgressBar commentsProgressBar;
    private EditText etComment;
    private RatingBar rbRating;
    private ImageButton btnAddImage;
    private ImageButton btnSend;
    private TextView tvReplyHint;
    private LinearLayout llImagePreviewContainer;

    // Data
    private ApiManager apiManager;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private List<Uri> selectedImageUris = new ArrayList<>(); // Cho activity
    private List<Uri> selectedImageUrisDialog = new ArrayList<>(); // Cho dialog
    private String productId;
    private String nextCommentCursor;
    private boolean isLoadingComments, isLastCommentPage;
    private Comment replyingTo;
    private BottomSheetDialog currentDialog;
    private int currentRequestCode = -1;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        productId = getIntent().getStringExtra("productId");
        if (TextUtils.isEmpty(productId)) {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiManager = new ApiManager(this);
        initAdapters();
        initMediaPickers();
        setupListeners();

        loadComments();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsProgressBar = findViewById(R.id.commentsProgressBar);
        etComment = findViewById(R.id.etComment);
        rbRating = findViewById(R.id.rbRating);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSend = findViewById(R.id.btnSend);
        tvReplyHint = findViewById(R.id.tvReplyHint);
        llImagePreviewContainer = findViewById(R.id.llImagePreviewContainer);

        tvTitle.setText("Bình luận");
        rbRating.setVisibility(View.VISIBLE);
        rbRating.setEnabled(true);
        rbRating.setIsIndicator(false);
    }

    private void initAdapters() {
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, comments, new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyComment(Comment comment) {
                if (comment != null && comment.getId() != 0) {
                    replyingTo = comment;
                    tvReplyHint.setVisibility(View.VISIBLE);
                    tvReplyHint.setText("Trả lời: " + (comment.getUser() != null ? comment.getUser().getName() : "Người dùng"));
                    rbRating.setVisibility(View.GONE);
                    etComment.requestFocus();
                } else {
                    Log.e(TAG, "onReplyComment: Invalid comment or ID");
                }
            }

            @Override
            public void onEditComment(Comment comment) {
                if (comment != null) showCommentDialog(comment, false, true);
            }

            @Override
            public void onDeleteComment(Comment comment) {
                if (comment != null) deleteComment(comment);
            }

            @Override
            public void onReportComment(Comment comment) {
                if (comment != null) {
                    Toast.makeText(CommentActivity.this, "Báo cáo bình luận: " + comment.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentsRecyclerView.setAdapter(commentAdapter);
        commentsRecyclerView.setHasFixedSize(true);
    }

    private void initMediaPickers() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openImagePicker(currentRequestCode, null);
                Log.d(TAG, "Permission granted, opening image picker with requestCode: " + currentRequestCode);
            } else {
                Toast.makeText(this, "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Permission denied for requestCode: " + currentRequestCode);
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d(TAG, "Image picker result received: resultCode=" + result.getResultCode() + ", data=" + result.getData());
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                if (currentRequestCode == REQUEST_CODE_SELECT_IMAGE) {
                    handleImageSelection(data, selectedImageUris, llImagePreviewContainer);
                } else if (currentRequestCode == REQUEST_CODE_SELECT_IMAGE_DIALOG) {
                    handleImageSelection(data, selectedImageUrisDialog, currentDialog != null ? currentDialog.findViewById(R.id.llImagePreviewContainer) : null);
                    Log.d(TAG, "Selected images for dialog: " + selectedImageUrisDialog.size());
                }
            } else {
                Log.w(TAG, "Image picker failed: resultCode=" + result.getResultCode() + ", data=" + result.getData());
            }
        });
    }

    private void handleImageSelection(Intent data, List<Uri> targetList, LinearLayout targetPreview) {
        Log.d(TAG, "Handling image selection: data=" + data + ", targetList size=" + targetList.size());
        if (data.getClipData() != null) {
            int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGES - targetList.size());
            Log.d(TAG, "ClipData found, item count=" + data.getClipData().getItemCount() + ", adding " + count + " items");
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                targetList.add(imageUri);
                Log.d(TAG, "Added Uri: " + imageUri);
            }
        } else if (data.getData() != null && targetList.size() < MAX_IMAGES) {
            Uri imageUri = data.getData();
            targetList.add(imageUri);
            Log.d(TAG, "Added single Uri: " + imageUri);
        } else {
            Log.w(TAG, "No valid data or max images reached: data=" + data.getData() + ", current size=" + targetList.size());
        }
        if (targetList.size() >= MAX_IMAGES) {
            Toast.makeText(this, "Tối đa " + MAX_IMAGES + " ảnh", Toast.LENGTH_SHORT).show();
        }
        if (targetPreview != null && targetList.size() > 0) {
            updateImagePreview(targetPreview, targetList, null, false);
            Log.d(TAG, "Previewing " + targetList.size() + " images with Uri: " + targetList);
        }
    }

    private void openImagePicker(int requestCode, LinearLayout targetPreview) {
        currentRequestCode = requestCode;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void checkStoragePermission(int requestCode, LinearLayout targetPreview) {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker(requestCode, targetPreview);
        } else {
            currentRequestCode = requestCode;
            requestPermissionLauncher.launch(permission);
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnAddImage.setOnClickListener(v -> checkStoragePermission(REQUEST_CODE_SELECT_IMAGE, llImagePreviewContainer));
        rbRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) Toast.makeText(this, "Đánh giá: " + rating, Toast.LENGTH_SHORT).show();
        });
        btnSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            float rating = rbRating.getRating();
            Integer finalRating = replyingTo == null && rating > 0 ? (int) rating : null;
            Integer parentId = replyingTo != null ? replyingTo.getId() : null;
            List<String> existingImageUrls = new ArrayList<>();

            if (!selectedImageUris.isEmpty()) {
                uploadImagesToCloudinary(content, finalRating, parentId, null, existingImageUrls, llImagePreviewContainer, selectedImageUris, null);
            } else {
                postComment(content, finalRating, parentId, existingImageUrls);
            }
            etComment.setText("");
            rbRating.setRating(0);
            selectedImageUris.clear();
            llImagePreviewContainer.removeAllViews();
            tvReplyHint.setVisibility(View.GONE);
            replyingTo = null;
            rbRating.setVisibility(View.VISIBLE);
        });

        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingComments && !isLastCommentPage && !TextUtils.isEmpty(nextCommentCursor)
                        && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                    loadMoreComments();
                }
            }
        });
    }

    private void loadComments() {
        if (isLoadingComments) return;
        isLoadingComments = true;
        commentsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getProductComments(Integer.parseInt(productId), COMMENT_PAGE_SIZE, null, new ApiResponseListener<CommentResponse>() {
            @Override
            public void onSuccess(CommentResponse response) {
                isLoadingComments = false;
                commentsProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    CommentResponse.CommentMetadata metadata = response.getMetadata().getMetadata();
                    List<Comment> newComments = metadata.getComments();
                    nextCommentCursor = metadata.getNextCursor();
                    isLastCommentPage = TextUtils.isEmpty(nextCommentCursor);
                    if (newComments != null) {
                        comments.clear();
                        comments.addAll(newComments);
                        commentAdapter.updateComments(comments);
                        Log.d(TAG, "Loaded " + newComments.size() + " comments");
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoadingComments = false;
                commentsProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load comments error: " + errorMessage);
                Toast.makeText(CommentActivity.this, "Lỗi tải bình luận: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreComments() {
        if (!isLoadingComments && !isLastCommentPage && !TextUtils.isEmpty(nextCommentCursor)) {
            isLoadingComments = true;
            commentsProgressBar.setVisibility(View.VISIBLE);
            apiManager.getProductComments(Integer.parseInt(productId), COMMENT_PAGE_SIZE, nextCommentCursor, new ApiResponseListener<CommentResponse>() {
                @Override
                public void onSuccess(CommentResponse response) {
                    isLoadingComments = false;
                    commentsProgressBar.setVisibility(View.GONE);
                    if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                        CommentResponse.CommentMetadata metadata = response.getMetadata().getMetadata();
                        List<Comment> newComments = metadata.getComments();
                        nextCommentCursor = metadata.getNextCursor();
                        isLastCommentPage = TextUtils.isEmpty(nextCommentCursor);
                        if (newComments != null) {
                            int startPosition = comments.size();
                            comments.addAll(newComments);
                            commentAdapter.notifyItemRangeInserted(startPosition, newComments.size());
                            Log.d(TAG, "Loaded " + newComments.size() + " more comments");
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    isLoadingComments = false;
                    commentsProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Load more comments error: " + errorMessage);
                    Toast.makeText(CommentActivity.this, "Lỗi tải thêm bình luận: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadReplies(Comment comment) {
        commentsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getCommentReplies(comment.getId(), new ApiResponseListener<RepliesResponse>() {
            @Override
            public void onSuccess(RepliesResponse response) {
                commentsProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<Comment> replies = response.getMetadata().getMetadata().getComments();
                    if (replies != null) {
                        comment.setReplies(replies);
                        int index = comments.indexOf(comment);
                        if (index != -1) {
                            commentAdapter.notifyItemChanged(index);
                        }
                        Log.d(TAG, "Loaded " + replies.size() + " replies for comment ID: " + comment.getId());
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                commentsProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load replies error: " + errorMessage);
                Toast.makeText(CommentActivity.this, "Lỗi tải trả lời: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCommentDialog(Comment comment, boolean isReply, boolean isEdit) {
        currentDialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_comment, null);
        currentDialog.setContentView(dialogView);

        EditText etCommentDialog = dialogView.findViewById(R.id.etComment);
        Button btnAddImageDialog = dialogView.findViewById(R.id.btnAddImage);
        LinearLayout llImagePreview = dialogView.findViewById(R.id.llImagePreviewContainer);
        RatingBar rbRatingDialog = dialogView.findViewById(R.id.rbRating);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSendDialog = dialogView.findViewById(R.id.btnSend);

        selectedImageUrisDialog.clear();
        llImagePreview.setVisibility(View.GONE);

        if (isEdit && comment != null) {
            etCommentDialog.setText(comment.getContent() != null ? comment.getContent() : "");
            long createdTime = parseDateTime(comment.getCreatedAt());
            long currentTime = System.currentTimeMillis();
            long diffHours = (currentTime - createdTime) / (1000 * 60 * 60);
            if (diffHours <= 24 && comment.getRating() != null) {
                rbRatingDialog.setRating(comment.getRating());
                rbRatingDialog.setVisibility(View.VISIBLE);
                rbRatingDialog.setIsIndicator(false);
            } else {
                rbRatingDialog.setVisibility(View.GONE);
            }
            if (comment.getImageUrls() != null && !comment.getImageUrls().isEmpty()) {
                updateImagePreview(llImagePreview, null, new ArrayList<>(comment.getImageUrls()), true);
                llImagePreview.setVisibility(View.VISIBLE);
            }
        } else {
            rbRatingDialog.setRating(0);
            rbRatingDialog.setVisibility(isReply ? View.GONE : View.VISIBLE);
            etCommentDialog.setText("");
            llImagePreview.setVisibility(View.GONE);
            if (isReply && comment != null) {
                etCommentDialog.setHint("Trả lời: " + (comment.getUser() != null ? comment.getUser().getName() : "Người dùng"));
            } else {
                etCommentDialog.setHint("Nhập bình luận...");
            }
        }

        btnAddImageDialog.setOnClickListener(v -> checkStoragePermission(REQUEST_CODE_SELECT_IMAGE_DIALOG, llImagePreview));

        btnCancel.setOnClickListener(v -> {
            selectedImageUrisDialog.clear();
            currentDialog.dismiss();
        });

        btnSendDialog.setOnClickListener(v -> {
            Log.d(TAG, "btnSendDialog clicked, selectedImageUrisDialog size: " + selectedImageUrisDialog.size());
            String content = etCommentDialog.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            float rating = rbRatingDialog.getVisibility() == View.VISIBLE ? rbRatingDialog.getRating() : 0;
            Integer finalRating = null;
            long localDiffHours = isEdit && comment != null ? (System.currentTimeMillis() - parseDateTime(comment.getCreatedAt())) / (1000 * 60 * 60) : 0;
            if (!isReply && (isEdit ? (localDiffHours <= 24 && comment.getRating() != null) : true) && rating > 0) {
                finalRating = (int) rating;
            } else if (isEdit && comment.getRating() != null) {
                finalRating = comment.getRating();
            }
            Integer parentId = isReply && comment != null ? comment.getId() : null; // Đảm bảo parentId chỉ khi reply
            List<String> existingImageUrls = isEdit && comment != null ? comment.getImageUrls() : new ArrayList<>();

            if (!selectedImageUrisDialog.isEmpty()) {
                Log.d(TAG, "Uploading images, uris: " + selectedImageUrisDialog);
                currentDialog.setCancelable(false);
                uploadImagesToCloudinary(content, finalRating, parentId, isEdit ? comment : null, existingImageUrls, llImagePreview, new ArrayList<>(selectedImageUrisDialog), currentDialog);
            } else {
                if (isEdit) {
                    updateComment(comment, content, finalRating, existingImageUrls);
                } else {
                    postComment(content, finalRating, parentId, existingImageUrls);
                }
                currentDialog.dismiss();
            }
        });

        currentDialog.setOnDismissListener(d -> {
            selectedImageUrisDialog.clear();
            tvReplyHint.setVisibility(View.GONE);
            replyingTo = null;
            rbRating.setVisibility(View.VISIBLE);
        });
        currentDialog.show();
    }

    private void updateImagePreview(LinearLayout llImagePreview, List<Uri> uris, List<String> urls, boolean isEdit) {
        if (llImagePreview == null) return;
        llImagePreview.removeAllViews();
        llImagePreview.setVisibility(uris != null && !uris.isEmpty() || (urls != null && !urls.isEmpty()) ? View.VISIBLE : View.GONE);

        List<Object> imagesToShow = new ArrayList<>();
        if (uris != null) imagesToShow.addAll(uris);
        if (urls != null) imagesToShow.addAll(urls);

        for (int i = 0; i < imagesToShow.size(); i++) {
            View mediaView = LayoutInflater.from(this).inflate(R.layout.item_media, llImagePreview, false);
            ImageView ivMedia = mediaView.findViewById(R.id.ivMedia);
            ImageButton btnRemove = mediaView.findViewById(R.id.btnRemove);
            Object image = imagesToShow.get(i);
            Glide.with(this).load(image instanceof Uri ? (Uri) image : (String) image).into(ivMedia);
            int finalI = i;
            btnRemove.setOnClickListener(v -> {
                if (finalI >= 0 && finalI < imagesToShow.size()) {
                    imagesToShow.remove(finalI);
                    if (image instanceof Uri) {
                        List<Uri> currentUris = llImagePreview == llImagePreviewContainer ? selectedImageUris : selectedImageUrisDialog;
                        if (finalI < currentUris.size()) currentUris.remove(finalI);
                    } else if (image instanceof String && urls != null) {
                        if (finalI < urls.size()) urls.remove(finalI);
                    }
                    updateImagePreview(llImagePreview, uris, urls, isEdit);
                }
            });
            llImagePreview.addView(mediaView);
        }
    }

    private void uploadImagesToCloudinary(String content, Integer rating, Integer parentId, Comment comment, List<String> existingImageUrls, LinearLayout llImagePreview, List<Uri> uploadUris, BottomSheetDialog dialog) {
        commentsProgressBar.setVisibility(View.VISIBLE);
        List<String> uploadedImageUrls = new ArrayList<>();
        final int totalImages = uploadUris.size();

        Log.d(TAG, "Starting upload for " + totalImages + " images: " + uploadUris);

        if (totalImages == 0) {
            commentsProgressBar.setVisibility(View.GONE);
            if (comment != null) {
                updateComment(comment, content, rating != null ? rating : 0, existingImageUrls);
            } else {
                postComment(content, rating, parentId, existingImageUrls);
            }
            if (dialog != null) dialog.dismiss();
            return;
        }

        // Xử lý upload từng ảnh
        for (Uri uri : new ArrayList<>(uploadUris)) {
            try {
                MediaManager.get().upload(uri)
                        .unsigned("android_unsigned")
                        .option("folder", "shopman/comments")
                        .option("public_id", "comment_" + System.currentTimeMillis() + "_" + uri.getLastPathSegment())
                        .callback(new com.cloudinary.android.callback.UploadCallback() {
                            @Override
                            public void onStart(String requestId) {
                                Log.d(TAG, "Upload started for Uri: " + uri + ", Request ID: " + requestId);
                            }

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {
                                float progress = (bytes * 100f) / totalBytes;
                                Log.d(TAG, "Upload progress for " + requestId + ": " + progress + "%");
                            }

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                String secureUrl = (String) resultData.get("secure_url");
                                if (secureUrl != null) {
                                    uploadedImageUrls.add(secureUrl);
                                    Log.d(TAG, "Upload success for " + requestId + ", URL: " + secureUrl + ", Total uploaded: " + uploadedImageUrls.size());
                                    if (uploadedImageUrls.size() == totalImages) {
                                        handleUploadCompletion(content, rating, parentId, comment, existingImageUrls, uploadedImageUrls, llImagePreview, dialog);
                                    }
                                }
                            }

                            @Override
                            public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                                Log.e(TAG, "Upload error for " + requestId + ": " + error.getDescription());
                                uploadedImageUrls.add(null); // Đánh dấu lỗi
                                if (uploadedImageUrls.size() == totalImages) {
                                    handleUploadCompletion(content, rating, parentId, comment, existingImageUrls, uploadedImageUrls, llImagePreview, dialog);
                                }
                            }

                            @Override
                            public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                                Log.w(TAG, "Upload rescheduled for " + requestId + ": " + error.getDescription());
                            }
                        })
                        .dispatch();
            } catch (Exception e) {
                Log.e(TAG, "Error during upload dispatch for Uri: " + uri + ": " + e.getMessage(), e);
                uploadedImageUrls.add(null); // Đánh dấu lỗi
                if (uploadedImageUrls.size() == totalImages) {
                    handleUploadCompletion(content, rating, parentId, comment, existingImageUrls, uploadedImageUrls, llImagePreview, dialog);
                }
            }
        }
    }

    private void handleUploadCompletion(String content, Integer rating, Integer parentId, Comment comment, List<String> existingImageUrls, List<String> uploadedImageUrls, LinearLayout llImagePreview, BottomSheetDialog dialog) {
        runOnUiThread(() -> {
            List<String> finalUrls = new ArrayList<>(existingImageUrls);
            for (String url : uploadedImageUrls) {
                if (url != null) finalUrls.add(url); // Chỉ thêm URL thành công
            }

            commentsProgressBar.setVisibility(View.GONE);
            if (comment != null) {
                updateComment(comment, content, rating != null ? rating : 0, finalUrls);
            } else {
                postComment(content, rating, parentId, finalUrls);
            }

            if (llImagePreview != null) {
                llImagePreview.removeAllViews();
                updateImagePreview(llImagePreview, null, finalUrls, false);
                Log.d(TAG, "Preview updated with " + finalUrls.size() + " URLs");
            }

            if (dialog != null) {
                dialog.dismiss();
            }

            if (uploadedImageUrls.contains(null)) {
                Toast.makeText(CommentActivity.this, "Một số ảnh không thể upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment(String content, Integer rating, Integer parentId, List<String> imageUrls) {
        commentsProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Calling postComment with content: " + content + ", rating: " + rating + ", parentId: " + parentId + ", imageUrls: " + imageUrls);
        try {
            int productIdInt = Integer.parseInt(productId);
            apiManager.postComment(productIdInt, content, rating, parentId, imageUrls, new ApiResponseListener<Comment>() {
                @Override
                public void onSuccess(Comment comment) {
                    runOnUiThread(() -> {
                        commentsProgressBar.setVisibility(View.GONE);
                        int scrollPosition = -1;
                        if (parentId != null) {
                            for (int i = 0; i < comments.size(); i++) {
                                Comment c = comments.get(i);
                                if (c.getId() == parentId) {
                                    List<Comment> replies = c.getReplies();
                                    if (replies == null) {
                                        replies = new ArrayList<>();
                                        c.setReplies(replies);
                                    }
                                    replies.add(comment);
                                    commentAdapter.notifyItemChanged(i); // Cập nhật parent để hiển thị reply
                                    scrollPosition = i;
                                    break;
                                }
                            }
                        } else {
                            comments.add(0, comment);
                            commentAdapter.addComment(comment);
                            scrollPosition = 0;
                        }
                        if (scrollPosition != -1) {
                            commentsRecyclerView.smoothScrollToPosition(scrollPosition);
                        }
                        Toast.makeText(CommentActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        commentsProgressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Post comment error: " + errorMessage);
                        Toast.makeText(CommentActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (NumberFormatException e) {
            runOnUiThread(() -> {
                commentsProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Invalid productId: " + productId, e);
                Toast.makeText(CommentActivity.this, "Lỗi: productId không hợp lệ", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateComment(Comment comment, String content, Integer rating, List<String> imageUrls) {
        commentsProgressBar.setVisibility(View.VISIBLE);
        apiManager.updateComment(comment.getId(), content, rating, imageUrls, new ApiResponseListener<Comment>() {
            @Override
            public void onSuccess(Comment updatedComment) {
                runOnUiThread(() -> {
                    commentsProgressBar.setVisibility(View.GONE);
                    // Kiểm tra xem comment có phải là reply không
                    Integer parentId = updatedComment.getParentId(); // Giả sử Comment model có getParentId()
                    if (parentId != null) {
                        // Đây là reply, tìm comment gốc
                        for (int i = 0; i < comments.size(); i++) {
                            Comment parentComment = comments.get(i);
                            if (parentComment.getId() == parentId) {
                                // Cập nhật reply trong danh sách replies của comment gốc
                                List<Comment> replies = parentComment.getReplies();
                                if (replies != null) {
                                    for (int j = 0; j < replies.size(); j++) {
                                        if (replies.get(j).getId() == updatedComment.getId()) {
                                            replies.set(j, updatedComment);
                                            break;
                                        }
                                    }
                                } else {
                                    replies = new ArrayList<>();
                                    replies.add(updatedComment);
                                    parentComment.setReplies(replies);
                                }
                                commentAdapter.notifyItemChanged(i); // Render lại comment gốc
                                commentsRecyclerView.smoothScrollToPosition(i); // Scroll đến comment gốc
                                break;
                            }
                        }
                    } else {
                        // Đây là comment gốc
                        int index = comments.indexOf(comment);
                        if (index != -1) {
                            comments.set(index, updatedComment);
                            commentAdapter.updateComment(updatedComment);
                            commentsRecyclerView.smoothScrollToPosition(index);
                        } else {
                            comments.add(updatedComment);
                            commentAdapter.addComment(updatedComment);
                            commentsRecyclerView.smoothScrollToPosition(comments.size() - 1);
                        }
                    }
                    Toast.makeText(CommentActivity.this, "Cập nhật bình luận thành công", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    commentsProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Update comment error: " + errorMessage);
                    Toast.makeText(CommentActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void deleteComment(Comment comment) {
        commentsProgressBar.setVisibility(View.VISIBLE);
        apiManager.deleteComment(comment.getId(), new ApiResponseListener<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                commentsProgressBar.setVisibility(View.GONE);
                int index = comments.indexOf(comment);
                if (index != -1) {
                    comments.remove(index);
                    commentAdapter.notifyItemRemoved(index);
                    Toast.makeText(CommentActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                commentsProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete comment error: " + errorMessage);
                Toast.makeText(CommentActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long parseDateTime(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            return sdf.parse(dateTime).getTime();
        } catch (ParseException e) {
            Log.e(TAG, "Parse date error: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}