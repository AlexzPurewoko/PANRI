/*
DO NOT MODIFY THIS FILE!!!
EXCEPT YOU AS AUTHOR OF THIS FILE


*/
#include <string.h>
#include <stdlib.h>
#include "MyLEXZ-Cutils/component.h"
/*
MORSE ARRAY CHARACTERS
*/
static char *lmorse[] = {
	/* EISH */
	".", "..", "...", "....",
	/* TMO KH */
	"-", "--", "---", "----",
	/* KX RP*/
	"-.-", "-..-", ".-.", ".--.",
	/*AUV NDB*/
	".-", "..-", "...-", "-.", "-..", "-...",
	/*WFY GLQ*/
	".--", "..-.", "-.--",  "--.", ".-.."
	, "--.-",
	/* CJZ */
	"-.-.", ".---", "--..",
	/* NUMBER CODES */
	/* 1234567890*/
	".----", "..---", "...--", "....-", ".....", 
	"-....", "--...", "---..", "----.", "-----",
	"\0"
};
/*
ARRAY OF ALL CHARACTERS A - Z(LOWER)
*/
static char *lchr[] = {
	"e", "i", "s", "h",
	"t", "m", "o", "kh",
	"k", "x", "r", "p",
	"a", "u", "v", "n", "d", "b",
	"w","f","y","g","l","q",
	"c","j","z",
	"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
	"\0"
};
int __fdL(char *str[], char t[]){
	int x;
	for (x = 0; str[x] != "\0" && !cmp(str[x], t); x++);
	if (str[x] == "\0")return -1;
	return x;
}
int __aToM(const char *s){
	int x = 0;
	int sum = 0;
	char t[3];
	for (;*(s+x) != '\0'; x++){
		if(toLower(*(s+x))== 'k'&& toLower(*(s+(x+1))) == 'h'){
			t[0] = 'k';
			t[1] = 'h';
			t[2] = '\0';
			x++;			
		}
		else if(*(s+x) == ' '){
			sum+=2;
			continue;
		}
		else {
			t[0] = toLower(*(s+x));
			t[1] = '\0';
		}
		int line = __fdL(lchr, t);
		
		if(line == -1)sum++;
		else 
			sum += (strlen(lmorse[line])+1);
	}
	//printf("%i",sum);
	return sum;
}

void ccat(char s[], char t[], int sp, int *p){
	if(s[sp] == '\0')return;
	else {
		t[*p] = s[sp];
		*p = *p + 1;
		sp++;
		ccat(s, t, sp, p );
	}
}
int cm(int a, int b){return a==b;}
int _____alloc_memToStr(const char *s){
	int idx, strp = 0;
	int count;
	char tmp;
	for(idx = count = 0; *s != '\0'; s++){
		
		if (*s == ' '){
			tmp = *(s+1);
			/*if (tmp == '\0')
			    if(idx != 0) {
			    	count++;
			    	break;
			    }
			else*/ 
			
			if(tmp == ' '){
				count++;
				s++;
			}
			if (strp == 4)strp=0;
			else if(idx != 0)count++;
			idx = 0;
		}
		else if(*s == '-'){
			strp++;
			idx++;
			tmp = *(s+1);
			if (tmp == '\0' || tmp == ' '){
				if (strp == 4) count+=2;
				else if (tmp == '\0' ) count++;
			}
			
		}
		else if (*s == '.'){
			tmp = *(s+1);
			idx++;
			strp= 0;
			if (tmp == '\0') count++;
			
		}
	}
	return count;
}

int len(const char s[]){
	int m = 0;
	while (s[m++] != '\0');
	return --m;
}
/*
char *ToStr(const char *s){
	
}*/
