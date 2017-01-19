package com.github.ayltai.newspaper.list;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ayltai.newspaper.Constants;
import com.github.ayltai.newspaper.R;
import com.github.ayltai.newspaper.RxBus;
import com.github.ayltai.newspaper.data.Feed;
import com.github.ayltai.newspaper.data.FeedManager;
import com.github.ayltai.newspaper.item.ItemPresenter;
import com.github.ayltai.newspaper.item.ItemViewHolder;
import com.github.ayltai.newspaper.rss.Item;
import com.github.ayltai.newspaper.util.LogUtils;
import com.jakewharton.rxbinding.view.RxView;

import io.realm.ItemRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

final class ListAdapter extends RealmRecyclerViewAdapter<Item, ItemViewHolder> implements Closeable {
    //region Variables

    private final Map<ItemViewHolder, ItemPresenter> map           = new HashMap<>();
    private final CompositeSubscription              subscriptions = new CompositeSubscription();
    private final Context                            context;
    private final ListScreen.Key                     parentKey;
    private final int                                listViewType;
    private final Realm                              realm;
    private final Subscriber<Item>                   subscriber;

    private Feed feed;

    //endregion

    ListAdapter(@NonNull final Context context, @NonNull final ListScreen.Key parentKey, @Constants.ListViewType final int listViewType, @Nullable final Feed feed, @NonNull final Realm realm) {
        super(context, feed == null ? new RealmList<>() : feed.getItems(), false);

        this.context      = context;
        this.parentKey    = parentKey;
        this.listViewType = listViewType;
        this.feed         = feed;
        this.realm        = realm;

        if (Constants.SOURCE_BOOKMARK.equals(this.parentKey.getUrl())) {
            this.subscriber = new Subscriber<Item>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(final Throwable e) {
                    LogUtils.getInstance().e(this.getClass().getSimpleName(), e.getMessage(), e);
                }

                @Override
                public void onNext(final Item item) {
                    new FeedManager(ListAdapter.this.realm).getFeed(Constants.SOURCE_BOOKMARK)
                        .subscribe(feed -> {
                            ListAdapter.this.feed = feed;

                            if (feed.contains(item)) {
                                if (feed.getItems().size() == 1) {
                                    // Hides empty view
                                    RxBus.getInstance().send(feed);
                                } else {
                                    ListAdapter.this.notifyItemInserted(feed.indexOf(item));
                                }
                            } else {
                                if (feed.getItems().isEmpty()) {
                                    // Shows empty view
                                    RxBus.getInstance().send(feed);
                                } else {
                                    ListAdapter.this.notifyItemRemoved(feed.indexOf(item));
                                }
                            }
                        });
                }
            };

            RxBus.getInstance().register(Item.class, this.subscriber);
            RxBus.getInstance().register(ItemRealmProxy.class, this.subscriber);
        } else {
            this.subscriber = null;
        }
    }

    @Override
    public int getItemCount() {
        return this.feed == null ? 0 : this.feed.getItems().size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.view_item_container, parent, false);

        LayoutInflater.from(this.context).inflate(this.listViewType == Constants.LIST_VIEW_TYPE_COZY ? R.layout.view_item_cozy : R.layout.view_item_compact, (ViewGroup)view.findViewById(R.id.smContentView), true);

        final ItemViewHolder holder    = new ItemViewHolder(view);
        final ItemPresenter  presenter = new ItemPresenter(this.realm);

        this.subscriptions.add(RxView.attaches(view).subscribe(dummy -> presenter.onViewAttached(holder)));
        this.subscriptions.add(RxView.detaches(view).subscribe(dummy -> presenter.onViewDetached()));

        this.map.put(holder, presenter);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        this.map.get(holder).bind(this.parentKey, this.feed.getItems().get(position), this.listViewType);
    }

    @Override
    public void close() {
        if (this.subscriber != null) {
            RxBus.getInstance().unregister(Item.class, this.subscriber);
            RxBus.getInstance().unregister(ItemRealmProxy.class, this.subscriber);
        }

        if (this.subscriptions.hasSubscriptions()) this.subscriptions.unsubscribe();

        for (final ItemViewHolder holder : this.map.keySet()) holder.close();

        this.map.clear();
    }
}
