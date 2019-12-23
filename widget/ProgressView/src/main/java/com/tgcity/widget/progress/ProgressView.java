package com.tgcity.widget.progress;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.tgcity.function.network.cache.ErrorMode;
import com.tgcity.utils.IntentUtils;
import com.tgcity.widget.progress.utils.ErrorEventOperation;
import com.tgcity.widget.progress.utils.GameCountDownTimerUtils;
import com.tgcity.widget.progressview.R;


/**
 * @author TGCity
 * @description 列表中的加载浮层，拥有加载中的动画、加载失败的布局、加载完成后的处理
 * 在AbstractProgressLayout上做了个简单的二次封装，加入了自动判断错误类型的功能
 * 警告：为AbstractProgressLayout加入了自定义布局，使用本控件时在未设置setLayout()之前禁止调用showCustom()方法！
 */

public class ProgressView extends AbstractProgressLayout {

    //出错重试等待时间（暂时定为10秒）
    public final static long SERVICE_TRY_WAIT = 10 * 1000;
    //出错重试时的间隔时间（暂时定为1秒）
    public final static long SERVICE_TRY_INTERVAL = 1 * 1000;

    private int normalErrorButtonTextColor = getResources().getColor(R.color.pv_color_FF6C4B);
    private int stopErrorButtonTextColor = getResources().getColor(R.color.pv_color_aaaaaa);
    private Drawable normalErrorButtonBackground = getResources().getDrawable(R.drawable.pv_fillet_button_normal);
    private Drawable stopErrorButtonBackground = getResources().getDrawable(R.drawable.pv_fillet_button_stop);

    private GameCountDownTimerUtils gameCountDownTimerUtils;

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int setLayout() {
        //为ProgressActivity加入了自定义布局，禁止调用showCustom()方法，如需使用，请返回一个自定义布局ID。
        return 0;
    }

    @Override
    protected void bindView(View view) {

    }

    /**
     * 自适应空布局
     */
    public void setEmptyWrapContenHeight() {
        if (emptyStateRelativeLayout != null && emptyStateRelativeLayout.getLayoutParams() != null) {
            emptyStateRelativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 错误判断
     */
    public void errorOperation(final Throwable throwable, OnProgressViewCallBack callBack) {
        errorOperation(ErrorEventOperation.depositReturnErrorMode(throwable), callBack,
                //内部处理
                new OnDataErrorCallBack() {
                    @Override
                    public void onDataFormatError() {
//                        LogUtils.e(throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onCastError() {
//                        LogUtils.e(throwable.getLocalizedMessage());
                    }
                });
    }

    /**
     * 错误判断
     */
    public void errorOperation(final ErrorMode errorMode, final OnProgressViewCallBack callBack, final OnDataErrorCallBack onDataErrorCallBack) {
        if (errorMode == null) {
            reset();
            showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_DEFAULT_TITLE), getResources().getString(R.string.pv_ERROR_DEFAULT_CONTENT), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null) {
                        showLoading();
                        callBack.onReTry();
                    }
                }
            });
        } else {
            ErrorEventOperation.operation(getContext(), errorMode, new ErrorEventOperation.OnErrorEventOperationCallBack() {
                @Override
                public void onNoAuthority() {
                    reset();
                    showError(getResources().getDrawable(R.drawable.icon_authority), errorMode.getErrorTitle(), getResources().getString(R.string.pv_ERROR_API_NO_AUTHORITY_TITLE), getResources().getString(R.string.pv_ERROR_API_NO_AUTHORITY_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            RouteHelper.with(VipIntroduceActivity.class).build();
                        }
                    });
                }

                @Override
                public void onVisualizationMsg(final String msg) {
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), errorMode.getErrorTitle(), getResources().getString(R.string.pv_ERROR_DEFAULT_TITLE), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                showLoading();
                                callBack.onReTry();
                            }
                        }
                    });

                }

                @Override
                public void onOverLoad() {
                    startCountDown();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_OVERLOAD_TITLE), getResources().getString(R.string.pv_ERROR_OVERLOAD_DESC), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gameCountDownTimerUtils.isFinished()) {
                                return;
                            }
                            if (callBack != null) {
                                showLoading();
                                callBack.onReTry();
                            }
                        }
                    });
                }

                @Override
                public void onNoNetWork() {
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_NO_NETWORK_TITLE), getResources().getString(R.string.pv_ERROR_NO_NETWORK_DESC), getResources().getString(R.string.pv_ERROR_NO_NETWORK_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getContext().startActivity(IntentUtils.getNetworkIntent());
                        }
                    });
                }

                @Override
                public void onSignatureFailTime() {
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_SINGNATURE_FAILURE_TIME_TITLE), getResources().getString(R.string.pv_ERROR_SINGNATURE_FAILURE_TIME_DESC), getResources().getString(R.string.pv_ERROR_NO_NETWORK_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getContext().startActivity(IntentUtils.getDateIntent());
                        }
                    });
                }

                @Override
                public void onTimeOut() {
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_CONNECT_TIME_OUT_TITLE), getResources().getString(R.string.pv_ERROR_NO_NETWORK_DESC), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                showLoading();
                                callBack.onReTry();
                            }
                        }
                    });

                }

                @Override
                public void onOther(ErrorMode errorMode1) {
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_DEFAULT_TITLE), errorMode.getErrorTitle(), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                showLoading();
                                callBack.onReTry();
                            }

                        }
                    });

                }

                @Override
                public void onDataFormatError() {
                    //内部单独处理
                    if (onDataErrorCallBack != null) {
                        onDataErrorCallBack.onDataFormatError();
                    }
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_FORMATE_DATA_TITLE), errorMode.getErrorTitle(), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                showLoading();
                                callBack.onReTry();
                            }

                        }
                    });
                }

                @Override
                public void onCastError() {
                    //内部单独处理
                    if (onDataErrorCallBack != null) {
                        onDataErrorCallBack.onCastError();
                    }
                    reset();
                    showError(getResources().getDrawable(R.drawable.monkey_cry), getResources().getString(R.string.pv_ERROR_CLASS_CAST_TITLE), errorMode.getErrorTitle(), getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                showLoading();
                                callBack.onReTry();
                            }

                        }
                    });
                }
            });
        }
    }

    private void reset() {
        if (gameCountDownTimerUtils != null) {
            gameCountDownTimerUtils.cancel();
            gameCountDownTimerUtils.onFinish();
        }

    }

    /**
     * 重试按钮开始倒计时
     */
    private void startCountDown() {
        if (gameCountDownTimerUtils != null) {
            gameCountDownTimerUtils.clear();
            gameCountDownTimerUtils = null;
        }
        gameCountDownTimerUtils = new GameCountDownTimerUtils(SERVICE_TRY_WAIT, SERVICE_TRY_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished, int second) {
                if (errorStateButton != null) {
                    errorStateButton.setEnabled(false);
                    errorStateButton.setTextColor(stopErrorButtonTextColor);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        errorStateButton.setBackground(stopErrorButtonBackground);
                    }
                    errorStateButton.setText("请" + second + "秒后再试");
                }
            }

            @Override
            public void onFinish() {
                if (errorStateButton != null) {
                    errorStateButton.setEnabled(true);
                    errorStateButton.setTextColor(normalErrorButtonTextColor);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        errorStateButton.setBackground(normalErrorButtonBackground);
                    }
                    errorStateButton.setText(getResources().getString(R.string.pv_ERROR_DEFAULT_BUTTON));
                }
            }
        };
        gameCountDownTimerUtils.start();

    }

    @Override
    public void clear() {
        normalErrorButtonTextColor = 0;
        stopErrorButtonTextColor = 0;
        if (gameCountDownTimerUtils != null) {
            gameCountDownTimerUtils.clear();
        }
        gameCountDownTimerUtils = null;
        normalErrorButtonBackground = null;
        stopErrorButtonBackground = null;
    }

    public interface OnProgressViewCallBack {

        /**
         * retry
         */
        void onReTry();

        /**
         * other
         *
         * @param errorMode ErrorMode
         */
        void onOther(ErrorMode errorMode);

    }

    public interface OnDataErrorCallBack {

        /**
         * data format error
         */
        void onDataFormatError();

        /**
         * cast error
         */
        void onCastError();
    }
}
