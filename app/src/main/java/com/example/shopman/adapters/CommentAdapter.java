package com.example.shopman.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.activities.CommentActivity;
import com.example.shopman.models.Comments.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private static final String TAG = "CommentAdapter";
    private final Context context;
    private List<Comment> comments;
    private final OnCommentActionListener listener;
    private int maxComments = Integer.MAX_VALUE;

    public interface OnCommentActionListener {
        void onReplyComment(Comment comment);
        void onEditComment(Comment comment);
        void onDeleteComment(Comment comment);
        void onReportComment(Comment comment);
    }

    public CommentAdapter(Context context, List<Comment> comments, OnCommentActionListener listener) {
        this.context = context;
        this.comments = new ArrayList<>(comments != null ? comments : new ArrayList<>());
        this.listener = listener;
        Log.d(TAG, "Initialized with comments size: " + this.comments.size());
    }

    public void setMaxComments(int max) {
        this.maxComments = max;
        updateComments(comments); // Cập nhật lại với DiffUtil khi thay đổi maxComments
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        Log.d(TAG, "Binding comment at position " + position + ": " + (comment.getContent() != null ? comment.getContent() : "null"));
        holder.bindComment(comment, 0); // Level 0 for root comments
    }

    @Override
    public int getItemCount() {
        int count = Math.min(comments.size(), maxComments);
        Log.d(TAG, "Item count: " + count + " (from " + comments.size() + " total)");
        return count;
    }

    public void addComment(Comment comment) {
        if (comment != null) {
            List<Comment> oldList = new ArrayList<>(comments);
            comments.add(0, comment);
            CommentDiffCallback diffCallback = new CommentDiffCallback(oldList, comments);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            diffResult.dispatchUpdatesTo(this);
            Log.d(TAG, "Added comment with id: " + comment.getId());
        }
    }

    public void updateComment(Comment updatedComment) {
        if (updatedComment != null) {
            int index = -1;
            for (int i = 0; i < comments.size(); i++) {
                if (comments.get(i).getId() == updatedComment.getId()) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                List<Comment> oldList = new ArrayList<>(comments);
                comments.set(index, updatedComment);
                CommentDiffCallback diffCallback = new CommentDiffCallback(oldList, comments);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
                diffResult.dispatchUpdatesTo(this);
                Log.d(TAG, "Updated comment with id: " + updatedComment.getId() + " at position " + index);
            }
        }
    }

    public void updateComments(List<Comment> newComments) {
        if (newComments == null) {
            Log.e(TAG, "updateComments: newComments is null");
            return;
        }
        Set<Integer> seenIds = new HashSet<>();
        List<Comment> filteredComments = new ArrayList<>();
        for (Comment c : newComments) {
            if (c != null && c.getId() != 0 && seenIds.add(c.getId())) {
                filteredComments.add(c);
            }
        }
        CommentDiffCallback diffCallback = new CommentDiffCallback(comments, filteredComments);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        comments.clear();
        comments.addAll(filteredComments);
        diffResult.dispatchUpdatesTo(this);
        Log.d(TAG, "Updated comments list size: " + comments.size());
    }

    public void removeComment(Comment comment) {
        if (comment != null) {
            int index = comments.indexOf(comment);
            if (index != -1) {
                List<Comment> oldList = new ArrayList<>(comments);
                comments.remove(index);
                CommentDiffCallback diffCallback = new CommentDiffCallback(oldList, comments);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
                diffResult.dispatchUpdatesTo(this);
                Log.d(TAG, "Removed comment with id: " + comment.getId() + " at position " + index);
            }
        }
    }

    private static class CommentDiffCallback extends DiffUtil.Callback {
        private final List<Comment> oldList;
        private final List<Comment> newList;

        CommentDiffCallback(List<Comment> oldList, List<Comment> newList) {
            this.oldList = new ArrayList<>(oldList != null ? oldList : new ArrayList<>());
            this.newList = new ArrayList<>(newList != null ? newList : new ArrayList<>());
            Log.d("CommentDiffCallback", "Diff callback initialized: old size " + oldList.size() + ", new size " + newList.size());
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivUserAvatar;
        private final TextView tvUserName, tvCommentContent, tvCommentTime, tvReply, tvReplyIndicator;
        private final RatingBar ratingBarComment;
        private final RecyclerView rvMedia, rvReplies;
        private final LinearLayout llRepliesContainer, llReplyControls;
        private final ImageButton ibMoreOptions;
        private final View replyLine;
        private final MediaAdapter mediaAdapter;
        private final CommentAdapter replyAdapter;

        CommentViewHolder(View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvReplyIndicator = itemView.findViewById(R.id.tvReplyIndicator);
            ratingBarComment = itemView.findViewById(R.id.ratingBarComment);
            rvMedia = itemView.findViewById(R.id.rvMedia);
            rvReplies = itemView.findViewById(R.id.rvReplies);
            llRepliesContainer = itemView.findViewById(R.id.llRepliesContainer);
            llReplyControls = itemView.findViewById(R.id.llReplyControls);
            ibMoreOptions = itemView.findViewById(R.id.ibMoreOptions);
            replyLine = itemView.findViewById(R.id.replyLine);

            rvMedia.setLayoutManager(new GridLayoutManager(context, 3)); // Max 3 thumbnails
            mediaAdapter = new MediaAdapter(context);
            rvMedia.setAdapter(mediaAdapter);

            rvReplies.setLayoutManager(new LinearLayoutManager(context));
            replyAdapter = new CommentAdapter(context, new ArrayList<>(), listener);
            rvReplies.setAdapter(replyAdapter);
        }

        void bindComment(Comment comment, int level) {
            if (comment == null) {
                Log.w(TAG, "bindComment: Comment is null, skipping");
                return;
            }
            Log.d(TAG, "Binding comment: " + (comment.getContent() != null ? comment.getContent() : "null") + " at level " + level);

            // Load avatar
            String avatarUrl = comment.getUser() != null ? comment.getUser().getAvatar() : null;
            Glide.with(context).load(avatarUrl != null ? avatarUrl : R.drawable.ic_menu_user).into(ivUserAvatar);

            // Set username
            tvUserName.setText(comment.getUser() != null ? comment.getUser().getName() : "Anonymous");

            // Set rating
            if (comment.getRating() != null && comment.getRating() > 0) {
                ratingBarComment.setRating(comment.getRating());
                ratingBarComment.setVisibility(View.VISIBLE);
            } else {
                ratingBarComment.setVisibility(View.GONE);
            }

            // Set content and time
            tvCommentContent.setText(comment.getContent() != null ? comment.getContent() : "");
            tvCommentTime.setText(getRelativeTime(comment.getCreatedAt()));

            // Handle media
            if (comment.getImageUrls() != null && !comment.getImageUrls().isEmpty()) {
                mediaAdapter.setMediaItems(comment.getImageUrls());
                rvMedia.setVisibility(View.VISIBLE);
            } else {
                rvMedia.setVisibility(View.GONE);
            }

            // Handle reply controls
            if (listener != null) {
                llReplyControls.setVisibility(View.VISIBLE);
                tvReply.setVisibility(View.VISIBLE);
                tvReply.setOnClickListener(v -> listener.onReplyComment(comment));
            } else {
                llReplyControls.setVisibility(View.GONE);
            }

            // Handle reply indicator
            boolean hasReplies = comment.getRight() - comment.getLeft() > 1;
            if (hasReplies && listener != null) {
                int replyCount = (comment.getRight() - comment.getLeft() - 1) / 2;
                tvReplyIndicator.setText("Xem " + replyCount + " trả lời");
                tvReplyIndicator.setVisibility(View.VISIBLE);
                tvReplyIndicator.setOnClickListener(v -> {
                    if (context instanceof CommentActivity) {
                        ((CommentActivity) context).loadReplies(comment);
                    }
                });
            } else {
                tvReplyIndicator.setVisibility(View.GONE);
            }

            // Handle replies
            List<Comment> replies = comment.getReplies();
            if (replies != null && !replies.isEmpty()) {
                replyAdapter.updateComments(replies);
                rvReplies.setVisibility(View.VISIBLE);
                llRepliesContainer.setVisibility(View.VISIBLE);
                tvReplyIndicator.setVisibility(View.GONE);
            } else {
                rvReplies.setVisibility(View.GONE);
                llRepliesContainer.setVisibility(View.GONE);
            }

            // Handle more options
            ibMoreOptions.setVisibility(listener != null ? View.VISIBLE : View.GONE);
            if (listener != null) {
                ibMoreOptions.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(context, ibMoreOptions);
                    if (comment.isEditable()) popup.getMenu().add(Menu.NONE, 1, 1, "Sửa");
                    if (comment.isDeletable()) popup.getMenu().add(Menu.NONE, 2, 2, "Xóa");
                    if (!comment.isEditable() && !comment.isDeletable()) popup.getMenu().add(Menu.NONE, 3, 3, "Report");
                    popup.setOnMenuItemClickListener(item -> {
                        int id = item.getItemId();
                        if (id == 1) listener.onEditComment(comment);
                        else if (id == 2) listener.onDeleteComment(comment);
                        else if (id == 3) listener.onReportComment(comment);
                        return true;
                    });
                    popup.show();
                });
            }

            // Set reply line for nested comments
            replyLine.setVisibility(level > 0 ? View.VISIBLE : View.GONE);
            if (level > 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) replyLine.getLayoutParams();
                params.setMarginStart(20 + (level - 1) * 16);
                replyLine.setLayoutParams(params);
            }

            // Adjust margin for nested comments
            LinearLayout llMainComment = itemView.findViewById(R.id.llMainComment);
            if (llMainComment != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) llMainComment.getLayoutParams();
                params.setMarginStart(level * 16);
                llMainComment.setLayoutParams(params);
            }
        }

        private String getRelativeTime(String createdAt) {
            if (createdAt == null || createdAt.isEmpty()) return "Không xác định";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                long time = sdf.parse(createdAt).getTime();
                return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
            } catch (ParseException e) {
                return "Không xác định";
            }
        }
    }
}