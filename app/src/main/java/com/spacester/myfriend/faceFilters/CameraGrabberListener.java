package com.spacester.myfriend.faceFilters;

@SuppressWarnings("ALL")
public interface CameraGrabberListener {
    void onCameraInitialized();
    void onCameraError(String errorMsg);
}
