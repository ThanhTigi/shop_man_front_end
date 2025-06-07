package com.example.shopman.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.models.Comments.Comment;
import com.example.shopman.utilitis.MyPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private OnCommentActionListener actionListener;
    private int currentUserId; // ID của người dùng hiện tại

    public CommentAdapter(Context context, List<Comment> commentList, int currentUserId) {
        this.context = context;
        this.commentList = commentList;
        this.currentUserId = currentUserId;
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Hiển thị thông tin người dùng
        if (comment.getUser() != null) {
            holder.tvUserName.setText(comment.getUser().getName());
            if (comment.getUser().getAvatar() != null && !comment.getUser().getAvatar().isEmpty()) {
                Glide.with(context)
                        .load(comment.getUser().getAvatar())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .into(holder.ivUserAvatar);
            } else {
                holder.ivUserAvatar.setImageResource(R.drawable.ic_placeholder);
            }
        }

        // Hiển thị rating nếu có
        if (comment.getRating() != null) {
            holder.ratingBarComment.setVisibility(View.VISIBLE);
            holder.ratingBarComment.setRating(comment.getRating());
        } else {
            holder.ratingBarComment.setVisibility(View.GONE);
        }

        // Hiển thị nội dung comment
        holder.tvCommentContent.setText(comment.getContent());

        // Hiển thị thời gian
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            long time = sdf.parse(comment.getCreatedAt()).getTime();
            holder.tvCommentTime.setText(DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        } catch (ParseException e) {
            holder.tvCommentTime.setText(comment.getCreatedAt());
        }

        // Hiển thị nút chỉnh sửa/xóa nếu comment thuộc về người dùng hiện tại
        if (comment.getUserId() == currentUserId) {
            holder.tvEdit.setVisibility(View.VISIBLE);
            holder.tvDelete.setVisibility(View.VISIBLE);
        } else {
            holder.tvEdit.setVisibility(View.GONE);
            holder.tvDelete.setVisibility(View.GONE);
        }

        // Sự kiện nút trả lời
        holder.tvReply.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onReplyClicked(comment);
            }
        });

        // Sự kiện nút chỉnh sửa
        holder.tvEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClicked(comment);
            }
        });

        // Sự kiện nút xóa
        holder.tvDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClicked(comment);
            }
        });

        // Hiển thị replies
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            holder.tvViewReplies.setVisibility(View.VISIBLE);
            holder.tvViewReplies.setText("Xem " + comment.getReplies().size() + " trả lời");
            holder.rvReplies.setVisibility(View.VISIBLE);
            CommentAdapter repliesAdapter = new CommentAdapter(context, comment.getReplies(), currentUserId);
            repliesAdapter.setOnCommentActionListener(actionListener);
            holder.rvReplies.setLayoutManager(new LinearLayoutManager(context));
            holder.rvReplies.setAdapter(repliesAdapter);
        } else {
            holder.tvViewReplies.setVisibility(View.VISIBLE);
            holder.tvViewReplies.setText("Xem thêm trả lời");
            holder.rvReplies.setVisibility(View.GONE);
        }

        // Sự kiện xem thêm replies
        holder.tvViewReplies.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onViewRepliesClicked(comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName;
        RatingBar ratingBarComment;
        TextView tvCommentContent;
        TextView tvCommentTime;
        TextView tvReply;
        TextView tvEdit;
        TextView tvDelete;
        TextView tvViewReplies;
        RecyclerView rvReplies;

        CommentViewHolder(View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ratingBarComment = itemView.findViewById(R.id.ratingBarComment);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            tvViewReplies = itemView.findViewById(R.id.tvViewReplies);
            rvReplies = itemView.findViewById(R.id.rvReplies);
        }
    }

    public interface OnCommentActionListener {
        void onReplyClicked(Comment comment);
        void onEditClicked(Comment comment);
        void onDeleteClicked(Comment comment);
        void onViewRepliesClicked(Comment comment);
    }
}