// Generated by view binder compiler. Do not edit!
package com.example.fastchat.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.fastchat.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentFragImageThreeBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final ImageView imageView3;

  private FragmentFragImageThreeBinding(@NonNull FrameLayout rootView,
      @NonNull ImageView imageView3) {
    this.rootView = rootView;
    this.imageView3 = imageView3;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentFragImageThreeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentFragImageThreeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_frag_image_three, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentFragImageThreeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imageView3;
      ImageView imageView3 = ViewBindings.findChildViewById(rootView, id);
      if (imageView3 == null) {
        break missingId;
      }

      return new FragmentFragImageThreeBinding((FrameLayout) rootView, imageView3);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
