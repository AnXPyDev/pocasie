package lib;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

public abstract class ViewBindingHolder<T> extends AdvancedAdapter.ViewHolder<T> {

    public static abstract class Factory implements AdvancedAdapter.ViewHolder.Factory {
        protected abstract ViewBinding inflate(LayoutInflater inflater, ViewGroup parent);

        @Override
        public AdvancedAdapter.ViewHolder create(ViewGroup parent) {
            ViewBinding binding = inflate(LayoutInflater.from(parent.getContext()), parent);
            return create(binding);
        };

        protected abstract ViewBindingHolder create(ViewBinding binding);
    }
    public ViewBindingHolder(ViewBinding binding) {
        super(binding.getRoot());
    }
}
