package com.doubleclick.MyFacebook.groupVideoCall.openvcall;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.doubleclick.MyFacebook.GlobalApplication;
import com.doubleclick.MyFacebook.R;
import com.doubleclick.MyFacebook.groupVideoCall.openvcall.model.AGEventHandler;
import com.doubleclick.MyFacebook.groupVideoCall.openvcall.model.CurrentUserSettings;
import com.doubleclick.MyFacebook.groupVideoCall.openvcall.model.EngineConfig;
import com.doubleclick.MyFacebook.groupVideoCall.openvcall.model.MyEngineEventHandler;
import com.doubleclick.MyFacebook.groupVoiceCall.openacall.model.WorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

@SuppressWarnings("ALL")
public class AGApplicationVideo extends GlobalApplication {
    private final CurrentUserSettings mVideoSettings = new CurrentUserSettings();

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private RtcEngine mRtcEngine;
    private EngineConfig mConfig;
    private MyEngineEventHandler mEventHandler;

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public EngineConfig config() {
        return mConfig;
    }

    public CurrentUserSettings userSettings() {
        return mVideoSettings;
    }

    public void addEventHandler(AGEventHandler handler) {
        mEventHandler.addEventHandler(handler);
    }

    public void remoteEventHandler(AGEventHandler handler) {
        mEventHandler.removeEventHandler(handler);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createRtcEngine();
    }

    private void createRtcEngine() {
        Context context = getApplicationContext();
        String appId = context.getString(R.string.agora_app_id);
        if (TextUtils.isEmpty(appId)) {
            throw new RuntimeException("NEED TO use your App ID, get your own ID at https:dashboard.agora.io/");
        }

        mEventHandler = new MyEngineEventHandler();
        try {

            mRtcEngine = RtcEngine.create(context, appId, mEventHandler);
        } catch (Exception e) {
            log.error(Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        /*
          Sets the channel profile of the Agora RtcEngine.
          The Agora RtcEngine differentiates channel profiles and applies different optimization
          algorithms accordingly. For example, it prioritizes smoothness and low latency for a
          video call, and prioritizes video quality for a video broadcast.
         */
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        mRtcEngine.enableVideo();
        /*
          Enables the onAudioVolumeIndication callback at a set time interval to report on which
          users are speaking and the speakers' volume.
          Once this method is enabled, the SDK returns the volume indication in the
          onAudioVolumeIndication callback at the set time interval, regardless of whether any user
          is speaking in the channel.
         */
        mRtcEngine.enableAudioVolumeIndication(200, 3, false);

        mConfig = new EngineConfig();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private WorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }


    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }


    public static final com.doubleclick.MyFacebook.groupVoiceCall.openacall.model.CurrentUserSettings mAudioSettings = new com.doubleclick.MyFacebook.groupVoiceCall.openacall.model.CurrentUserSettings();

}
