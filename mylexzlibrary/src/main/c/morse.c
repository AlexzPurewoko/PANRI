#include <jni.h>
#include "MORSECHR.H"
#include "MyLEXZ-Cutils/component.h"
JNIEXPORT jstring JNICALL Java_com_mylexz_utils_Sandi_ToMorse(JNIEnv *env, jclass thiz, jstring str){
	char *s = (*env)->GetStringUTFChars(env, str, 0);
	jsize sz = (*env)->GetStringUTFLength(env, str);
	//Allocate the memory
	char tmp[__aToM(s)];
	char t[3];
	int cnt = 0;
	jsize x = 0;
	while(x < sz){
		if(cf(toLower(s[x]), 'k')&& cf(toLower(s[x+1]),'h')){
			t[0] = 'k';
			t[1] = 'h';
			t[2] = '\0';
			x++;			
		}
		else if(s[x] == ' '){
			ccat("  ", tmp, 0, &cnt);
			x++;
			continue;
		}
		else {
			t[0] = toLower(s[x]);
			t[1] = '\0';
		}
		int line = __fdL(lchr, t);
		
		if(line == -1)ccat(t, tmp, 0, &cnt);
		else {
			ccat(" ", tmp, 0, &cnt);
			ccat(  lmorse[line],  tmp, 0, &cnt);
		}
		x++;
	}
	tmp[cnt] = '\0';
	//printf("%s",tmp);
	//printf("%s\n", b);
	(*env)->ReleaseStringUTFChars(env, str, s);
	return (*env)->NewStringUTF(env, tmp);
}

JNIEXPORT jstring JNICALL Java_com_mylexz_utils_Sandi_ToStr(JNIEnv *env, jclass thiz, jstring morse){
	char *s = (*env)->GetStringUTFChars(env, morse, 0);
	int sz = _____alloc_memToStr(s);
	char t[6];
	char tmp[sz];
	jsize w = (*env)->GetStringUTFLength(env, morse);
	jint x;
	int y, cnt;
	x = 0;
	y = cnt = 0;
	while(x < w){
		if (s[x] == '.' || s[x] == '-'){
		    t[y++] = s[x];
		    if((x+1) == w){
		    	t[y] = '\0';
			    int line = __fdL(lmorse,t);
				if(line == -1)ccat(t, tmp, 0, &cnt);
				else
		    	ccat(lchr[line], tmp, 0, &cnt);
		    	y = 0;
		    }
		}
		else if(s[x] == ' '){
			t[y] = '\0';
			int line = __fdL(lmorse,t);
			if(line == -1)ccat(t, tmp, 0, &cnt);
			else
			ccat(lchr[line], tmp, 0, &cnt);
			y = 0;
            if((x+1) >= w)break;
            else /*if(!((x+1)==idx))*/{
                if(s[++x] ==' ')ccat(" ", tmp, 0, &cnt);
                else{
                    continue;}
                    }
            }
            else {
				t[y] = '\0';
				if(len(t) != 0){
					int line = __fdL(lmorse,t);
					if(line == -1)ccat(t, tmp, 0, &cnt);
					else
		    		ccat(lchr[line], tmp, 0, &cnt);	
					y = 0;
				}
				tmp[cnt++] = s[x];
			}
			x++;
	}
	tmp[cnt] = '\0';
	(*env)->ReleaseStringUTFChars(env, morse, s);
	return (*env)->NewStringUTF(env, tmp);
}


void func(char n[], jint chr, jint k, jint v){
	while (k>=v){
			if (chr == 1 ){
				n[k] = '1';
				return;
			}
			else {
				n[k] = (chr % 2) + '0';
				chr/=2;
			}
			k--;
		}
}


JNIEXPORT jstring JNICALL Java_com_mylexz_utils_Sandi_StringToBinary(JNIEnv *env, jclass t, jstring p){
	jint bt = 8;
	char *s = (*env)->GetStringUTFChars(env, p, 0);
	/** Allocate the memory */
	jsize sz = (*env)->GetStringUTFLength(env, p);
	jint allc = ((sz*bt)+(sz-1));
	char tmp[allc];
	const jint q = bt-1;
	/************************/
	/** Allocate the zero space */
	jint a;
	jint b;
	for (a = b = 0; a < sz; a++){
		jint c;
		for(c = 0; c <= q; c++)
			tmp[b+c] = '0';
		b+=c;
		tmp[b++] = ' ';
	}
	tmp[b] = '\0';
	/***************************/
	/** Kueri */
	jint g = 0;
	jint v = 0;
	while ( g< sz){
		func(tmp, s[g], q+v, v);
		v+=(q+2);
		g++;
	}
	tmp[v-1] = '\0';
	/**********/
	(*env)->ReleaseStringUTFChars(env, p, s);
	return (*env)->NewStringUTF(env, tmp);
}


