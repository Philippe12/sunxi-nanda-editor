#include "sunxi-tools-wrapper.h"

#include "./sunxi-tools/fexc.h"

#include <android/log.h>

#define  LOG_TAG    "AWSE.Native"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)


JNIEXPORT jobjectArray JNICALL Java_com_llt_awse_FexUtils_compileFex
  (JNIEnv * env , jclass class , jobject arg)
{

	return 0;
}

JNIEXPORT jstring JNICALL Java_com_llt_awse_FexUtils_decompileBin
  (JNIEnv * env, jclass class, jobject arg)
{
	struct script * pScript = script_new();

	if(!pScript)
	{
		LOGE("Cannot alloc memory for script");
		return 0;
	}

	const char * szFileName = (*env)->GetStringUTFChars(env, arg, NULL);

	if(script_parse(BIN_SCRIPT_FORMAT, filename[0], pScript))
		script_generate(FEX_SCRIPT_FORMAT, filename[1], pScript);


	script_delete(pScript);
	(*env)->ReleaseStringUTFChars(env, arg, szFileName);
	return env->NewStringUTF(je, "_placeholder_");
}

