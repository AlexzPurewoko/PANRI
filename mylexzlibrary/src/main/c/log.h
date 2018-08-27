
/*
 * Copyright (C) 2017 Alexzander Purwoko Widiantoro
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
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *	@(#)Log.h	v2 @@ HEADER FILE @@
 * Last updated in : Tuesday Nov, 21, 2017 in 04:35 AM
 * Time Zone : Jakarta, GMT+7 , WIB
 */
/* HEADER FILES */
#ifndef LOG_H
#define LOG_H
#include <time.h>
#include <sys/timeb.h>
#include "component.h"
/** DO NOT MODIFY STRUCT MEMBERS BECAUSE OF FUNCTIONALITY */

typedef struct __time {
	int _millis;
	int _sec;
	int _min;
	int _hours;
} __time;
typedef struct __date {
	int _day;
	int _month;
	int _years;
} __date;
typedef struct __logs {
	int prio;
	struct __date mDate;
	struct __time mTime;
	char proc_name[32];
	char *tag;
	char *content;
} __logs;

/************** LOG PRIORITY *****************/
static enum PRIORITY {
	ERRORS 	= 0x0000a,
	INFO	= 0x0000b,
	DEBUG 	= 0x0000c,
	VERBOSE = 0x0000d,
	WARNING = 0x0000e,
	FAILURE = 0x0000f
}PRIORITY;
typedef struct __slog {
	int _log_err;
	char _logBuff[8192];
	char proc[32];
	char tag[32];
	char _logFile[1024];
	short _lockPointer;
	FILE* rdL;
	struct __logs current;
}LOG;
LOG* openl(char file[], char proc[]);//
void setProccess(LOG* log, const char *proc);//
void writeLog(LOG* log, struct __logs* data);//
void writeL(LOG* log, int priority, char *tagS, char *content, ...);//
int logError(LOG* log);//
struct __logs* readL(LOG* log);//
void readS(LOG* log, char res[]);//
struct __logs* read_withPrio(LOG* log, int priority);//
void readS_withPrio(LOG* log, int priority, char res[]);//
void reset(LOG* log);//
void closel(LOG* log);//
size_t countLogs(LOG* log);//
void clearLogs(LOG* log);
#endif /* LOG_H */
