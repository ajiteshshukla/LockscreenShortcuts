package com.acubeapps.lockscreen.shortcuts.curtail;

import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.player.DemoPlayer;
import com.acubeapps.lockscreen.shortcuts.player.ExtractorRendererBuilder;
import com.acubeapps.lockscreen.shortcuts.player.HlsRendererBuilder;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.util.Util;

/**
 * Created by aasha.medhi on 29/06/16.
 */
public class LivePlayerTextureView implements TextureView.SurfaceTextureListener {

    // Context of activity
    private Context mContext = null;

    // Layout inflater
    private LayoutInflater mInflater = null;

    // Exo player
    private DemoPlayer mExoPlayer = null;
    private AspectRatioFrameLayout mVideoFrame = null;
    public TextureView mTextureView = null;

    // Listener for clients
    private DemoPlayer.Listener mPlayerStateListener = null;

    // Player STATE flags
    private boolean mPlayerNeedsPrepare;
    private long mPlayerPosition = 0;
    private boolean mIsVideoPlaying = false;

    // Content uri associated with this player
    private Uri mContentUri = null;
    private Handler handler;

    public LivePlayerTextureView(Context context) {
        mContext = context;
        handler = new Handler();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mVideoFrame = (AspectRatioFrameLayout) mInflater.inflate(R.layout.player_live, null);

        mTextureView = (TextureView) mVideoFrame.findViewById(R.id.surface_view);

        mTextureView.setSurfaceTextureListener(this);
    }

    public void setPlayerStateListener(DemoPlayer.Listener listener) {
        this.mPlayerStateListener = listener;
    }

    public void setMute(boolean mute) {
        if (mute) {
            mExoPlayer.setSelectedTrack(DemoPlayer.TYPE_AUDIO, DemoPlayer.TRACK_DISABLED);
        } else {
            mExoPlayer.setSelectedTrack(DemoPlayer.TYPE_AUDIO, DemoPlayer.TRACK_DEFAULT);
        }
    }

    public void preparePlayer(boolean playWhenReady, boolean isStreaming) {
        if (mExoPlayer == null) {
            if (isStreaming) {
                mExoPlayer = new DemoPlayer(getStreamingRendererBuilder());
            } else {
                mExoPlayer = new DemoPlayer(getRendererBuilder());
            }
            if (null != mPlayerStateListener) {
                mExoPlayer.addListener(mPlayerStateListener);
            }
            mPlayerNeedsPrepare = true;
        }
        if (mTextureView.getSurfaceTexture() != null) {
            mExoPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
        }
        if (playWhenReady) {
            play();
        } else {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    public void play() {
        setPlayerPosition((int) mPlayerPosition);
        if (mPlayerNeedsPrepare) {
            mExoPlayer.prepare();
            mPlayerNeedsPrepare = false;
        }
        mExoPlayer.setPlayWhenReady(true);
        mIsVideoPlaying = true;
    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mPlayerPosition = 0;
            mExoPlayer.release();
            mExoPlayer = null;
        }
        mIsVideoPlaying = false;
        handler.removeCallbacksAndMessages(null);
    }

    public long getPlayerPosition() {
        if (mExoPlayer != null) {
            return mExoPlayer.getCurrentPosition();
        }
        return mPlayerPosition;
    }

    public boolean isPlaying() {
        return mIsVideoPlaying;
    }

    protected DemoPlayer.RendererBuilder getStreamingRendererBuilder() {
        String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
        return new HlsRendererBuilder(mContext, userAgent, mContentUri.toString());
    }

    protected DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
        return new ExtractorRendererBuilder(mContext, userAgent, mContentUri);
    }

    public AspectRatioFrameLayout getVideoFrame() {
        return mVideoFrame;
    }

    public void setPlayerPosition(int seekPosition) {
        mPlayerPosition = seekPosition;
        if (mExoPlayer == null) {
            preparePlayer(true, false);
        } else {
            mExoPlayer.seekTo(mPlayerPosition);
        }
    }

    public void setContentUri(Uri uri) {
        mContentUri = uri;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mExoPlayer != null) {
            mExoPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
