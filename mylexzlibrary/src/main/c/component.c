#include "MyLEXZ-Cutils/component.h"
/*
* Convert chacacters into lowerCase
*/
char toLower(int c){
	return (c >= 97 && c<=122)?c:((c>=65 &&c<=90)?c + 'a' - 'A':c);
}
/************/
/*
* cf - >  Comparing character c and t
*/
int cf(const char c, const char t){
	if( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ){
	char tmp = toLower(c);
	return (tmp == t)?1:0;
	}
	return 0;
}
/**
 * This function is used to compare 2 strings 
 * how to use
 * 		cmp(string1, string2)
 * this method will return 1 (true) if string1 and string2
 * have a same character elements and have a same length
 * and will return 0 vice-versa
 */
int cmp(const char s[],  const char t[]){
	int k;
	for (k = 0; t[k] != '\0' && s[k] == t[k]; k++);
	return (k> 0&&t[k]=='\0'&&s[k]=='\0');
}
/**
 * reverse(String_to_reverse);
 * this function is used to reverse string values
 * and return back into its array
 * without returning back 
 */
void reverse(char s[]){
	register int tmp, x = 0;
	size_t len = strlen(s);
	size_t loop = len / 2;
	for (; x < loop; x++){
		tmp = s[x];
		s[x] = s[(len-1)-x];
		s[(len-1)-x] = tmp;
	}
	
}

/**
 * This function is used to check the file is exists or not
 * how to use
 * checkExists(filename<string>);
 * This method will return 0 if no error occurs if searching the file
 * and the file is exists in directory
 * return 1 if the file isn't exists and the error is occurs if searching the file
 */
int checkExists(const char *filename){
    struct stat v;
    errno = 0;
    return (stat(filename, &v) != 0 && errno == ENOENT) ? 1 : 0;
}
double pow(double a, double b){
	return pow(a*a, b-1);
}
/*
double atof(const char *s){
	double val, power;       
     register  int i, sign, __count_e = 0;   
     register short __has_e = 0, _sign = 1;     
     for (i = 0; isspace(s[i]); i++)  ;        
     sign = (s[i] == '-') ?  -1 : 1;       
      if (s[i] == '+' || s[i] == '-')i++;        
      for (val = 0.0; isdigit(s[i]); i++)
          val = 10.0 * val + (s[i]  - '0');
      if(s[i] == 'e' || s[i] == 'E'){
      	__has_e = 1;
      	i++;
      	if(s[i] == '+')
      	    _sign = 1;
      	else if( s[i] == '-')
      	    _sign = -1;
      	if(!isdigit(s[i]))i++;
      	for(; isdigit(s[i]); i++)
      	    __count_e = 10 * __count_e + (s[i] - '0');
  	}       
      if (s[i] == '.') i++;
      for (power = 1.0; isdigit(s[i]); i++) {           
      val = 10.0 * val + (s[i]  - '0');
      power *= 10;        
      }  
      if(!__has_e )
      	if(s[i] == 'e' || s[i] == 'E'){
      	__has_e = 1;
      	i++;
      	if(s[i] == '+')
      	    _sign = 0;
      	else if( s[i] == '-')
      	    _sign = -1;
      	if(!isdigit(s[i]))i++;
      	for(; isdigit(s[i]); i++)
      	    __count_e = 10 * __count_e + (s[i] - '0');
  	    }
      return (sign * val / power) * ((double)pow(10, (__count_e * _sign)));
}*/
long double atollf(const char s[]){        
     long double val, power;       
     register  int i, sign, __count_e = 0;   
     register short __has_e = 0, _sign = 1;     
     for (i = 0; isspace(s[i]); i++)  ;        
     sign = (s[i] == '-') ?  -1 : 1;       
      if (s[i] == '+' || s[i] == '-')i++;        
      for (val = 0.0; isdigit(s[i]); i++)
          val = 10.0 * val + (s[i]  - '0');
      if(s[i] == 'e' || s[i] == 'E'){
      	__has_e = 1;
      	i++;
      	if(s[i] == '+')
      	    _sign = 1;
      	else if( s[i] == '-')
      	    _sign = -1;
      	if(!isdigit(s[i]))i++;
      	for(; isdigit(s[i]); i++)
      	    __count_e = 10 * __count_e + (s[i] - '0');
  	}       
      if (s[i] == '.') i++;
      for (power = 1.0; isdigit(s[i]); i++) {           
      val = 10.0 * val + (s[i]  - '0');
      power *= 10;        
      }  
      if(!__has_e )
      	if(s[i] == 'e' || s[i] == 'E'){
      	__has_e = 1;
      	i++;
      	if(s[i] == '+')
      	    _sign = 0;
      	else if( s[i] == '-')
      	    _sign = -1;
      	if(!isdigit(s[i]))i++;
      	for(; isdigit(s[i]); i++)
      	    __count_e = 10 * __count_e + (s[i] - '0');
  	    }
  	
      
      return (sign * val / power) * ((double)pow(10, (__count_e * _sign)));
} 
