#include "__common.h"

int throw_new_RuntimeException(JNIEnv * env, const char* message) {
    jclass RuntimeException = (*env)->FindClass(env, "java/lang/RuntimeException");
    return (*env)->ThrowNew(env, RuntimeException, message);
}
