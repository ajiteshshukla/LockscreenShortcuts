#!/bin/sh

i=0
while true
do
    adb shell input keyevent 26
    ((i++))
    [ $((i%10)) -eq 0 ] && echo "Total unlocks : $i"
done

