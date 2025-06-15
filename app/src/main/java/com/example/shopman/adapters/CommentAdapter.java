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
        this.comments = comments != null ? new ArrayList<>(comments) : new ArrayList<>();
        this.listener = listener;
        Log.d(TAG, "Constructor: Initial comments size=" + this.comments.size());
    }

    public void setMaxComments(int max) {
        this.maxComments = max;
        notifyDataSetChanged();
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
        Log.d(TAG, "onBindViewHolder: position=" + position + ", content=" + (comment.getContent() != null ? comment.getContent() : "null"));
        holder.bindComment(comment, 0); // Level 0 cho comment gốc
    }

    @Override
    public int getItemCount() {
        int count = Math.min(comments.size(), maxComments);
        Log.d(TAG, "getItemCount: comments size=" + comments.size() + ", count=" + count);
        return count;
    }

    public void addComment(Comment comment) {
        if (comment != null) {
            comments.add(0, comment);
            notifyItemInserted(0);
            Log.d(TAG, "addComment: Added comment with id=" + comment.getId() + ", new size=" + comments.size());
        }
    }

    public void updateComment(Comment updatedComment) {
        if (updatedComment != null) {
            for (int i = 0; i < comments.size(); i++) {
                if (comments.get(i).getId() == updatedComment.getId()) {
                    comments.set(i, updatedComment);
                    notifyItemChanged(i);
                    Log.d(TAG, "updateComment: Updated comment with id=" + updatedComment.getId() + " at position " + i);
                    break;
                }
            }
        }
    }

    public void updateComments(List<Comment> newComments) {
        if (newComments == null) {
            Log.e(TAG, "updateComments: newComments is null");
            return;
        }
        // Lọc trùng lặp dựa trên ID
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
        Log.d(TAG, "updateComments: Updated comments size=" + comments.size());
    }

    private static class CommentDiffCallback extends DiffUtil.Callback {
        private final List<Comment> oldList;
        private final List<Comment> newList;

        CommentDiffCallback(List<Comment> oldList, List<Comment> newList) {
            this.oldList = oldList != null ? new ArrayList<>(oldList) : new ArrayList<>();
            this.newList = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
            Log.d("CommentDiffCallback", "Constructor: oldList size=" + oldList.size() + ", newList size=" + newList.size());
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
        private final RatingBar rbCommentRating;
        private final RecyclerView rvMedia, rvReplies;
        private final LinearLayout llRepliesContainer;
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
            rbCommentRating = itemView.findViewById(R.id.ratingBarComment);
            rvMedia = itemView.findViewById(R.id.rvMedia);
            rvReplies = itemView.findViewById(R.id.rvReplies);
            llRepliesContainer = itemView.findViewById(R.id.llRepliesContainer);
            ibMoreOptions = itemView.findViewById(R.id.ibMoreOptions);
            replyLine = itemView.findViewById(R.id.replyLine);

            rvMedia.setLayoutManager(new GridLayoutManager(context, 2));
            mediaAdapter = new MediaAdapter(context);
            rvMedia.setAdapter(mediaAdapter);

            rvReplies.setLayoutManager(new LinearLayoutManager(context));
            replyAdapter = new CommentAdapter(context, new ArrayList<>(), listener);
            rvReplies.setAdapter(replyAdapter);
        }

        void bindComment(Comment comment, int level) {
            if (comment == null) {
                Log.w(TAG, "bindComment: Comment is null, skipping bind");
                return;
            }
            Log.d(TAG, "bindComment: comment=" + (comment.getContent() != null ? comment.getContent() : "null") + ", level=" + level);

            // Avatar
            if (comment.getUser() != null && comment.getUser().getAvatar() != null) {
                Glide.with(context).load(comment.getUser().getAvatar()).into(ivUserAvatar);
            } else {
                ivUserAvatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_user));
            }

            // User name
            tvUserName.setText(comment.getUser() != null ? comment.getUser().getName() : "Người dùng ẩn danh");

            // Content
            tvCommentContent.setText(comment.getContent() != null ? comment.getContent() : "Nội dung rỗng");

            // Rating
            rbCommentRating.setVisibility(comment.getRating() != null && comment.getRating() > 0 ? View.VISIBLE : View.GONE);
            if (comment.getRating() != null) rbCommentRating.setRating(comment.getRating());

            // Time
            tvCommentTime.setText(getRelativeTime(comment.getCreatedAt()));

            // Media
            if (comment.getImageUrls() != null && !comment.getImageUrls().isEmpty()) {
                mediaAdapter.setMediaItems(comment.getImageUrls());
                rvMedia.setVisibility(View.VISIBLE);
            } else {
                rvMedia.setVisibility(View.GONE);
            }

            // Replies
            List<Comment> replies = comment.getReplies();
            if (replies != null && !replies.isEmpty()) {
                replyAdapter.updateComments(replies); // Cập nhật replies với level 1
                rvReplies.setVisibility(View.VISIBLE);
                llRepliesContainer.setVisibility(View.VISIBLE);
                tvReplyIndicator.setVisibility(View.GONE); // Ẩn khi replies đã load
            } else {
                boolean hasReplies = comment.getRight() - comment.getLeft() > 1;
                if (hasReplies) {
                    int replyCount = (comment.getRight() - comment.getLeft() - 1) / 2;
                    tvReplyIndicator.setText("Xem " + replyCount + " trả lời");
                    tvReplyIndicator.setVisibility(listener != null ? View.VISIBLE : View.GONE);
                    tvReplyIndicator.setOnClickListener(v -> {
                        if (context instanceof CommentActivity && listener != null) {
                            ((CommentActivity) context).loadReplies(comment);
                        }
                    });
                } else {
                    tvReplyIndicator.setVisibility(View.GONE);
                }
                rvReplies.setVisibility(View.GONE);
                llRepliesContainer.setVisibility(View.GONE);
            }

            // Reply line
            replyLine.setVisibility(level > 0 ? View.VISIBLE : View.GONE);
            if (level > 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) replyLine.getLayoutParams();
                params.setMarginStart(20 + (level - 1) * 16);
                replyLine.setLayoutParams(params);
            }

            // Reply button
            tvReply.setVisibility(listener != null ? View.VISIBLE : View.GONE);
            if (listener != null) {
                tvReply.setOnClickListener(v -> listener.onReplyComment(comment));
            }

            // More options
            ibMoreOptions.setVisibility(listener != null ? View.VISIBLE : View.GONE);
            if (listener != null) {
                ibMoreOptions.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(context, ibMoreOptions);
                    if (comment.isEditable()) popup.getMenu().add(Menu.NONE, 1, 1, "Sửa");
                    if (comment.isDeletable()) popup.getMenu().add(Menu.NONE, 2, 2, "Xóa");
                    if (!comment.isEditable() && !comment.isDeletable()) popup.getMenu().add(Menu.NONE, 3, 3, "Report");
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == 1) listener.onEditComment(comment);
                        else if (item.getItemId() == 2) listener.onDeleteComment(comment);
                        else if (item.getItemId() == 3) listener.onReportComment(comment);
                        return true;
                    });
                    popup.show();
                });
            }

            // Margin for nested comments
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