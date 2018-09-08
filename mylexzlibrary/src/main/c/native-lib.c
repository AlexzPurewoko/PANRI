#include <jni.h>
#include <stdio.h>
JNIEXPORT jstring JNICALL Java_com_mylexz_utils_CobaClass_hello(JNIEnv* env, jobject thiz){
    return (*env) -> NewStringUTF(env, "Hello WORLD");
}

