package mrl.viewer;

public final class MrlPersonalData {

    public final static IViewerData VIEWER_DATA;

    public static final boolean DEBUG_MODE = false;

    static {
        if (DEBUG_MODE) {//调试状态
            VIEWER_DATA = new FullViewerData();
        } else {
            VIEWER_DATA = new EmptyViewerData();
        }
    }

}
