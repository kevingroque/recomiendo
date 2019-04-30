package com.roque.app.recomiendo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.models.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    public List<Comment> commentsList;
    public Context mContext;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;

    public CommentsAdapter(List<Comment> commentsList){
        this.commentsList = commentsList;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments, parent, false);
        mContext = parent.getContext();
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getComment();
        holder.setComment_message(commentMessage);

        String user_id = commentsList.get(position).getUserId();

        mFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String lastName = task.getResult().getString("lastname");
                    String userImage = task.getResult().getString("avatar");
                    holder.setUserData(userName, lastName, userImage);
                } else {
                    //Firebase Exception
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(commentsList != null) {
            return commentsList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mMensajeText, mNombreUserText;
        private CircleImageView mImageUser;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setComment_message(String message){

            mMensajeText = mView.findViewById(R.id.comment_txt_message);
            mMensajeText.setText(message);
        }

        public void setUserData(String name, String lastname, String image){
            mNombreUserText = mView.findViewById(R.id.comment_txt_username);
            mImageUser = mView.findViewById(R.id.comment_img_avatar);
            mNombreUserText.setText(name + " " + lastname);
            Glide.with(mContext).load(image).into(mImageUser);
        }
    }
}
