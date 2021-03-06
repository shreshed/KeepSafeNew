package com.abhinav.keepsafe.home.category.email.show;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhinav.keepsafe.BaseActivity;
import com.abhinav.keepsafe.BaseFragment;
import com.abhinav.keepsafe.Constants;
import com.abhinav.keepsafe.R;
import com.abhinav.keepsafe.entities.Email;
import com.abhinav.keepsafe.home.category.email.edit.EditEmailFragment;
import com.abhinav.keepsafe.view.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.abhinav.keepsafe.Constants.Defaults.ALPHA_ANIMATIONS_DURATION;
import static com.abhinav.keepsafe.Constants.Defaults.PERCENTAGE_TO_HIDE_TITLE_DETAILS;
import static com.abhinav.keepsafe.Constants.Defaults.PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR;

/**
 * Created by abhinav.sharma on 25/11/17.
 */

public class ShowEmailFragment extends BaseFragment implements ShowEmailView, AppBarLayout.OnOffsetChangedListener {


    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_category_name)
    TextView tvCategoryName;
    @BindView(R.id.ll_title_container)
    LinearLayout llTitleContainer;
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.vs_category_item)
    ViewStub vsCategoryItem;
    @BindView(R.id.cv_container)
    CardView cvContainer;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_circle)
    CircleImageView ivCircle;
    Unbinder unbinder;
    private Context context;
    private int emailId;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private EditText etPlatformName, etRecoveryMail, etPassword;
    private ShowEmailPresenter mPresenter;

    public static ShowEmailFragment getInstance(int emailId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ExtrasKey.EMAIL_ID, emailId);
        ShowEmailFragment showEmailFragment = new ShowEmailFragment();
        showEmailFragment.setArguments(bundle);
        return showEmailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_category_item, container, false);
        setHasOptionsMenu(true);
        emailId = getArguments().getInt(Constants.ExtrasKey.EMAIL_ID, -1);
        unbinder = ButterKnife.bind(this, view);
        mPresenter = new ShowEmailPresenter(this);
        initCoordinatorView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            mPresenter.onEditClicked(emailId);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateEmailViewStub();
        initEmailViewStub(view);
        mPresenter.fetchEmailDetails(emailId);
    }

    private void initCoordinatorView() {
        setupToolbar(toolbar);
        toolbar.setTitle(null);
        appbar.addOnOffsetChangedListener(this);
        ivCircle.setImageResource(R.drawable.email_logo);
        ivHeader.setImageResource(R.drawable.img_email_header);
        startAlphaAnimation(tvTitle, 0, View.INVISIBLE);
    }

    private void initEmailViewStub(View view) {
        etPlatformName = view.findViewById(R.id.et_platform_name);
        etRecoveryMail = view.findViewById(R.id.et_recovery_email);
        etPassword = view.findViewById(R.id.et_email_password);

    }

    private void inflateEmailViewStub() {
        vsCategoryItem.setInflatedId(R.id.layout_email_details);
        vsCategoryItem.setLayoutResource(R.layout.layout_email_details);
        vsCategoryItem.inflate();
    }

    @Override
    public void showEmailDetails(Email email) {
        tvTitle.setText(email.getEmailId());
        tvCategoryName.setText(email.getEmailId());
        etPassword.setText(email.getEmailPassword());
        etRecoveryMail.setText(email.getRecoveryEmail());
        etPlatformName.setText(email.getPlatformName());
    }

    @Override
    public void showEditEmailFragment(int emailId) {
        ((BaseActivity) context).addFragmentWithBackStack(getFragmentManager(),
                EditEmailFragment.getInstance(emailId), R.id.frame_container,
                EditEmailFragment.class.getSimpleName());
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(tvTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(tvTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(llTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(llTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void popFragment() {
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mPresenter.detachView();
    }
}
