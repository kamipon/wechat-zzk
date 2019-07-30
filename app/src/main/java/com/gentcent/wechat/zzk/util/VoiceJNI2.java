package com.gentcent.wechat.zzk.util;

public class VoiceJNI2 {
    public static native int amr_to_mp3(String str, String str2);

    public static native int ffmpegCmd(String[] strArr);

    public static native int mp3_to_amr(String str, String str2);

    public static native int pcm_to_amr(String str, String str2, int i);

    static {
        System.loadLibrary("silk2mp3");
    }

    public static String[] transformAudio(String str, String str2) {
        return String.format("ffmpeg -i %s -f s16le -acodec pcm_s16le -ac 1 -ar 24000 %s", str, str2).split(" ");
    }
}
