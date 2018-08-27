#include "MyLEXZ-Cutils/data_op/PrimitiveData.h"
#include "com_mylexz_utils_PrimitiveData.h"
#ifdef PRIMITIVEDATA_H
#ifdef _Included_com_mylexz_utils_PrimitiveData
// this struct for listing all PDATAs
typedef struct __listPDATA{
	PDATA *mPdata;
	int __desc;
	struct __listPDATA *ptr;
}lPDATA;
jint __getDescriptor(JNIEnv *, jobject);
void __setDescriptor(JNIEnv *, jobject, jint);
lPDATA *__insert(lPDATA *, lPDATA *);
lPDATA *__releaseNode(lPDATA *, int);
lPDATA *__createNode();
PDATA *__content(lPDATA *, int); 
int __generateDesc(lPDATA *);
lPDATA *__temp, *__current = NULL;
/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    open
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_open
  (JNIEnv *env, jobject thiz, jstring filename, jstring signature){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc != -1)return;
	  __temp = __createNode();
	  __desc = __generateDesc(__current);
	  __temp -> __desc = __desc;
	  __temp -> ptr = NULL;
	  const char *fnm = (*env)->GetStringUTFChars(env, filename, 0);
	  const char *snm = (*env)->GetStringUTFChars(env, signature, 0);
	  __temp -> mPdata = openf(fnm, snm);
	  (*env)->ReleaseStringUTFChars(env, filename, fnm);
	  (*env)->ReleaseStringUTFChars(env, signature, snm);
	  int err = getErrors(__temp -> mPdata);
	  if(err != 0) {
	  	__temp = __releaseNode(__temp, __desc);
		return;
	  }
	  __current = __insert(__current, __temp);
	  __setDescriptor(env, thiz, __desc);
	  return;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_close
  (JNIEnv *env, jobject thiz){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  __current = __releaseNode(__current, __desc);
	  __setDescriptor(env, thiz, -1);
	  return;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getErrors
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getErrors
  (JNIEnv *env, jobject thiz){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  return (jint) getErrors(mPdata);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getContentPosition
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getContentPosition__Ljava_lang_String_2Ljava_lang_String_2
  (JNIEnv *env, jobject thiz, jstring element, jstring data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  const char *dt = (*env)->GetStringUTFChars(env, data, 0);
	  jint pos = findPos(mPdata, elm, dt);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  (*env)->ReleaseStringUTFChars(env, data, dt);
	  return pos;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getContentPosition
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getContentPosition__Ljava_lang_String_2I
  (JNIEnv *env, jobject thiz, jstring element, jint data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  int dt = data;
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint pos = findPos(mPdata, elm, &dt);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return pos;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getContentPosition
 * Signature: (Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getContentPosition__Ljava_lang_String_2J
  (JNIEnv *env, jobject thiz, jstring element, jlong data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint pos = findPos(mPdata, elm, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return pos;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getContentPosition
 * Signature: (Ljava/lang/String;D)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getContentPosition__Ljava_lang_String_2D
  (JNIEnv *env, jobject thiz, jstring element, jdouble data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint pos = findPos(mPdata, elm, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return pos;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getContentPosition
 * Signature: (Ljava/lang/String;C)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getContentPosition__Ljava_lang_String_2C
  (JNIEnv *env, jobject thiz, jstring element, jchar data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  char dt = (char)data;
	  jint pos = findPos(mPdata, elm, &dt);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return pos;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getArrLength
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getArrLength
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint len = getArrLength(mPdata, elm);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return len;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addElement
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addElement__Ljava_lang_String_2Ljava_lang_String_2
  (JNIEnv *env, jobject thiz, jstring element, jstring data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  const char *dt = (*env)->GetStringUTFChars(env, data, 0);
	  addElem(mPdata, elm, STR, dt);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  (*env)->ReleaseStringUTFChars(env, data, dt);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addElement
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addElement__Ljava_lang_String_2I
  (JNIEnv *env, jobject thiz, jstring element, jint data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  addElem(mPdata, elm, INT, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addElement
 * Signature: (Ljava/lang/String;C)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addElement__Ljava_lang_String_2C
  (JNIEnv *env, jobject thiz, jstring element, jchar data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  char dt = (char)data;
	  addElem(mPdata, elm, CHR, &dt);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addElement
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addElement__Ljava_lang_String_2J
  (JNIEnv *env, jobject thiz, jstring element, jlong data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  addElem(mPdata, elm, LONG, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addElement
 * Signature: (Ljava/lang/String;D)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addElement__Ljava_lang_String_2D
  (JNIEnv *env, jobject thiz, jstring element, jdouble data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  addElem(mPdata, elm, DOUBLE, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    deleteElement
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_deleteElement
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  deleteElem(mPdata, elm);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }
/********/

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getStrElement
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mylexz_utils_PrimitiveData_getStrElement
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  const char *str = getElem(mPdata, elm);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return (*env)->NewStringUTF(env, str);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getIntElement
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getIntElement
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  int *result = (int *)getElem(mPdata, elm);
	  int data = *result;
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getLongElement
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_mylexz_utils_PrimitiveData_getLongElement
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jlong data = *((long long int *)getElem(mPdata, elm));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getDoubleElement
 * Signature: (Ljava/lang/String;)D
 */
JNIEXPORT jdouble JNICALL Java_com_mylexz_utils_PrimitiveData_getDoubleElement
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jdouble data = *((double *)getElem(mPdata, elm));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getCharElement
 * Signature: (Ljava/lang/String;)C
 */
JNIEXPORT jchar JNICALL Java_com_mylexz_utils_PrimitiveData_getCharElement
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return 0;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jchar data = *((char *)getElem(mPdata, elm));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setElement
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setElement__Ljava_lang_String_2Ljava_lang_String_2
  (JNIEnv *env, jobject thiz, jstring element, jstring data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  const char *dt = (*env)->GetStringUTFChars(env, data, 0);
	  setElem(mPdata, elm, dt);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  (*env)->ReleaseStringUTFChars(env, data, dt);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setElement
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setElement__Ljava_lang_String_2I
  (JNIEnv *env, jobject thiz, jstring element, jint data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setElem(mPdata, elm, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setElement
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setElement__Ljava_lang_String_2J
  (JNIEnv *env, jobject thiz, jstring element, jlong data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setElem(mPdata, elm, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);

  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setElement
 * Signature: (Ljava/lang/String;D)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setElement__Ljava_lang_String_2D
  (JNIEnv *env, jobject thiz, jstring element, jdouble data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setElem(mPdata, elm, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setElement
 * Signature: (Ljava/lang/String;C)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setElement__Ljava_lang_String_2C
  (JNIEnv *env, jobject thiz, jstring element, jchar data){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setElem(mPdata, elm, &data);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addArray
 * Signature: (Ljava/lang/String;[I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addArray__Ljava_lang_String_2_3I
  (JNIEnv *env, jobject thiz, jstring element, jintArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jint *a = (*env)->GetIntArrayElements(env, arr, 0);
	  addArr(mPdata, elm, INT, a, len);
	  (*env)->ReleaseIntArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addArray
 * Signature: (Ljava/lang/String;[J)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addArray__Ljava_lang_String_2_3J
  (JNIEnv *env, jobject thiz, jstring element, jlongArray arr)
  {
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jlong *a = (*env)->GetLongArrayElements(env, arr, 0);
	  addArr(mPdata, elm, LONG, a, len);
	  (*env)->ReleaseLongArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addArray
 * Signature: (Ljava/lang/String;[D)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addArray__Ljava_lang_String_2_3D
  (JNIEnv *env, jobject thiz, jstring element, jdoubleArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jdouble *a = (*env)->GetDoubleArrayElements(env, arr, 0);
	  addArr(mPdata, elm, DOUBLE, a, len);
	  (*env)->ReleaseDoubleArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addArray
 * Signature: (Ljava/lang/String;[C)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addArray__Ljava_lang_String_2_3C
  (JNIEnv *env, jobject thiz, jstring element, jcharArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jchar *a = (*env)->GetCharArrayElements(env, arr, 0);
	  addArr(mPdata, elm, CHR, a, len);
	  (*env)->ReleaseCharArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getAllIntContentInArr
 * Signature: (Ljava/lang/String;)[I
 */
JNIEXPORT jintArray JNICALL Java_com_mylexz_utils_PrimitiveData_getAllIntContentInArr
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint len = getArrLength(mPdata, elm);
	  jintArray arr = (*env)->NewIntArray(env, len);
	  if(arr == NULL)return NULL;
	  (*env)->SetIntArrayRegion(env, arr, 0, len, ((int *)getAllContentInArr(mPdata, elm)));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return arr;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getAllLongContentInArr
 * Signature: (Ljava/lang/String;)[J
 */
JNIEXPORT jlongArray JNICALL Java_com_mylexz_utils_PrimitiveData_getAllLongContentInArr
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint len = getArrLength(mPdata, elm);
	  jlongArray arr = (*env)->NewLongArray(env, len);
	  if(arr == NULL)return NULL;
	  (*env)->SetLongArrayRegion(env, arr, 0, len, ((long long int *)getAllContentInArr(mPdata, elm)));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return arr;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getAllDoubleContentInArr
 * Signature: (Ljava/lang/String;)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_mylexz_utils_PrimitiveData_getAllDoubleContentInArr
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint len = getArrLength(mPdata, elm);
	  jdoubleArray arr = (*env)->NewDoubleArray(env, len);
	  if(arr == NULL)return NULL;
	  (*env)->SetDoubleArrayRegion(env, arr, 0, len, ((double *)getAllContentInArr(mPdata, elm)));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return arr;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getAllCharContentInArr
 * Signature: (Ljava/lang/String;)[C
 */
JNIEXPORT jcharArray JNICALL Java_com_mylexz_utils_PrimitiveData_getAllCharContentInArr
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint len = getArrLength(mPdata, elm);
	  jcharArray arr = (*env)->NewCharArray(env, len);
	  if(arr == NULL)return NULL;
	  (*env)->SetCharArrayRegion(env, arr, 0, len, ((char *)getAllContentInArr(mPdata, elm)));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return arr;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getIntContentArrAtPos
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getIntContentArrAtPos
  (JNIEnv *env, jobject thiz, jstring element, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jint data = *((int *)getContentArrAtPos(mPdata, elm, pos));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getLongContentArrAtPos
 * Signature: (Ljava/lang/String;I)J
 */
JNIEXPORT jlong JNICALL Java_com_mylexz_utils_PrimitiveData_getLongContentArrAtPos
  (JNIEnv *env, jobject thiz, jstring element, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jlong data = *((long long int *)getContentArrAtPos(mPdata, elm, pos));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getDoubleContentArrAtPos
 * Signature: (Ljava/lang/String;I)D
 */
JNIEXPORT jdouble JNICALL Java_com_mylexz_utils_PrimitiveData_getDoubleContentArrAtPos
  (JNIEnv *env, jobject thiz, jstring element, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return __desc;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jdouble data = *((double *)getContentArrAtPos(mPdata, elm, pos));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getCharContentArrAtPos
 * Signature: (Ljava/lang/String;I)C
 */
JNIEXPORT jchar JNICALL Java_com_mylexz_utils_PrimitiveData_getCharContentArrAtPos
  (JNIEnv *env, jobject thiz, jstring element, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return 0;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jchar data = *((char *)getContentArrAtPos(mPdata, elm, pos));
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return data;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    appendArray
 * Signature: (Ljava/lang/String;[I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_appendArray__Ljava_lang_String_2_3I
  (JNIEnv *env, jobject thiz, jstring element, jintArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jint *a = (*env)->GetIntArrayElements(env, arr, 0);
	  appendArr(mPdata, elm, a, len);
	  (*env)->ReleaseIntArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    appendArray
 * Signature: (Ljava/lang/String;[J)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_appendArray__Ljava_lang_String_2_3J
  (JNIEnv *env, jobject thiz, jstring element, jlongArray arr)
  {
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jlong *a = (*env)->GetLongArrayElements(env, arr, 0);
	  appendArr(mPdata, elm, a, len);
	  (*env)->ReleaseLongArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    appendArray
 * Signature: (Ljava/lang/String;[D)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_appendArray__Ljava_lang_String_2_3D
  (JNIEnv *env, jobject thiz, jstring element, jdoubleArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jdouble *a = (*env)->GetDoubleArrayElements(env, arr, 0);
	  appendArr(mPdata, elm, a, len);
	  (*env)->ReleaseDoubleArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm); 
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    appendArray
 * Signature: (Ljava/lang/String;[C)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_appendArray__Ljava_lang_String_2_3C
  (JNIEnv *env, jobject thiz, jstring element, jcharArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jchar *a = (*env)->GetCharArrayElements(env, arr, 0);
	  appendArr(mPdata, elm, a, len);
	  (*env)->ReleaseCharArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setArray
 * Signature: (Ljava/lang/String;[I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setArray__Ljava_lang_String_2_3I
  (JNIEnv *env, jobject thiz, jstring element, jintArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jint *a = (*env)->GetIntArrayElements(env, arr, 0);
	  setArr(mPdata, elm, a, len);
	  (*env)->ReleaseIntArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setArray
 * Signature: (Ljava/lang/String;[J)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setArray__Ljava_lang_String_2_3J
  (JNIEnv *env, jobject thiz, jstring element, jlongArray arr)
  {
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jlong *a = (*env)->GetLongArrayElements(env, arr, 0);
	  setArr(mPdata, elm, a, len);
	  (*env)->ReleaseLongArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setArray
 * Signature: (Ljava/lang/String;[D)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setArray__Ljava_lang_String_2_3D
  (JNIEnv *env, jobject thiz, jstring element, jdoubleArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jdouble *a = (*env)->GetDoubleArrayElements(env, arr, 0);
	  setArr(mPdata, elm, a, len);
	  (*env)->ReleaseDoubleArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }


/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setArray
 * Signature: (Ljava/lang/String;[C)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setArray__Ljava_lang_String_2_3C
  (JNIEnv *env, jobject thiz, jstring element, jcharArray arr){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jsize len = (*env)->GetArrayLength(env, arr);
	  jchar *a = (*env)->GetCharArrayElements(env, arr, 0);
	  setArr(mPdata, elm, a, len);
	  (*env)->ReleaseCharArrayElements(env, arr, a, 0);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setDataAtArrPos
 * Signature: (Ljava/lang/String;II)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setDataAtArrPos__Ljava_lang_String_2II
  (JNIEnv *env, jobject thiz, jstring element, jint data, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  int dt = (int) data;
	  setDataAtPosArr(mPdata, elm, INT, &dt, pos);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setDataAtArrPos
 * Signature: (Ljava/lang/String;JI)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setDataAtArrPos__Ljava_lang_String_2JI
  (JNIEnv *env, jobject thiz, jstring element, jlong data, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setDataAtPosArr(mPdata, elm, LONG, &data, pos);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setDataAtArrPos
 * Signature: (Ljava/lang/String;DI)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setDataAtArrPos__Ljava_lang_String_2DI
  (JNIEnv *env, jobject thiz, jstring element, jdouble data, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setDataAtPosArr(mPdata, elm, DOUBLE, &data, pos);
	  (*env)->ReleaseStringUTFChars(env, element, elm);

  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setDataAtArrPos
 * Signature: (Ljava/lang/String;CI)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setDataAtArrPos__Ljava_lang_String_2CI
  (JNIEnv *env, jobject thiz, jstring element, jchar data, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  setDataAtPosArr(mPdata, elm, CHR, &data, pos);
	  (*env)->ReleaseStringUTFChars(env, element, elm);

  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    isArrayType
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mylexz_utils_PrimitiveData_isArrayType
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return JNI_FALSE;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jboolean bl = getTPElem(mPdata, elm) == ID_ARR ? 1 : 0;
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return bl;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getElementDataType
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mylexz_utils_PrimitiveData_getElementDataType
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return 0;
	  PDATA *pdata = __content(__current, __desc);
	  const char *elem = (*env)->GetStringUTFChars(env, element, 0);
	  jint d = getIDElem(pdata, elem);
	  (*env)->ReleaseStringUTFChars(env, element, elem);
	  return d;
  }
  
  /*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    addStrArray
 * Signature: (Ljava/lang/String;[Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_addStrArray
  (JNIEnv *env, jobject thiz, jstring element, jobjectArray arrStr, jint size){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *pdata = __content(__current, __desc);
	  const char *elem = (*env)->GetStringUTFChars(env, element, 0);
	  if(((jint)(*env)->GetArrayLength(env, arrStr)) < size){
	  	(*env)->ReleaseStringUTFChars(env, element, elem);
	  	return;
	  }
	  extern int checkExistsElem(char *, FILE *, char *, off_t);
	  extern void __passToFile(char *, FILE *, int, int);
	  
	// Copying pointers from pdata to temp and rdfiles
	  FILE* rd = pdata->rd;
	  char* temp = pdata->temp;
	  int* errnum = &(pdata->errnum);
	  off_t offset = ftell(rd);
	  if(checkExistsElem(temp, rd, elem, offset)){
		  fseek(rd, offset, 0);
		  *errnum = -2;
		  (*env)->ReleaseStringUTFChars(env, element, elem);
		  return;
	}
	// writing first content
	fprintf(rd, "%c%c%s%c", ID_ARR, STR, elem, BUKA);
	// writing content array
	size_t x = 0;
	for (; x < size; x++){
		jstring a = (jstring) (*env)->GetObjectArrayElement(env, arrStr, x);
		const char *n = (*env)->GetStringUTFChars(env, a, 0);
		strcpy(temp, n);
		edStr(temp, ENC);
		__passToFile(temp, rd, x, size);
		(*env)->ReleaseStringUTFChars(env, a, n);
	}
    fseek(rd, offset, 0);
	*errnum = 0;
	(*env)->ReleaseStringUTFChars(env, element, elem);
	return;
	  
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getStrAtPos
 * Signature: (Ljava/lang/String;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mylexz_utils_PrimitiveData_getStrAtPos
  (JNIEnv *env, jobject thiz, jstring element, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  char *s = getStrAtPos(mPdata, elm, (int)pos);
	  jstring a = (s == NULL)?NULL:(*env)->NewStringUTF(env, s);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return a;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    getStrArray
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_mylexz_utils_PrimitiveData_getStrArray
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  size_t len = getArrLength(mPdata, elm);
	  jobjectArray arr = NULL;
	  if(len != 0){
		  arr = (*env)->NewObjectArray(env, len, (*env)->FindClass(env, "java/lang/String"), NULL);
		  size_t x = 0;
		  for(; x < len; x++){
			  char *str = readStringPoint(mPdata, elm);
			  if(str == NULL)break;
			  (*env)->SetObjectArrayElement(env, arr, x, (*env)->NewStringUTF(env, str));
		  }
		  resetPoint(mPdata);
	  }
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return arr;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    appendStrArray
 * Signature: (Ljava/lang/String;[Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_appendStrArray
  (JNIEnv *env, jobject thiz, jstring element, jobjectArray arr, jint len){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *pdata = __content(__current, __desc);
	  const char *elem = (*env)->GetStringUTFChars(env, element, 0);
	  if(((jint)(*env)->GetArrayLength(env, arr)) < len){
	  	(*env)->ReleaseStringUTFChars(env, element, elem);
	  	return;
	  }
	  extern int checkExistsElem(char *, FILE *, char *, off_t);
	  extern void __passToFile(char *, FILE *, int, int);
	  
	  FILE* rd = pdata->rd;
	  char* temp = pdata->temp;
	  int* errnum = &(pdata->errnum);
	  char* tempFile = pdata->tempFile;
	  off_t offset = ftell(rd);
	  if(!checkExistsElem(temp, rd, elem, offset)) {
		  fseek(rd, offset, 0);
		  *errnum = -2;
	  	  (*env)->ReleaseStringUTFChars(env, element, elem);
		  return;
	  }
	  char ID = getc(rd);
	  char TP = getc(rd);
	  if (ID != ID_ARR || TP != STR){
		  fseek(rd, offset, 0);
		  *errnum = -3;
		  (*env)->ReleaseStringUTFChars(env, element, elem);
		  return;
	  }
	  register int y;
	  while ((y = getc(rd))!=TUTUP);
	  off_t pos1 = ftell(rd)-1;
	  char n[32];
	  sprintf(n, "%s.tmp", tempFile);
	  FILE* rv = fopen(n, "w+");
	  fseek(rd, pos1, 0);
	  size_t x = 0;
	  for(; x<len; x++){
		  jstring a = (jstring) (*env)->GetObjectArrayElement(env, arr, x);
		  const char *n = (*env)->GetStringUTFChars(env, a, 0);
		  strcpy(temp, n);
		  edStr(temp, ENC);
		  fprintf(rv, "%c%s", ARR_SEPARATOR,temp);
		  (*env)->ReleaseStringUTFChars(env, a, n);
	  }
	  while((y = getc(rd))!=-1) putc(y, rv);
	  fseek(rd, pos1, 0);
	  fseek(rv, 0, 0);
	  while((y = getc(rv))!=-1)putc(y, rd);
	  fclose(rv);
	  remove(n);
	  fseek(rd, offset, 0);
	  *errnum = 0;
	  (*env)->ReleaseStringUTFChars(env, element, elem);
	  return ;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setStrArray
 * Signature: (Ljava/lang/String;[Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setStrArray
  (JNIEnv *env, jobject thiz, jstring element, jobjectArray arr, jint len){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *pdata = __content(__current, __desc);
	  const char *elem = (*env)->GetStringUTFChars(env, element, 0);
	  if(((jint)(*env)->GetArrayLength(env, arr)) < len){
	  	(*env)->ReleaseStringUTFChars(env, element, elem);
	  	return;
	  }
	  extern int checkExistsElem(char *, FILE *, char *, off_t);
	  
	  FILE* rd = pdata->rd;
	  char* temp = pdata->temp;
	  int* errnum = &(pdata->errnum);
	  char* tempFile = pdata->tempFile;
	  off_t offset = ftell(rd);
	  if(!checkExistsElem(temp, rd, elem, offset)) {
		  fseek(rd, offset, 0);
		  *errnum = -2;
		  (*env)->ReleaseStringUTFChars(env, element, elem);
		  return;
	  }
	  char ID = getc(rd);
	  char TP = getc(rd);
	  if (ID != ID_ARR || TP != STR){
		  fseek(rd, offset, 0);
		  *errnum = -3;
		  (*env)->ReleaseStringUTFChars(env, element, elem);
		  return;
	  }
	  register int y;
	  while ((y = getc(rd))!=BUKA);
	  off_t pos1 = ftell(rd);
	  while ((y = getc(rd))!=TUTUP);
	  off_t pos2 = ftell(rd);
	  char n[32];
	  sprintf(n, "%s.tmp", tempFile);
	  FILE* rv = fopen(n, "w+");
	  fseek(rd, 0,0);
	  off_t t = 0;
	  while(((y = getc(rd))!= -1)&&(t++ < pos1))putc(y, rv);
	  size_t x = 0;
	  for(; x<len; x++){
		  jstring a = (jstring) (*env)->GetObjectArrayElement(env, arr, x);
		  const char *n = (*env)->GetStringUTFChars(env, a, 0);
		  strcpy(temp, n);
		  edStr(temp, ENC);
		  fprintf(rv, "%s%c",  temp, ARR_SEPARATOR);
	  }
	  t  = ftell(rv);
	  fseek(rv, t-1, 0);
	  fseek(rd, pos2-1, 0);
	  while(((y = getc(rd))!= -1))putc(y, rv);
	  fclose(rd);
	  fclose(rv);
	//printf("hello");
	  rename(n , tempFile);
	  rd = fopen(tempFile, "r+");
	  fseek(rd, offset, 0);
	  *errnum = 0;
	  (*env)->ReleaseStringUTFChars(env, element, elem);
	  return;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    setStrArrAtPosition
 * Signature: (Ljava/lang/String;[Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_setStrArrAtPosition
  (JNIEnv *env, jobject thiz, jstring element, jstring str, jint pos){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  const char *s = (*env)->GetStringUTFChars(env, str, 0);
	  setStrArrAtPos(mPdata, elm, s, pos);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  (*env)->ReleaseStringUTFChars(env, str, s);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    readString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mylexz_utils_PrimitiveData_readString
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return NULL;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  char *str = readStringPoint(mPdata, elm);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return (str == NULL)? NULL: (*env)->NewStringUTF(env, str);
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_mylexz_utils_PrimitiveData_reset
  (JNIEnv *env, jobject thiz){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return;
	  PDATA *mPdata = __content(__current, __desc);
	  resetPoint(mPdata);
	  return;
  }

/*
 * Class:     com_mylexz_utils_PrimitiveData
 * Method:    isElementExists
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_mylexz_utils_PrimitiveData_isElementExists
  (JNIEnv *env, jobject thiz, jstring element){
	  jint __desc = __getDescriptor(env, thiz);
	  if(__desc == -1)return 0;
	  PDATA *mPdata = __content(__current, __desc);
	  const char *elm = (*env)->GetStringUTFChars(env, element, 0);
	  jboolean b = isElemExists(mPdata, elm);
	  (*env)->ReleaseStringUTFChars(env, element, elm);
	  return b;
  }

  /******/
  
jint __getDescriptor(JNIEnv *env, jobject thiz){
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID fid = (*env)->GetFieldID(env, cls, "__desc__", "I");
	return (*env)->GetIntField(env, thiz, fid);
	 
}
void __setDescriptor(JNIEnv *env, jobject thiz, jint val){
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID fid = (*env)->GetFieldID(env, cls, "__desc__", "I");
	(*env)->SetIntField(env, thiz, fid, val);
	 
}
jobject getSTR(JNIEnv *env, jobject thiz, const char *s){
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID fid = (*env)->GetFieldID(env, cls, s, "Ljava/lang/String;");
	
}
lPDATA *__insert(lPDATA *curr, lPDATA *tmp){
	if(!curr)return tmp;
	curr -> ptr = __insert(tmp, curr -> ptr);
}
lPDATA *__releaseNode(lPDATA *curr, int __desc){
	lPDATA *tmp;
	if(!curr) return curr;
	if(curr -> __desc == __desc)
	{
		closef((curr->mPdata));
		tmp = curr;
		curr = curr->ptr;
		free(tmp);
		return(curr);
	}
	curr -> ptr = __releaseNode(curr -> ptr, __desc);
	return(curr);
}
lPDATA *__createNode(){
	return (lPDATA *) malloc(sizeof(lPDATA));
}
PDATA *__content(lPDATA *curr, int __desc){
	if(__desc == -1)return NULL;
	PDATA *addr = NULL;
	while(curr){
		if(curr -> __desc == __desc){
			addr = (curr -> mPdata);
			break;
		}
		curr = curr -> ptr;
	}
	return addr;
}
int __generateDesc(lPDATA *curr){
	if(curr == NULL)return 0;
	int __desc;
	while(curr){
		__desc = curr -> __desc;
		curr = curr -> ptr;
	}
	return ++__desc;
}
#endif /* _Included_com_mylexz_utils_PrimitiveData */
#endif /* PRIMITIVEDATA_H */
