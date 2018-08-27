/*
 * Copyright (C) 2017 @KOPI Alexzander Purwoko Widiantoro
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*
  * Header File included for important function
  */
#include <stdio.h>
#include <jni.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/stat.h>
#include <android/log.h>
#include "MyLEXZ-Cutils/component.h"
/*******************************/

/* CONSTANT EXPRESSION */
static const jint pemisah = 0x02,
				  last 	  = 0x01,
				  GID     = 0x08,
				  TID 	  = 0x0f;
/*
* Reference Variables
* used for storing integer value into memory
* and saving them
*/
static jobject integerReference;
/******/
static char sig[] = "MyLogger\0";
/*
* Convert chacacters into lowerCase
*/

/*********** Function For encrypt and decrypt ************/
int estr(int chr){
	return ~(~(chr << 1) + ('a' >> 2));
}
int dstr(int chr){
	return (~(~chr + ~('a'>>2))) >> 1;
}
int enm(int d){
	return ( ~ ( ~ ( d << 1 ) + ~0 + ~('0' >> 3)) + ((d - '0')+(1<<3)<<3))-(5 << 1);
	
}
int dnum(int e){
	return (((e + (5 << 1))- (1 << 3)) / ( 5 << 1)) + (1 << 5);//-(5 << 1);
}
/***********************************/
/************** LOG PRIORITY *****************/
enum PRIORITY {
	ERRORS 	= 0x0000a,
	INFO	= 0x0000b,
	DEBUG 	= 0x0000c,
	VERBOSE = 0x0000d,
	WARNING = 0x0000e,
	FAILURE = 0x0000f
};

/************
 * IMPORTANT FUNCTION !!!!!
 *
 */
/*
 * Time and Date structure table function
 */
struct tp {
	jint millis;
	jint sec;
	jint min;
	jint hours;
};
struct date {
	jint day;
	jint month;
	jint years;
};
/* Function of collecting Time and date */
void check(jint *tochck, jint val, jint *hchk){
	while((*tochck)> val){
		(*tochck )-=(val+1);
		(*hchk)++;
	}
}
void totime(unsigned long int  millis, struct tp *v){
	v->millis = millis % 1000;
	millis/=1000;
	v->sec = millis%100;
	millis-=v->sec;
	millis/=60;
	v->min = millis%60;
	v->hours = millis/60;
	// arg check for time
	//check minutes
	check(&(v->sec), 59, &(v->min));
	//check hours
	check(&(v->min), 59, &(v->hours));
}
void todate(jlong date, struct date *x){
	x->years = date%10000;
	date/=10000;
	x->month = date%100;
	date/=100;
	x->day = date%100;
	date/=100;
}

jobject jintToObject(JNIEnv* env, jint a){
	jclass cls = (*env)->FindClass(env, "java/lang/Integer");
	jmethodID method = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
	return (*env)->NewObject(env, cls, method, a);
	
}
jint toInt(JNIEnv* env, jobject b){
	jmethodID id = (*env)->GetMethodID(env, (*env)->FindClass(env, "java/lang/Integer"), "intValue", "()I");
	return (*env)->CallIntMethod(env, b, id);
}
jobject getObjFromArr(JNIEnv* env, jobjectArray arr, jint pos){
	return (*env)->GetObjectArrayElement(env, arr, pos);
}
void setObjArr(JNIEnv* env, jobjectArray s, jint pos, jobject obj){
	return (*env)->SetObjectArrayElement(env, s, pos, obj);
}
jlong atol64(const char s[]){
	int x = 0;
	jlong b = 0;
	for(;s[x] != '\0'; x++)
	    b = (b +( s[x] - '0')) * 10;
	return b/10;
}

void mencobaRef(JNIEnv* env){
	jint a = toInt(env, (*env)->GetObjectArrayElement(env, integerReference, 0));
	jint b = toInt(env, (*env)->GetObjectArrayElement(env, integerReference, 1));
	__android_log_print(ANDROID_LOG_INFO, "check", "Array value %d %d", a, b);
}

void ReaderLog(JNIEnv* env, FILE* read, char temp[], jint chr, jobjectArray arr, jint* pindex, jint* hasNull, jint* offset, jint* deleteAfterCall){
	jint d = 0;
	jint index = *pindex;
	index+=2;
	getc(read);
	(*env)->SetObjectArrayElement(env, arr, d++, jintToObject(env, chr));
			
	//getting TAG and packageName values
	jint x = 0;
	while((chr = getc(read)) != pemisah)
		temp[x++] = dstr(chr);
	temp[x++] = ' ';
	while((chr = getc(read)) != pemisah)
		temp[x++] = dstr(chr);
	temp[x] = '\0';
	__android_log_print(ANDROID_LOG_INFO, "Check", "value of tag %s", temp);
	index+=x;index++;
	x = 0;
	(*env)->SetObjectArrayElement(env, arr, d++, (*env)->NewStringUTF(env, temp));
	//Getting date
	while((chr = getc(read))!= pemisah)temp[x++] = dnum(chr);
		temp[x] = '\0';
	index+=x;index++;
	struct date myDate;
	todate(atol64(temp), &myDate);
	sprintf(temp, "%d/%d/%d", myDate.day, myDate.month, myDate.years);
	(*env)->SetObjectArrayElement(env, arr, d++, (*env)->NewStringUTF(env, temp));
	x = 0;
	//Getting Time
	while((chr = getc(read))!= pemisah) temp[x++] = dnum(chr);
		temp[x] = '\0';
	index+=x;index++;
	struct tp myTime;
	totime(atol64(temp), &myTime);
	sprintf(temp, "%d:%d:%d.%d\0", myTime.hours, myTime.min, myTime.sec, myTime.millis);
	(*env)->SetObjectArrayElement(env, arr, d++, (*env)->NewStringUTF(env, temp));
	x = 0;
	//Getting content
	while((chr = getc(read))!=last)temp[x++] = dstr(chr);
	temp[x] = '\0';
	index+=x;index+=2;
	(*env)->SetObjectArrayElement(env, arr, d++, (*env)->NewStringUTF(env, temp));
	x = 0;
	if((chr = getc(read)) == '\0')
		*hasNull = 1;
	*offset += index;
	//saving value to reference
	if(*deleteAfterCall){
		if(integerReference!=NULL)
			(*env)->DeleteGlobalRef(env, integerReference);
			integerReference = NULL;
		}
		else {
			if(integerReference == NULL){
				jobjectArray tt = (*env)->NewObjectArray(env, 2, (*env)->FindClass(env, "java/lang/Object"), NULL);
				(*env)->SetObjectArrayElement(env, tt, 0, jintToObject(env, *offset));//Offset file
				(*env)->SetObjectArrayElement(env, tt, 1, jintToObject(env, *hasNull));//hasNull
				integerReference = (*env)->NewGlobalRef(env, tt);
			}
			else{
				(*env)->SetObjectArrayElement(env, integerReference, 0, jintToObject(env, *offset));//Offset file
				(*env)->SetObjectArrayElement(env, 	integerReference, 1, jintToObject(env, *hasNull));//hasNull	
			}
		mencobaRef(env);
	}
	*pindex = index;
}






JNIEXPORT void JNICALL Java_com_mylexz_utils_Logger_toNonNull(JNIEnv* env, jobject obj){
	if(integerReference!=NULL)
		(*env)->SetObjectArrayElement(env, integerReference, 1, jintToObject(env, 0));
}
JNIEXPORT jint JNICALL Java_com_mylexz_utils_Logger_hasNull(JNIEnv* env, jobject obj){
	if(integerReference!=NULL){
		jint a = toInt(env, getObjFromArr(env, integerReference, 1));
		return a;
	}
	return 0;
}
JNIEXPORT void JNICALL Java_com_mylexz_utils_Logger_reset(JNIEnv* env, jobject obj)
{
	if(integerReference!=NULL)
		(*env)->DeleteGlobalRef(env, integerReference);
	integerReference = NULL;
}
JNIEXPORT jobjectArray JNICALL Java_com_mylexz_utils_Logger_readLogAtPrio(JNIEnv* env, jobject thz, jstring file, jint prio, jint deleteAfterCall){
	char *filepath = (*env)->GetStringUTFChars(env, file, 0);
	if (checkExists(filepath) == 1)
		return NULL;
	char temp[2048]; //allocating 2kB memories for ram
	//opening file
	FILE* read = fopen(filepath, "r");
	//check signature
	jint index = 0;
	jint maxIdx= GID;
	jint offset, hasNull;
	while(index < maxIdx)
		temp[index++] = dstr(getc(read));
	temp[index] = '\0';
	index = 0;
	if(!cmp(temp, sig))
		return NULL;
	if(integerReference == NULL){
		offset = 31;
		hasNull = 0;
	}
	else {
		jint a = toInt(env, (*env)->GetObjectArrayElement(env, integerReference, 0));
		jint b = toInt(env, (*env)->GetObjectArrayElement(env, integerReference, 1));
		offset = a;
		hasNull = b;
	}
//	if(hasNull)
//		return NULL; //index reaching null character
	fseek(read, offset, 0);
	jobjectArray arr =
		(*env)->NewObjectArray(env, 5, (*env)->FindClass(env, "java/lang/Object"), NULL);
	jint chr, d = 0;
	while(1){
		if((chr = getc(read)) == -1)
			break;
		if(chr == prio){
			ReaderLog(env, read, temp, chr, arr, &index, &hasNull, &offset, &deleteAfterCall);
			return arr;					
		}
		index++;
	}/* LOOP END */
	__android_log_print(ANDROID_LOG_INFO, "CHECK", "NULL IS REACHED");
	offset+=index;
	if(deleteAfterCall){
		if(integerReference!=NULL)
			(*env)->DeleteGlobalRef(env, integerReference);
			integerReference = NULL;
	}
	else {
		if(integerReference == NULL){
			jobjectArray tt = (*env)->NewObjectArray(env, 2, (*env)->FindClass(env, "java/lang/Object"), NULL);
			setObjArr(env, tt, 0, jintToObject(env, offset));//Offset file
			setObjArr(env, tt, 1, jintToObject(env, 1));//hasNull	
			integerReference = (*env)->NewGlobalRef(env, tt);		
		}
		else {	
			setObjArr(env, integerReference, 0, jintToObject(env, offset));//Offset file
			setObjArr(env, integerReference, 1, jintToObject(env, 1));//hasNull	
		}
		mencobaRef(env);
	}
	fclose(read);
	return NULL;
}
JNIEXPORT jobjectArray JNICALL Java_com_mylexz_utils_Logger_readL(JNIEnv* env, jobject thz, jstring file, jint deleteAfterCall){
	char *filepath = (*env)->GetStringUTFChars(env, file, 0);
	if (checkExists(filepath)==1)
		return NULL;
	char temp[2048]; //allocating 2kB memories for ram
	//opening file
	FILE* read = fopen(filepath, "r");
	//check signature
	jint index = 0;
	jint maxIdx= GID;
	jint offset, hasNull;
	while(index < maxIdx)
		temp[index++] = dstr(getc(read));
	temp[index] = '\0';
	index = 0;
	if(!cmp(temp, sig))
		return NULL;
	if(integerReference == NULL){
		offset = 31;
		hasNull = 0;
	}
	else {
		jint a = toInt(env, (*env)->GetObjectArrayElement(env, integerReference, 0));
		jint b = toInt(env, (*env)->GetObjectArrayElement(env, integerReference, 1));
		offset = a;
		hasNull = b;
	}
//	if(hasNull)
//		return NULL; //index reaching null character
	fseek(read, offset, 0);
	jobjectArray arr =
		(*env)->NewObjectArray(env, 5, (*env)->FindClass(env, "java/lang/Object"), NULL);
	jint chr, d = 0;
	//while((chr = getc(read))!= -1){
	if((chr = getc(read)) == -1 ){
		hasNull = 1;
		if(deleteAfterCall){
			if(integerReference!=NULL)
				(*env)->DeleteGlobalRef(env, integerReference);
			integerReference = NULL;
		}
		else {
			if(integerReference == NULL){
				jobjectArray tt = (*env)->NewObjectArray(env, 2, (*env)->FindClass(env, "java/lang/Object"), NULL);
				(*env)->SetObjectArrayElement(env, tt, 0, jintToObject(env, offset));//Offset file
				(*env)->SetObjectArrayElement(env, tt, 1, jintToObject(env, hasNull));//hasNull
				integerReference = (*env)->NewGlobalRef(env, tt);
			}
			else{
				(*env)->SetObjectArrayElement(env, integerReference, 0, jintToObject(env, offset));//Offset file
				(*env)->SetObjectArrayElement(env, integerReference, 1, jintToObject(env, hasNull));//hasNull	
			}
			mencobaRef(env);
		}
		return NULL;
	}
	ReaderLog(env, read, temp, chr, arr, &index, &hasNull, &offset, &deleteAfterCall);
	fclose(read);
	return arr;
}
jint size(const char s[]){
	jint x = 0;
	while(s[x++]!='\0');
	return x-1;
}
JNIEXPORT void JNICALL Java_com_mylexz_utils_Logger_writeLog(JNIEnv *env, jobject thz,
							jstring file, jint prio, jlong millis, jlong date,
							jstring packageName, jstring nameId, jstring content) 
{
	/******* Convert jlong millis and date to string ******/
	char strMs[16], strDt[16];
	sprintf(strMs, "%lld", millis);
	sprintf(strDt, "%lld", date);
	/******* Convert all string to C-style string ******/
	char *files = (*env)->GetStringUTFChars(env, file, 0);
	char *pack = (*env)->GetStringUTFChars(env, packageName, 0);
	char *TAG = (*env)->GetStringUTFChars(env, nameId, 0);
	char *contentd = (*env)->GetStringUTFChars(env, content, 0);
	/*********** Get all size string *************/
	jsize lpack = (*env)->GetStringUTFLength(env, packageName);
	jsize lTAG = (*env)->GetStringUTFLength(env, nameId);
	jsize lcontentd = (*env)->GetStringUTFLength(env, content);
	jsize lstrMs = size(strMs);
	jsize lstrDt = size(strDt);
	/*********** Allocate memory for new string ********/
	char tmp[(lpack+lTAG+lcontentd+lstrMs+lstrDt+7)];
	/*********** Querying *************/
	jint x = 0;
	tmp[x++] = prio;
	tmp[x++] = pemisah;
	//TAG
	jint y = 0;
				__android_log_print(ANDROID_LOG_INFO, "CheckBeforeSave", "value of tag %s, packageName %s", TAG, pack);
	while(y < lTAG)
		tmp[x++] = estr(TAG[y++]);
	tmp[x++] = pemisah;
	//package
	y = 0;
	while(y < lpack)
		tmp[x++] = estr(pack[y++]);
	tmp[x++] = pemisah;
	
	//date
	y = 0;
	while(y < lstrDt)
		tmp[x++] = enm(strDt[y++]);
	tmp[x++] = pemisah;
	//time
	y = 0;
	while(y < lstrMs)
		tmp[x++] = enm(strMs[y++]);
	tmp[x++] = pemisah;
	//content
	y = 0;
	while(y < lcontentd)
		tmp[x++] = estr(contentd[y++]);
	tmp[x++] = last;
	tmp[x++] = '\n';
	tmp[x] = '\0';
	//Release Strings
	(*env)->ReleaseStringUTFChars(env, packageName, pack);
	(*env)->ReleaseStringUTFChars(env, nameId, TAG);
	(*env)->ReleaseStringUTFChars(env, content, contentd);
	/*** Opening file****/
	if(checkExists(files) == 0){
		FILE *f;
		f = fopen(files, "a+");
		fprintf(f, "%s", tmp);
		fclose(f);
	}
	else {
		FILE *f;
		f = fopen(files, "a+");
		char *n = "MyLogger";
		char *z = "@ Lcom/mylexz \n";
		char w[8];
		int y = 0;
		for (;n[y] != '\0'; y++)
			w[y] =estr( n[y]);
		w[y] = '\0';

		fprintf(f, "%s , ^^^ %s\n%s", w, z, tmp);
		fclose(f);
	}
	//Release
	
	(*env)->ReleaseStringUTFChars(env, file, files);
	
}
	
