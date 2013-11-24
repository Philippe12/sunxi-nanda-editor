LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libsunxi-tools
TARGET_ARCH		:= arm
TARGET_ARCH_ABI := armeabi-v7a
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS	:= -std=c99 -D_POSIX_C_SOURCE=200112L
LOCAL_SRC_FILES :=  stdio/open_memstream.c stdio/fopencookie.c stdio/fmemopen.c 

LOCAL_SRC_FILES +=  sunxi-tools-wrapper.c

LOCAL_SRC_FILES +=  sunxi-tools/script.c sunxi-tools/script_bin.c \
					sunxi-tools/script_fex.c sunxi-tools/fexc.c  \
					


include $(BUILD_SHARED_LIBRARY)