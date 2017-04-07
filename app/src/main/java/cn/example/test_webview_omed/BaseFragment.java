package cn.example.test_webview_omed;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
    protected static final String TITLE = "title";
    private final String SAVE_STATE = "saveState";
    //根控件view
    protected View rootView;
    //上下文
    protected Context mContext;

    protected boolean isPrepared;

    protected int netState;

    protected Bundle saveState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutView(), null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        mContext = getActivity();
//        ButterKnife.bind(this, rootView);
//        EventBus.getDefault().register(this);
        isPrepared = true;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (rootView.findViewById(R.id.ib_return) != null) {
//            rootView.findViewById(R.id.ib_return).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//        }
//        netState = NetUtil.getNetworkState(mContext);
        init();
        initSet();
//        Bundle bundle = getArguments();
//        if (!restoreStateFromArguments()) {
//            onFirstLaunched(bundle);
//        }
//        loadData(bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
//        EventBus.getDefault().unregister(this);
        saveStateToArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = JSApp.getRefWatcher();
//        if (refWatcher != null) {
//            refWatcher.watch(this);
//        }
    }

    protected abstract int getLayoutView();

    protected void onFirstLaunched(Bundle bundle) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    /*
            在这个方法里做一些对象实例化的操作
         */
    protected abstract void init();

    /*
        在这个方法里为控件设置监听事件
     */
    protected abstract void initSet();

    public void loadData(Bundle bundle) {
        if (bundle != null) {
            String title = bundle.getString(TITLE);
//            if (rootView.findViewById(R.id.tv_title) != null) {
//                TextView tv = (TextView) rootView.findViewById(R.id.tv_title);
//                tv.setText(title);
//            }
        }
    }

    public void showToast(String text) {
//        ToastUtil.showShortToast(mContext, text);
    }

    public void showToast(int stringId) {
        showToast(getString(stringId));
    }

    public void finish() {
        getActivity().finish();
    }

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        saveState = b.getBundle(SAVE_STATE);
        if (saveState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    private void saveStateToArguments() {
        if (getView() != null) {
            saveState = getSaveState();
        }
        if (saveState != null) {
            Bundle b = getArguments();
            if (b == null) {
                return;
            }
            b.putBundle(SAVE_STATE, saveState);
        }
    }

    private void restoreState() {
        if (saveState != null) {
            onRestoreState(saveState);
        }
    }

    private Bundle getSaveState() {
        Bundle state = new Bundle();
        onSaveState(state);
        return state;
    }

    protected void onSaveState(Bundle outState) {

    }

    protected void onRestoreState(Bundle savedInstanceState) {

    }

    public static BaseFragment newInstance(Class<? extends BaseFragment> c) {
        BaseFragment prod = null;
        Bundle bundle = new Bundle();
        try {
            prod = (BaseFragment) Class.forName(c.getName()).newInstance();
            prod.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prod;
    }

    public void toastNetError() {
//        ToastUtil.showShortToast(mContext, getString(R.string.out_of_network));
    }

    public static BaseFragment newInstanceWithTitle(Class<? extends BaseFragment> c, String title) {
        BaseFragment prod = null;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        try {
            prod = (BaseFragment) Class.forName(c.getName()).newInstance();
            prod.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prod;
    }

    public boolean onBackPress() {
        return false;
    }
}
