package host.exp.exponent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

 import com.theoplayer.android.api.THEOplayerConfig;
 import com.theoplayer.android.api.THEOplayerView;
 import com.theoplayer.android.api.message.MessageListener;
 import com.theoplayer.android.api.source.SourceDescription;

public class RCTTHEOplayerView extends FrameLayout {
    public enum UIStyle {
        DEFAULT, UNSTYLED, CHROMELESS;
    }

    ThemedReactContext mContext;

    @Nullable
    THEOplayerView mPlayerView = null;

    UIStyle _defaultUIStyle = UIStyle.DEFAULT;
    @Nullable
    String[] _defaultCssPaths = null;
    @Nullable
    String[] _defaultJsPaths = null;

    @Nullable
    SourceDescription _source = null;
    boolean _autoPlay = false;

    public RCTTHEOplayerView(ThemedReactContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mPlayerView != null)
            return;

        THEOplayerConfig.Builder builder = new THEOplayerConfig.Builder();

        if (_defaultUIStyle.equals(UIStyle.CHROMELESS)) {
            builder = builder.chromeless(true);
        } else {
            boolean useDefaultCss = _defaultUIStyle.equals(UIStyle.DEFAULT);
            builder = builder.defaultCss(useDefaultCss);
        }

        if (_defaultCssPaths != null)
            builder = builder.cssPaths(_defaultCssPaths);
        if (_defaultJsPaths != null)
            builder = builder.jsPaths(_defaultJsPaths);

        THEOplayerConfig playerConfig = builder.build();
        mPlayerView = new THEOplayerView(mContext.getCurrentActivity(), playerConfig);

        if (_source != null)
            mPlayerView.getPlayer().setSource(_source);
        mPlayerView.getPlayer().setAutoplay(_autoPlay);

        mPlayerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mPlayerView.addJavaScriptMessageListener("userEvent", new MessageListener() {
            @Override
            public void handleMessage(String s) {
                WritableMap event = Arguments.createMap();
                event.putString("data", s);
                ReactContext reactContext = (ReactContext) getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topUserEvent", event);
            }
        });

        mPlayerView.getSettings().setFullScreenOrientationCoupled(true);

        addView(mPlayerView);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mPlayerView != null) {
            if (visibility == View.VISIBLE)
                mPlayerView.onResume();
            else
                mPlayerView.onPause();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mPlayerView != null) {
            if (hasWindowFocus)
                mPlayerView.onResume();
            else
                mPlayerView.onPause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPlayerView != null) {
            mPlayerView.onDestroy();
        }
    }

    public void setSource(SourceDescription source) {
        _source = source;
        if (mPlayerView != null)
            mPlayerView.getPlayer().setSource(source);
    }

    public void setAutoPlay(boolean autoPlay) {
        _autoPlay = autoPlay;
        if (mPlayerView != null)
            mPlayerView.getPlayer().setAutoplay(autoPlay);
    }

    public void setDefaultUIStyle(UIStyle defaultUIStyle) {
        _defaultUIStyle = defaultUIStyle;
    }

    public void setDefaultCssPaths(@Nullable String[] cssPaths) {
        _defaultCssPaths = cssPaths;
    }

    public void setDefaultJsPaths(@Nullable String[] jsPaths) {
        _defaultJsPaths = jsPaths;
    }

    public void play() {
        if (mPlayerView != null) {
            mPlayerView.getPlayer().play();
        }
    }

    public void pause() {
        if (mPlayerView != null)
            mPlayerView.getPlayer().pause();
    }

    public void stop() {
        if (mPlayerView != null)
            mPlayerView.getPlayer().stop();
    }
}