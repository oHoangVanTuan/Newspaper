package com.github.ayltai.newspaper;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.github.ayltai.newspaper.item.ItemPresenter;
import com.github.ayltai.newspaper.item.ItemScreen;
import com.github.ayltai.newspaper.list.ListScreen;
import com.github.ayltai.newspaper.main.MainPresenter;
import com.github.ayltai.newspaper.main.MainScreen;
import com.github.ayltai.newspaper.setting.Settings;

import flow.Flow;
import flow.KeyDispatcher;
import flow.KeyParceler;
import flow.State;
import flow.TraversalCallback;
import io.realm.Realm;
import rx.subscriptions.CompositeSubscription;

final class FlowController {
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    //region Cached presenters and views

    private final Map<Class<?>, Presenter.View>  screens    = new HashMap<>();
    private final Map<Presenter.View, Presenter> presenters = new HashMap<>();

    //endregion

    //region Variables

    private final Activity activity;
    private final Realm    realm;

    //endregion

    FlowController(@NonNull final Activity activity) {
        this.activity = activity;
        this.realm    = Realm.getDefaultInstance();
    }

    public Context attachNewBase(@NonNull final Context newBase) {
        return Flow.configure(newBase, this.activity)
            .keyParceler(new KeyParceler() {
                @NonNull
                @Override
                public Parcelable toParcelable(@NonNull final Object key) {
                    return (Parcelable)key;
                }

                @NonNull
                @Override
                public Object toKey(@NonNull final Parcelable parcelable) {
                    return parcelable;
                }
            })
            .dispatcher(KeyDispatcher.configure(this.activity, (outgoingState, incomingState, direction, incomingContexts, callback) -> {
                if (outgoingState != null)
                    outgoingState.save(((ViewGroup)this.activity.findViewById(android.R.id.content)).getChildAt(0));

                final Presenter.View view;
                final Presenter      presenter;

                if (this.screens.containsKey(incomingState.getKey().getClass())) {
                    view      = this.screens.get(incomingState.getKey().getClass());
                    presenter = this.presenters.get(view);
                } else {
                    if (incomingState.getKey() instanceof ItemScreen.Key) {
                        view      = new ItemScreen(this.activity);
                        presenter = new ItemPresenter(this.realm);

                        this.subscriptions.add(view.attachments().subscribe(dummy -> presenter.onViewAttached(view), error -> Log.e(this.getClass().getSimpleName(), error.getMessage(), error)));
                        this.subscriptions.add(view.detachments().subscribe(dummy -> presenter.onViewDetached(), error -> Log.e(this.getClass().getSimpleName(), error.getMessage(), error)));
                    } else {
                        view      = new MainScreen(this.activity, this.realm);
                        presenter = new MainPresenter();
                    }

                    this.screens.put(incomingState.getKey().getClass(), view);
                    this.presenters.put(view, presenter);
                }

                if (incomingState.getKey() instanceof ItemScreen.Key) {
                    final ItemScreen.Key key = incomingState.getKey();

                    ((ItemPresenter)presenter).bind((ListScreen.Key)key.getParentKey(), key.getItem(), Settings.getListViewType(this.activity));
                }

                //this.dispatch((View)view, incomingState, callback);
                this.transit(outgoingState, incomingState, outgoingState == null ? null : (View)this.screens.get(outgoingState.getKey().getClass()), (View)view, callback);
            }).build())
            .defaultKey(Constants.KEY_SCREEN_MAIN)
            .install();
    }

    public boolean onBackPressed() {
        if (!Flow.get(this.activity).goBack()) {
            final View view = ((ViewGroup)this.activity.findViewById(android.R.id.content)).getChildAt(0);

            if (view instanceof MainScreen) {
                if (!((MainScreen)view).goBack()) return false;
            }
        }

        return true;
    }

    public void onDestroy() {
        if (this.subscriptions.hasSubscriptions()) this.subscriptions.unsubscribe();

        for (final Presenter.View view : this.screens.values()) {
            if (view instanceof Closeable) {
                try {
                    ((Closeable)view).close();
                } catch (final IOException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }

        if (!this.realm.isClosed()) this.realm.close();
    }

    private void transit(@Nullable final State outgoingState, @NonNull final State incomingState, @Nullable final View fromView, @NonNull final View toView, @NonNull final TraversalCallback callback) {
        if (outgoingState == null || fromView == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.dispatch(toView, incomingState, callback);
        } else {
            final Animation animation = AnimationUtils.loadAnimation(this.activity, R.anim.slide_out_bottom);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(final Animation animation) {
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    FlowController.this.dispatch(toView, incomingState, callback);

                    toView.startAnimation(AnimationUtils.loadAnimation(FlowController.this.activity, R.anim.slide_in_bottom));
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }
            });

            fromView.startAnimation(animation);
        }
    }

    private void dispatch(@NonNull final View view, @NonNull final State incomingState, @NonNull final TraversalCallback callback) {
        incomingState.restore(view);

        this.activity.setContentView(view);

        callback.onTraversalCompleted();
    }
}
