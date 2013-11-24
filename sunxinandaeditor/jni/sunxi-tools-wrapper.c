#include "sunxi-tools-wrapper.h"

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <libgen.h>

#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>

#include <android/log.h>

#include "sunxi-tools/fexc.h"
#include "stdio/extrastdio.h"

#define  LOG_TAG    "AWSE.Native"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)

JNIEXPORT jobjectArray JNICALL Java_com_llt_awse_FexUtils_compileFex
  (JNIEnv * env , jclass jclass , jobject arg)
{
	return 0;
}

JNIEXPORT jbyteArray JNICALL Java_com_llt_awse_FexUtils_decompileBin
  (JNIEnv * env, jclass jc, jbyteArray bin, jint bin_size)
{
	struct script * pScript = script_new();
	FILE* stream = 0;
	char* final_out = 0;
	jbyteArray result = 0;

	if(!pScript)
	{
		LOGE("Cannot allocate memory for script!");
		return 0;
	}

    jbyte* buffer = (*env)->GetByteArrayElements(env, bin, NULL);
    char* inputBytes = malloc((int)bin_size * sizeof(char));

    if(!inputBytes)
    {
    	LOGE("Cannot allocate memory for buffer!");
    	script_delete(pScript);
    	return 0;
    }

    memcpy(inputBytes, buffer, (int)bin_size);
	(*env)->ReleaseByteArrayElements(env, bin, buffer, JNI_ABORT);


	if(script_decompile_bin(inputBytes, bin_size, "script.bin", pScript))
	{
		//Assume that decompiled file is not bigger than compiled

		stream = fmemopen (inputBytes, bin_size, "r+");
		if(stream)
			if(script_generate_fex(stream, NULL, pScript))
			{
				int final_size = ftell (stream);
				rewind(stream);
				char * final_out = malloc((int)final_size * sizeof(char));
				if(!final_out)
				{
					LOGE("Cannot allocate memory for final buffer!");
					script_delete(pScript);
					fclose(stream);
					free(inputBytes);
				}
				fread(final_out,1, final_size, stream);
				//LOGE("DATA:%s", final_out);
				result = (*env)->NewByteArray(env, final_size);
				(*env)->SetByteArrayRegion(env, result, 0, final_size, (jbyte*)final_out);

			}
	}

	script_delete(pScript);
	fclose(stream);
	free(inputBytes);
	free(final_out);
	return result;
}

