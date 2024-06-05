#!/bin/bash

# 현재 날짜와 시간을 YYMMDD(AM/PM)HHMM 형식으로 설정
current_time=$(date +'%y%m%d%p%H%M')

# Docker 이미지 빌드 명령 실행
docker build --no-cache -t dalbodeule/dnsapi:$current_time -t dalbodeule/dnsapi:latest .