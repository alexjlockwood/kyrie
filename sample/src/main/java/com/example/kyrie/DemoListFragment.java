package com.example.kyrie;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DemoListFragment extends Fragment {

  private static final Demo[] DEMOS = {
    new Demo("Polygons", PolygonsFragment.class),
    new Demo("Progress bars", ProgressFragment.class),
    new Demo("Path morphing", PathMorphFragment.class),
    new Demo("Heartbreak", HeartbreakFragment.class),
  };

  private Callbacks callbacks;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (!(context instanceof Callbacks)) {
      throw new IllegalArgumentException("Host must implement Callbacks interface");
    }
    callbacks = (Callbacks) context;
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_demo_list, container, false);
    final RecyclerView recyclerView = view.findViewById(R.id.demo_list);
    recyclerView.setAdapter(new Adapter());
    return view;
  }

  private class Adapter extends RecyclerView.Adapter<ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new ViewHolder(inflater.inflate(R.layout.fragment_demo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.bind(DEMOS[position]);
    }

    @Override
    public int getItemCount() {
      return DEMOS.length;
    }
  }

  private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView textView;
    private Demo demo;

    public ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      textView = itemView.findViewById(R.id.demo_text);
    }

    public void bind(Demo d) {
      demo = d;
      textView.setText(d.title);
    }

    @Override
    public void onClick(View v) {
      callbacks.onListItemClick(demo);
    }
  }

  public static class Demo {
    public final String title;
    public final String fragmentName;

    public Demo(String title, Class<?> fragmentClass) {
      this.title = title;
      this.fragmentName = fragmentClass.getName();
    }
  }

  public interface Callbacks {
    void onListItemClick(@NonNull Demo demo);
  }
}
