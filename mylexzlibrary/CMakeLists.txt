# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories( /root/AndroidStudioProjects/MyLEXZLibrary/app/src/main/c/MyLEXZ-Cutils )
set( MCUTILS src/main/c/MyLEXZ-Cutils )
set( ALLC src/main/c )

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#FOR EXAMPLING
add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/c/native-lib.c )

#library NODEDATA
add_library( # Sets the name of the library.
             mNDATA

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${ALLC}/com_mylexz_utils_NodeData.c ${MCUTILS}/data_op/NodeData.c ${MCUTILS}/array/cstring.c src/main/c/component.c)

#library LOGGER
add_library( # Sets the name of the library.
             mlogger

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${ALLC}/logger.c ${ALLC}/component.c)

#library PRIMITIVEDATA
add_library( # Sets the name of the library.
             mPDATA

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${ALLC}/com_mylexz_utils_PrimitiveData.c ${MCUTILS}/data_op/PrimitiveData.c ${ALLC}/component.c)


add_library( # Sets the name of the library.
             msandi

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${ALLC}/morse.c ${ALLC}/component.c)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#for exampling
target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
#mNDATA
target_link_libraries( # Specifies the target library.
                       mNDATA

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
#msandi
target_link_libraries( # Specifies the target library.
                       msandi

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

#mLOGGER
target_link_libraries( # Specifies the target library.
                       mlogger

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
#mPDATA
target_link_libraries( # Specifies the target library.
                       mPDATA

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )