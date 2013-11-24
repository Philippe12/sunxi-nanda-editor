LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := sunxi-tools
TARGET_ARCH		:= arm
TARGET_ARCH_ABI := armeabi-v7a
LOCAL_SRC_FILES :=  ./sunxi-tools/script.c ./sunxi-tools/script_bin.c \
					./sunxi-tools/script_fex.c \
				      sunxi-tools-wrapper.c
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS	:= -std=c99 -D_POSIX_C_SOURCE=200112L

include $(BUILD_SHARED_LIBRARY)