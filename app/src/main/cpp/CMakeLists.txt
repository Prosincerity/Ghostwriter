cmake_minimum_required(VERSION 3.10)
project(ghostwriter_ipa LANGUAGES C CXX)

# eSpeak NG 1.52 has a CMake minimum older than CMake 4 accepts by default.
set(CMAKE_POLICY_VERSION_MINIMUM 3.5)
set(USE_MBROLA OFF CACHE BOOL "" FORCE)
set(USE_LIBSONIC OFF CACHE BOOL "" FORCE)
set(USE_LIBPCAUDIO OFF CACHE BOOL "" FORCE)
set(USE_KLATT OFF CACHE BOOL "" FORCE)
set(USE_SPEECHPLAYER OFF CACHE BOOL "" FORCE)
set(USE_ASYNC OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
# The upstream 1.52 dependency script fetches Sonic even when USE_LIBSONIC is OFF.
# Provide inert probe results; the disabled feature never links or includes it.
set(SONIC_LIB disabled CACHE STRING "Unused Sonic probe result" FORCE)
set(SONIC_INC "${CMAKE_CURRENT_SOURCE_DIR}" CACHE PATH "Unused Sonic probe result" FORCE)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../../../../third_party/espeak-ng
                 ${CMAKE_CURRENT_BINARY_DIR}/espeak-ng EXCLUDE_FROM_ALL)

add_library(ghostwriter_ipa SHARED ipa_jni.c)
target_link_libraries(ghostwriter_ipa PRIVATE espeak-ng)
target_compile_options(ghostwriter_ipa PRIVATE -fvisibility=hidden -ffunction-sections -fdata-sections)
target_compile_options(espeak-ng PRIVATE -ffunction-sections -fdata-sections)
target_link_options(ghostwriter_ipa PRIVATE -Wl,--gc-sections -Wl,--exclude-libs,ALL)
