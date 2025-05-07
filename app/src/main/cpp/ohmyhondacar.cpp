#include <jni.h>
#include <android/log.h>
#include <cstdio>
#include <cstdlib>
#include <cstdint>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_dicklight_ohmyhondacar_util_ScreenShotUtil_captureScreen(JNIEnv *env, jclass clazz,
                                                                  jint width, jint height) {
    // TODO: implement captureScreen()
    int pixelCount = width * height;
    int bufferSize = pixelCount * 4;
    uint8_t *buffer = new uint8_t[bufferSize];

    FILE *fp = popen("su -c screencap", "r");
    if (!fp){
        delete[] buffer;
        return nullptr;
    }

    int totalRead = 0;
    while(totalRead < bufferSize){
        int n = fread(buffer + totalRead, 1, bufferSize - totalRead, fp);
        if (n <= 0)break;
        totalRead += n;
    }

    pclose(fp);

    if (totalRead < bufferSize){
        delete[] buffer;
        return nullptr;
    }

    jbyteArray result = env->NewByteArray(bufferSize);
    env->SetByteArrayRegion(result, 0, bufferSize, reinterpret_cast<const jbyte*>(buffer));
    delete[] buffer;
    return result;
}