package com.github.ayltai.newspaper.app.widget;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.AttrRes;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.github.ayltai.newspaper.Constants;
import com.github.ayltai.newspaper.R;
import com.github.ayltai.newspaper.app.MainActivity;
import com.github.ayltai.newspaper.app.view.PagerNewsPresenter;
import com.github.ayltai.newspaper.app.config.UserConfig;
import com.github.ayltai.newspaper.widget.ListView;
import com.github.ayltai.newspaper.widget.ObservableView;
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

public class PagerNewsView extends ObservableView implements PagerNewsPresenter.View {
    private final FlowableProcessor<Integer> pageSelections = PublishProcessor.create();

    private ViewPager        viewPager;
    private PagerNewsAdapter adapter;

    //region Constructors

    public PagerNewsView(@NonNull final Context context) {
        super(context);
        this.init();
    }

    public PagerNewsView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PagerNewsView(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public PagerNewsView(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    //endregion

    @NonNull
    @Override
    public Flowable<Integer> pageSelections() {
        return this.pageSelections;
    }

    @CallSuper
    @Override
    protected void onAttachedToWindow() {
        if (this.isFirstTimeAttachment) {
            this.adapter = new PagerNewsAdapter(this.getContext());

            final LifecycleOwner lifecycleOwner = this.getLifecycleOwner();
            if (lifecycleOwner != null) lifecycleOwner.getLifecycle().addObserver(this.adapter);

            this.viewPager.setAdapter(this.adapter);
            this.manageDisposable(RxViewPager.pageSelections(this.viewPager).subscribe(index -> {
                this.adapter.setCurrentPosition(index);

                this.pageSelections.onNext(index);
            }));

            this.pageSelections.onNext(0);
        }

        super.onAttachedToWindow();
    }

    //region Methods

    @Override
    public void up() {
        final ListView view = this.adapter.getItem(this.viewPager.getCurrentItem());
        if (view != null) view.up();
    }

    @Override
    public void refresh() {
        final ListView view = this.adapter.getItem(this.viewPager.getCurrentItem());
        if (view != null) view.refresh();
    }

    @Override
    public void clear() {
    }

    @Override
    public void filter() {
        final OptionsView view = new OptionsView(this.getContext(), UserConfig.getTheme(this.getContext()) == Constants.THEME_LIGHT ? R.style.AppDialogThemeLight : R.style.AppDialogThemeDark);

        this.manageDisposable(view.cancelClicks().subscribe(irrelevant -> view.dismiss()));

        this.manageDisposable(view.okClicks().subscribe(irrelevant -> {
            view.dismiss();

            final Activity activity = this.getActivity();
            if (activity != null) activity.finish();

            this.getContext().startActivity(new Intent(this.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }));

        view.show();
    }

    @Override
    public void search(@Nullable final CharSequence newText) {
        if (this.adapter != null) this.adapter.getFilter().filter(newText);
    }

    private void init() {
        final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.view_news_pager, this, true);

        this.viewPager = view.findViewById(R.id.viewPager);

        final TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(this.viewPager, true);
    }

    //endregion
}
