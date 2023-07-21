// Generated by view binder compiler. Do not edit!
package com.example.fastchat.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.fastchat.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityChatBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView backImg;

  @NonNull
  public final ImageView call;

  @NonNull
  public final CardView cardView2;

  @NonNull
  public final CardView cardView3;

  @NonNull
  public final ImageView clip;

  @NonNull
  public final ConstraintLayout cont;

  @NonNull
  public final ImageView imageView6;

  @NonNull
  public final LinearLayout linearLayout;

  @NonNull
  public final LinearLayout linearLayout4;

  @NonNull
  public final EditText messageEdt;

  @NonNull
  public final ImageView more;

  @NonNull
  public final ImageView profileImage;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final ImageView send;

  @NonNull
  public final TextView status;

  @NonNull
  public final TextView userNameTxt;

  private ActivityChatBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView backImg,
      @NonNull ImageView call, @NonNull CardView cardView2, @NonNull CardView cardView3,
      @NonNull ImageView clip, @NonNull ConstraintLayout cont, @NonNull ImageView imageView6,
      @NonNull LinearLayout linearLayout, @NonNull LinearLayout linearLayout4,
      @NonNull EditText messageEdt, @NonNull ImageView more, @NonNull ImageView profileImage,
      @NonNull RecyclerView recyclerView, @NonNull ImageView send, @NonNull TextView status,
      @NonNull TextView userNameTxt) {
    this.rootView = rootView;
    this.backImg = backImg;
    this.call = call;
    this.cardView2 = cardView2;
    this.cardView3 = cardView3;
    this.clip = clip;
    this.cont = cont;
    this.imageView6 = imageView6;
    this.linearLayout = linearLayout;
    this.linearLayout4 = linearLayout4;
    this.messageEdt = messageEdt;
    this.more = more;
    this.profileImage = profileImage;
    this.recyclerView = recyclerView;
    this.send = send;
    this.status = status;
    this.userNameTxt = userNameTxt;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityChatBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityChatBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_chat, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityChatBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.backImg;
      ImageView backImg = ViewBindings.findChildViewById(rootView, id);
      if (backImg == null) {
        break missingId;
      }

      id = R.id.call;
      ImageView call = ViewBindings.findChildViewById(rootView, id);
      if (call == null) {
        break missingId;
      }

      id = R.id.cardView2;
      CardView cardView2 = ViewBindings.findChildViewById(rootView, id);
      if (cardView2 == null) {
        break missingId;
      }

      id = R.id.cardView3;
      CardView cardView3 = ViewBindings.findChildViewById(rootView, id);
      if (cardView3 == null) {
        break missingId;
      }

      id = R.id.clip;
      ImageView clip = ViewBindings.findChildViewById(rootView, id);
      if (clip == null) {
        break missingId;
      }

      ConstraintLayout cont = (ConstraintLayout) rootView;

      id = R.id.imageView6;
      ImageView imageView6 = ViewBindings.findChildViewById(rootView, id);
      if (imageView6 == null) {
        break missingId;
      }

      id = R.id.linearLayout;
      LinearLayout linearLayout = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout == null) {
        break missingId;
      }

      id = R.id.linearLayout4;
      LinearLayout linearLayout4 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout4 == null) {
        break missingId;
      }

      id = R.id.messageEdt;
      EditText messageEdt = ViewBindings.findChildViewById(rootView, id);
      if (messageEdt == null) {
        break missingId;
      }

      id = R.id.more;
      ImageView more = ViewBindings.findChildViewById(rootView, id);
      if (more == null) {
        break missingId;
      }

      id = R.id.profileImage;
      ImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.send;
      ImageView send = ViewBindings.findChildViewById(rootView, id);
      if (send == null) {
        break missingId;
      }

      id = R.id.status;
      TextView status = ViewBindings.findChildViewById(rootView, id);
      if (status == null) {
        break missingId;
      }

      id = R.id.userNameTxt;
      TextView userNameTxt = ViewBindings.findChildViewById(rootView, id);
      if (userNameTxt == null) {
        break missingId;
      }

      return new ActivityChatBinding((ConstraintLayout) rootView, backImg, call, cardView2,
          cardView3, clip, cont, imageView6, linearLayout, linearLayout4, messageEdt, more,
          profileImage, recyclerView, send, status, userNameTxt);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
