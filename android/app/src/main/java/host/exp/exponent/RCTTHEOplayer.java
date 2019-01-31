package host.exp.exponent;

import java.util.HashMap;
import java.util.Map;

import android.support.annotation.Nullable;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;

 import com.theoplayer.android.api.player.track.texttrack.TextTrackKind;
 import com.theoplayer.android.api.source.SourceDescription;
 import com.theoplayer.android.api.source.SourceType;
 import com.theoplayer.android.api.source.TypedSource;
 import com.theoplayer.android.api.source.addescription.THEOplayerAdDescription;
 import com.theoplayer.android.api.source.TextTrackDescription;
 import com.theoplayer.android.api.source.drm.preintegration.XstreamConfiguration;

public class RCTTHEOplayer extends SimpleViewManager<RCTTHEOplayerView> {
    public static final int COMMAND_PLAY = 1;
    public static final int COMMAND_PAUSE = 2;
    public static final int COMMAND_STOP = 3;

    private final Map<String, SourceType> sourceTypes = new HashMap<String, SourceType>() {
        {
            // FIXME: Add the rest of these types
            put("application/x-mpegURL", SourceType.HLS);
            put("application/x-mpegurl", SourceType.HLS);
        }
    };

    private final Map<String, TextTrackKind> textTrackKinds = new HashMap<String, TextTrackKind>() {
        {
            put("subtitles", TextTrackKind.SUBTITLES);
            put("captions", TextTrackKind.CAPTIONS);
            put("descriptions", TextTrackKind.DESCRIPTIONS);
            put("chapters", TextTrackKind.CHAPTERS);
            put("metadata", TextTrackKind.METADATA);
        }
    };

    private final Map<String, RCTTHEOplayerView.UIStyle> theoPlayerUIStyles = new HashMap<String, RCTTHEOplayerView.UIStyle>() {
        {
            put("default", RCTTHEOplayerView.UIStyle.DEFAULT);
            put("unstyled", RCTTHEOplayerView.UIStyle.UNSTYLED);
            put("chromeless", RCTTHEOplayerView.UIStyle.CHROMELESS);
        }
    };

    @Override
    public String getName() {
        return "RCTTHEOplayer";
    }

    @Override
    public RCTTHEOplayerView createViewInstance(ThemedReactContext reactContext) {
        return new RCTTHEOplayerView(reactContext);
    }

    @Override
    public @Nullable Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("play", COMMAND_PLAY, "pause", COMMAND_PAUSE, "stop", COMMAND_STOP);
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put("topUserEvent", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onUserEvent")))
                .build();
    }

    @Override
    public void receiveCommand(RCTTHEOplayerView view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
        case COMMAND_PLAY:
            view.play();
            break;
        case COMMAND_PAUSE:
            view.pause();
            break;
        case COMMAND_STOP:
            view.stop();
            break;
        }
    }

    @ReactProp(name = "source")
    public void setSource(RCTTHEOplayerView view, @Nullable ReadableMap source) {
        SourceDescription.Builder builder = SourceDescription.Builder.sourceDescription();

        if (source != null) {
            ReadableArray sources = source.getArray("sources");
            TypedSource typedSources[] = new TypedSource[sources.size()];

            for (int i = 0; i < sources.size(); i += 1) {
                ReadableMap s = sources.getMap(i);
                if (s.hasKey("drm")) {
                    ReadableMap drmMap = s.getMap("drm");
                    String ticketAcquisitionURL = drmMap.getString("ticketAcquisitionURL");
                    String streamId = drmMap.getString("streamId");
                    String sessionId = drmMap.getString("sessionId");
                    typedSources[i] = TypedSource.Builder.typedSource().src(s.getString("src"))
                            .type(sourceTypes.get(s.getString("type")))
                            .drm(new XstreamConfiguration.Builder(streamId, ticketAcquisitionURL).sessionId(sessionId)
                                    .build())
                            .build();
                } else {
                    typedSources[i] = TypedSource.Builder.typedSource().src(s.getString("src"))
                            .type(sourceTypes.get(s.getString("type"))).build();
                }
            }

            builder = builder.sources(typedSources);

            if (source.hasKey("ads")) {
                ReadableArray ads = source.getArray("ads");
                THEOplayerAdDescription typedAds[] = new THEOplayerAdDescription[ads.size()];

                for (int i = 0; i < ads.size(); i += 1) {
                    ReadableMap s = ads.getMap(i);
                    THEOplayerAdDescription.Builder adBuilder = new THEOplayerAdDescription.Builder()
                            .source(s.getString(("src")));
                    if (s.hasKey("timeOffset")) {
                        adBuilder = adBuilder.timeOffset(s.getString("timeOffset"));
                    }
                    if (s.hasKey("skipOffset")) {
                        adBuilder = adBuilder.skipOffset(s.getString("skipOffset"));
                    }
                    typedAds[i] = adBuilder.build();
                }

                builder = builder.ads(typedAds);
            }

            if (source.hasKey("textTracks")) {
                ReadableArray textTracks = source.getArray("textTracks");
                TextTrackDescription typedTracks[] = new TextTrackDescription[textTracks.size()];

                for (int i = 0; i < textTracks.size(); i += 1) {
                    ReadableMap s = textTracks.getMap(i);
                    TextTrackDescription.Builder trackBuilder = new TextTrackDescription.Builder()
                            .src(s.getString("src")).srclang(s.getString("srcLang"));
                    if (s.hasKey("default")) {
                        trackBuilder.isDefault(s.getBoolean("default"));
                    }
                    if (s.hasKey("kind")) {
                        trackBuilder.kind(textTrackKinds.get(s.getString("kind")));
                    }
                    if (s.hasKey("label")) {
                        trackBuilder.label(s.getString("label"));
                    }
                    typedTracks[i] = trackBuilder.build();
                }

                builder = builder.textTracks(typedTracks);
            }

            if (source.hasKey("poster")) {
                builder = builder.poster(source.getString("poster"));
            }
        }

        view.setSource(builder.build());
    }

    @ReactProp(name = "autoPlay")
    public void setAutoPlay(RCTTHEOplayerView view, boolean autoPlay) {
        view.setAutoPlay(autoPlay);
    }

    @ReactProp(name = "fullscreenOrientationCoupling")
    public void setFullScreenOrientationCoupling(RCTTHEOplayerView view, boolean fullscreenOrientationCoupling) {
        // Is it possible to turn this off?
    }

    private @Nullable String[] readableArrayToStringArray(@Nullable ReadableArray input) {
        if (input == null)
            return null;

        String[] output = new String[input.size()];
        for (int i = 0; i < input.size(); i += 1) {
            output[i] = input.getString(i);
        }

        return output;
    }

    @ReactProp(name = "defaultUIStyle")
    public void setDefaultUIStyle(RCTTHEOplayerView view, String defaultUIStyle) {
        view.setDefaultUIStyle(theoPlayerUIStyles.get(defaultUIStyle));
    }

    @ReactProp(name = "defaultCssPaths")
    public void setDefaultCssPaths(RCTTHEOplayerView view, @Nullable ReadableArray defaultCssPaths) {
        view.setDefaultCssPaths(readableArrayToStringArray(defaultCssPaths));
    }

    @ReactProp(name = "defaultJsPaths")
    public void setDefaultJsPaths(RCTTHEOplayerView view, @Nullable ReadableArray defaultJsPaths) {
        view.setDefaultJsPaths(readableArrayToStringArray(defaultJsPaths));
    }
}
