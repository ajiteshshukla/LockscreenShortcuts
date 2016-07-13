#!/bin/bash

buildVariant="clean build"
extras=""

# Mapping of args to gradle build command
ARGS_MAPPING=( "all:clean build"
        "fast:build --offline -x lint -x test")

while test $# -gt 0
do
    #Converting arg=$1 to lower case to make args case insensitive
    argLowerCase="$(tr [A-Z] [a-z] <<< "$1")"

    variantFound=false
    for item in "${ARGS_MAPPING[@]}" ; 
    do
        key="${item%%:*}"
        if [ "$key" = "$argLowerCase" ] 
            then
                buildVariant="${item##*:}"
                variantFound=true
        fi
    done
    if [ "$variantFound" = false ]
        then
            extras="$extras $1"
    fi
    shift
done



BREW_DIR="/usr/local/Cellar/android-sdk"

function die {
    echo $1
    exit 1
}

[[ -d "$ANDROID_HOME" ]] || export ANDROID_HOME="=/usr/local/opt/android-sdk"

if [ ! -d "$ANDROID_HOME" ]; then
    if [ -d "$BREW_DIR" ]; then
        versions=`cd $BREW_DIR; /bin/ls | sort -u | xargs`
        for v in $versions
        do
            export ANDROID_HOME="$BREW_DIR/$v"
        done
    fi
fi

[[ -d "$ANDROID_HOME" ]] || export ANDROID_HOME="=/Applications/Android\ Studio.app/sdk"

[[ -d "$ANDROID_HOME" ]] || die "ANDROID_HOME not set properly"

CMD_ARGS="$buildVariant $extras"
echo "Executing: ./gradlew $CMD_ARGS"
./gradlew $CMD_ARGS


if [ $? -eq 0 ]; then

    APKS=`find . -name '*.apk' | grep -v 'Test' | xargs`
    echo ""
    echo "Apk signature hashes"
    echo "--------------------"
    for apk in $APKS; do
        hash=$(keytool -list -printcert -jarfile $apk | grep "SHA1: " | cut -d " " -f 3 | xxd -r -p | openssl base64)
        if [ "$hash" = "" ]
            then
                hash="unsigned"
        fi
        echo "$apk : $hash" ;
    done
fi
