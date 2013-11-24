#include "sunxi-tools-wrapper.h"
#include <android/log.h>

#define  LOG_TAG    "AWSE"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

JNIEXPORT jobjectArray JNICALL Java_com_llt_awse_FexUtils_compileFex
  (JNIEnv * je, jclass jc, jobject jo)
{

	return 0;
}

JNIEXPORT jstring JNICALL Java_com_llt_awse_FexUtils_decompileBin
  (JNIEnv * je, jclass jc, jobject jo)
{

	return (*je)->NewStringUTF(je, "_placeholder_");
}

