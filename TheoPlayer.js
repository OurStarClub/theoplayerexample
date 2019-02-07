import React from "react";
import { StyleSheet, UIManager, findNodeHandle } from "react-native";
import { requireNativeComponent } from "react-native";

const RCTTHEOplayer = requireNativeComponent("RCTTHEOplayer", TheoPlayer, {
  nativeOnly: { onUserEvent: true }
});

export default class TheoPlayer extends React.Component {
  video = React.createRef();

  play() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.video.current),
      UIManager.RCTTHEOplayer.Commands.play,
      null
    );
  }

  pause() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.video.current),
      UIManager.RCTTHEOplayer.Commands.pause,
      null
    );
  }

  stop() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.video.current),
      UIManager.RCTTHEOplayer.Commands.stop,
      null
    );
  }

  render() {
    return (
      <RCTTHEOplayer
        ref={this.video}
        isEmbeddable={true}
        defaultUIStyle="default"
        autoPlay={false}
        fullscreenOrientationCoupling={true}
        style={{
          ...StyleSheet.absoluteFillObject,
          height: 300,
          backgroundColor: "black",
          aspectRatio: 1.7
        }}
        source={{
          sources: [
            {
              src:
                "https://cdn.theoplayer.com/video/elephants-dream/playlist.m3u8",
              type: "application/x-mpegURL"
            }
          ],

          poster: "http://cdn.theoplayer.com/video/big_buck_bunny/poster.jpg"
        }}
        onError={e => console.error(e)}
      />
    );
  }
}
