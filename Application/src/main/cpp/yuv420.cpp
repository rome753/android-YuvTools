#include <jni.h>
#include <string>

extern "C" JNIEXPORT void JNICALL
Java_cc_rome753_yuvtools_YUVTools_yv12ToNv21cpp
        (JNIEnv *env, jclass jcls, jbyteArray src_, jbyteArray dest_, jint w, jint h) {

    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jbyte *dest = env->GetByteArrayElements(dest_, NULL);
    jsize len = env->GetArrayLength(dest_);

    int pos = w * h;
    memcpy(dest,src,pos);
    int v = pos;
    int u = pos + (pos >> 2);
    while(pos < len) {
        dest[pos++] = src[v++];
        dest[pos++] = src[u++];
    }
    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(dest_, dest, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_cc_rome753_yuvtools_YUVTools_i420ToNv21cpp(JNIEnv *env, jclass type, jbyteArray src_,
                                                jbyteArray dest_, jint w, jint h) {
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jbyte *dest = env->GetByteArrayElements(dest_, NULL);
    jsize len = env->GetArrayLength(dest_);

    int pos = w * h;
    int u = pos;
    int v = pos + (pos >> 2);
    memcpy(dest,src,pos);
    while(pos < len) {
        dest[pos++] = src[v++];
        dest[pos++] = src[u++];
    }

    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(dest_, dest, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_cc_rome753_yuvtools_YUVTools_nv12ToNv21cpp(JNIEnv *env, jclass type, jbyteArray src_,
                                                jbyteArray dest_, jint w, jint h) {
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jbyte *dest = env->GetByteArrayElements(dest_, NULL);
    jsize len = env->GetArrayLength(dest_);

    int pos = w * h;
    int u = pos;
    int v = pos + (pos >> 2);
    memcpy(dest,src,pos);
    for(; pos < len; pos += 2) {
        dest[pos] = src[pos+1];
        dest[pos+1] = src[pos];
    }

    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(dest_, dest, 0);
}