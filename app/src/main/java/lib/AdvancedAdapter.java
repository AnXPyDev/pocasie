package lib;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class AdvancedAdapter<T> extends RecyclerView.Adapter<AdvancedAdapter.ViewHolder<T>> {
    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bind(T object);

        protected void recycle() {};

        public interface Factory {
            ViewHolder create(ViewGroup parent);
        }
    }


    protected List<T> source;
    protected ViewHolder.Factory viewHolderFactory;

    public void setSource(List<T> source) {
        this.source = source;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolderFactory.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(source.get(position));
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    public AdvancedAdapter(List<T> source, ViewHolder.Factory viewHolderFactory) {
        this.source = source;
        this.viewHolderFactory = viewHolderFactory;
    }

}
