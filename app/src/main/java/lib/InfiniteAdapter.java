package lib;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class InfiniteAdapter extends AdvancedAdapter<Integer> {
    public static final int MIDDLE_POSITION =  Integer.MAX_VALUE / 2;
    public static int getOffset(int position) {
        return position - MIDDLE_POSITION;
    }

    protected HashMap<Integer, ViewHolder> holderCache = new HashMap<>();

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(Integer.valueOf(position));
        holderCache.put(position, holder);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holderCache.remove(holder.getAdapterPosition());
    }

    public ViewHolder getHolderAt(int position) {
        return holderCache.get(position);
    }

    public InfiniteAdapter(ViewHolder.Factory viewHolderFactory) {
        super(null, viewHolderFactory);
    }
}
