package com.hmscl.huawei.ads.mediation_adapter_mopub.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.mopub.common.Preconditions
import com.mopub.common.logging.MoPubLog
import com.mopub.common.logging.MoPubLog.SdkLogEvent
import com.mopub.common.util.Dips
import com.mopub.common.util.Drawables
import com.mopub.mobileads.VastVideoProgressBarWidget
import com.mopub.mobileads.resource.DrawableConstants.GradientStrip

class HuaweiAdsMediaLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr) {
    enum class Mode {
        IMAGE, PLAYING, LOADING, BUFFERING, PAUSED, FINISHED
    }

    enum class MuteState {
        MUTED, UNMUTED
    }

    @Volatile
    private var mMode = Mode.IMAGE
    private var mMuteState: MuteState
    private val mMainImageView: ImageView

    // These views are video-only, ordered by their z index. Don't create them if they aren't needed.
    var textureView: TextureView? = null
        private set
    private var mLoadingSpinner: ProgressBar? = null
    private var mPlayButton: ImageView? = null
    private var mBottomGradient: ImageView? = null
    private var mTopGradient: ImageView? = null
    private var mVideoProgress: VastVideoProgressBarWidget? = null
    private var mMuteControl: ImageView? = null
    private var mOverlay: View? = null
    private var mMutedDrawable: Drawable? = null
    private var mUnmutedDrawable: Drawable? = null
    private var mIsInitialized = false

    // Measurements
    private val mControlSizePx: Int
    private val mGradientStripHeightPx: Int
    private val mMuteSizePx: Int
    private val mPaddingPx: Int
    fun setSurfaceTextureListener(stl: SurfaceTextureListener?) {
        if (textureView != null) {
            textureView!!.surfaceTextureListener = stl
            val st = textureView!!.surfaceTexture
            if (st != null && stl != null) {
                stl.onSurfaceTextureAvailable(st, textureView!!.width, textureView!!.height)
            }
        }
    }

    /**
     * Users should call this method when the view will be used for video. Video views are not
     * instantiated in the image-only case in order to save time and memory.
     */
    fun initForVideo() {
        if (mIsInitialized) {
            return
        }

        // Init and set up all the video view items.
        val videoTextureLayoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        videoTextureLayoutParams.addRule(CENTER_IN_PARENT)
        textureView = TextureView(context)
        textureView!!.layoutParams = videoTextureLayoutParams
        textureView!!.id = generateViewId()
        addView(textureView)

        // Place texture beneath image.
        mMainImageView.bringToFront()
        val loadingSpinnerParams = LayoutParams(mControlSizePx, mControlSizePx)
        loadingSpinnerParams.addRule(ALIGN_PARENT_TOP)
        loadingSpinnerParams.addRule(ALIGN_PARENT_RIGHT)
        mLoadingSpinner = ProgressBar(context)
        mLoadingSpinner!!.layoutParams = loadingSpinnerParams
        mLoadingSpinner!!.setPadding(0, mPaddingPx, mPaddingPx, 0)
        mLoadingSpinner!!.isIndeterminate = true
        addView(mLoadingSpinner)
        val bottomGradientParams = LayoutParams(LayoutParams.MATCH_PARENT, mGradientStripHeightPx)
        bottomGradientParams.addRule(ALIGN_BOTTOM, textureView!!.id)
        mBottomGradient = ImageView(context)
        mBottomGradient!!.layoutParams = bottomGradientParams
        val bottomGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(GradientStrip.START_COLOR, GradientStrip.END_COLOR)
        )
        mBottomGradient!!.setImageDrawable(bottomGradientDrawable)
        addView(mBottomGradient)
        val topGradientParams = LayoutParams(LayoutParams.MATCH_PARENT, mGradientStripHeightPx)
        topGradientParams.addRule(ALIGN_TOP, textureView!!.id)
        mTopGradient = ImageView(context)
        mTopGradient!!.layoutParams = topGradientParams
        val topGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(GradientStrip.START_COLOR, GradientStrip.END_COLOR)
        )
        mTopGradient!!.setImageDrawable(topGradientDrawable)
        addView(mTopGradient)
        mVideoProgress = VastVideoProgressBarWidget(context)
        mVideoProgress!!.setAnchorId(textureView!!.id)
        mVideoProgress!!.calibrateAndMakeVisible(1000, 0)
        addView(mVideoProgress)
        mMutedDrawable = Drawables.NATIVE_MUTED.createDrawable(context)
        mUnmutedDrawable = Drawables.NATIVE_UNMUTED.createDrawable(context)
        val muteControlParams = LayoutParams(mMuteSizePx, mMuteSizePx)
        muteControlParams.addRule(ALIGN_PARENT_LEFT)
        muteControlParams.addRule(ABOVE, mVideoProgress!!.id)
        mMuteControl = ImageView(context)
        mMuteControl!!.layoutParams = muteControlParams
        mMuteControl!!.scaleType = ImageView.ScaleType.CENTER_INSIDE
        mMuteControl!!.setPadding(mPaddingPx, mPaddingPx, mPaddingPx, mPaddingPx)
        mMuteControl!!.setImageDrawable(mMutedDrawable)
        addView(mMuteControl)
        val overlayParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        overlayParams.addRule(CENTER_IN_PARENT)
        mOverlay = View(context)
        mOverlay!!.layoutParams = overlayParams
        mOverlay!!.setBackgroundColor(Color.TRANSPARENT)
        addView(mOverlay)
        val playButtonParams = LayoutParams(mControlSizePx, mControlSizePx)
        playButtonParams.addRule(CENTER_IN_PARENT)
        mPlayButton = ImageView(context)
        mPlayButton!!.layoutParams = playButtonParams
        mPlayButton!!.setImageDrawable(Drawables.NATIVE_PLAY.createDrawable(context))
        addView(mPlayButton)
        mIsInitialized = true
        updateViewState()
    }

    /**
     * Resets the view, removing all the OnClickListeners and setting the view to hide
     */
    fun reset() {
        setMode(Mode.IMAGE)
        setPlayButtonClickListener(null)
        setMuteControlClickListener(null)
        setVideoClickListener(null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measHeight = MeasureSpec.getSize(heightMeasureSpec)
        val curWidth = measuredWidth
        val curHeight = measuredHeight
        var finalWidth: Int
        finalWidth = if (widthMode == MeasureSpec.EXACTLY) {
            measWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Cap width at max width.
            Math.min(measWidth, curWidth)
        } else {
            // MeasWidth is meaningless. Stay with current width.
            curWidth
        }

        // Set height based on width + height constraints.
        var finalHeight = (ASPECT_MULTIPLIER_WIDTH_TO_HEIGHT * finalWidth).toInt()

        // Check if the layout is giving us bounds smaller than we want, conform to those if needed.
        if (heightMode == MeasureSpec.EXACTLY && measHeight < finalHeight) {
            finalHeight = measHeight
            finalWidth = (ASPECT_MULTIPLIER_HEIGHT_TO_WIDTH * finalHeight).toInt()
        }
        if (Math.abs(finalHeight - curHeight) >= 2
            || Math.abs(finalWidth - curWidth) >= 2
        ) {
            MoPubLog.log(
                SdkLogEvent.CUSTOM,
                String.format("Resetting mediaLayout size to w: %d h: %d", finalWidth, finalHeight)
            )
            layoutParams.width = finalWidth
            layoutParams.height = finalHeight
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setMainImageDrawable(drawable: Drawable) {
        Preconditions.checkNotNull(drawable)
        mMainImageView.setImageDrawable(drawable)
    }

    fun resetProgress() {
        if (mVideoProgress != null) {
            mVideoProgress!!.reset()
        }
    }

    fun updateProgress(progressTenthPercentage: Int) {
        if (mVideoProgress != null) {
            mVideoProgress!!.updateProgress(progressTenthPercentage)
        }
    }

    fun setMode(mode: Mode) {
        Preconditions.checkNotNull(mode)
        mMode = mode
        post { updateViewState() }
    }

    val mainImageView: ImageView?
        get() = mMainImageView

    fun setMuteControlClickListener(muteControlListener: OnClickListener?) {
        if (mMuteControl != null) {
            mMuteControl!!.setOnClickListener(muteControlListener)
        }
    }

    fun setPlayButtonClickListener(playButtonListener: OnClickListener?) {
        if (mPlayButton != null && mOverlay != null) {
            mOverlay!!.setOnClickListener(playButtonListener)
            mPlayButton!!.setOnClickListener(playButtonListener)
        }
    }

    fun setVideoClickListener(videoClickListener: OnClickListener?) {
        if (textureView != null) {
            textureView!!.setOnClickListener(videoClickListener)
        }
    }

    fun setMuteState(muteState: MuteState) {
        Preconditions.checkNotNull(muteState)
        if (muteState == mMuteState) {
            return
        }
        mMuteState = muteState
        if (mMuteControl != null) {
            when (mMuteState) {
                MuteState.MUTED -> mMuteControl!!.setImageDrawable(mMutedDrawable)
                MuteState.UNMUTED -> mMuteControl!!.setImageDrawable(mUnmutedDrawable)
                else -> mMuteControl!!.setImageDrawable(mUnmutedDrawable)
            }
        }
    }

    private fun updateViewState() {
        when (mMode) {
            Mode.IMAGE -> {
                setMainImageVisibility(VISIBLE)
                setLoadingSpinnerVisibility(INVISIBLE)
                setVideoControlVisibility(INVISIBLE)
                setPlayButtonVisibility(INVISIBLE)
            }
            Mode.LOADING -> {
                setMainImageVisibility(VISIBLE)
                setLoadingSpinnerVisibility(VISIBLE)
                setVideoControlVisibility(INVISIBLE)
                setPlayButtonVisibility(INVISIBLE)
            }
            Mode.BUFFERING -> {
                setMainImageVisibility(INVISIBLE)
                setLoadingSpinnerVisibility(VISIBLE)
                setVideoControlVisibility(VISIBLE)
                setPlayButtonVisibility(INVISIBLE)
                setMainImageVisibility(INVISIBLE)
                setLoadingSpinnerVisibility(INVISIBLE)
                setVideoControlVisibility(VISIBLE)
                setPlayButtonVisibility(INVISIBLE)
            }
            Mode.PLAYING -> {
                setMainImageVisibility(INVISIBLE)
                setLoadingSpinnerVisibility(INVISIBLE)
                setVideoControlVisibility(VISIBLE)
                setPlayButtonVisibility(INVISIBLE)
            }
            Mode.PAUSED -> {
                setMainImageVisibility(INVISIBLE)
                setLoadingSpinnerVisibility(INVISIBLE)
                setVideoControlVisibility(VISIBLE)
                setPlayButtonVisibility(VISIBLE)
            }
            Mode.FINISHED -> {
                setMainImageVisibility(VISIBLE)
                setLoadingSpinnerVisibility(INVISIBLE)
                setVideoControlVisibility(INVISIBLE)
                setPlayButtonVisibility(VISIBLE)
            }
            else -> {
            }
        }
    }

    private fun setMainImageVisibility(visibility: Int) {
        mMainImageView.visibility = visibility
    }

    private fun setLoadingSpinnerVisibility(visibility: Int) {
        if (mLoadingSpinner != null) {
            mLoadingSpinner!!.visibility = visibility
        }
        if (mTopGradient != null) {
            mTopGradient!!.visibility = visibility
        }
    }

    private fun setVideoControlVisibility(visibility: Int) {
        if (mBottomGradient != null) {
            mBottomGradient!!.visibility = visibility
        }
        if (mVideoProgress != null) {
            mVideoProgress!!.visibility = visibility
        }
        if (mMuteControl != null) {
            mMuteControl!!.visibility = visibility
        }
    }

    private fun setPlayButtonVisibility(visibility: Int) {
        if (mPlayButton != null && mOverlay != null) {
            mPlayButton!!.visibility = visibility
            mOverlay!!.visibility = visibility
        }
    }

    companion object {
        private const val GRADIENT_STRIP_HEIGHT_DIPS = 35
        private const val MUTE_SIZE_DIPS = 36
        private const val CONTROL_SIZE_DIPS = 40
        private const val PINNER_PADDING_DIPS = 10
        private const val ASPECT_MULTIPLIER_WIDTH_TO_HEIGHT = 9f / 16
        private const val ASPECT_MULTIPLIER_HEIGHT_TO_WIDTH = 16f / 9
    }

    // Constructors
    init {
        Preconditions.checkNotNull(context)
        mMuteState = MuteState.MUTED

        // Create and layout the main imageView and set its modes.
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.addRule(CENTER_IN_PARENT)
        mMainImageView = ImageView(context)
        mMainImageView.layoutParams = params
        mMainImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(mMainImageView)
        mControlSizePx = Dips.asIntPixels(CONTROL_SIZE_DIPS.toFloat(), context)
        mGradientStripHeightPx = Dips.asIntPixels(GRADIENT_STRIP_HEIGHT_DIPS.toFloat(), context)
        mMuteSizePx = Dips.asIntPixels(MUTE_SIZE_DIPS.toFloat(), context)
        mPaddingPx = Dips.asIntPixels(PINNER_PADDING_DIPS.toFloat(), context)
    }
}