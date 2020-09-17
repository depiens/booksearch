# BookSearch
> BookSearch Android version (by Kotlin)

BookSearch는 카카오 도서 API를 이용해 도서 검색하는는 앱입니다.


## Dependencies
* [OkHttp](http://square.github.io/okhttp/) for Http connections
* [Retrofit](http://square.github.io/retrofit/) for Http API
* [Glide](https://github.com/bumptech/glide/) for Glide

## Environment
* macOS Catalina 10.15.3
* Android Studio 4.0.1

## How to
1. 요구사항 정리
    * 카카오 도서 검색 API를 이용하여 도서 검색
        - size를 50으로 설정해, page당 50권이 검색되도록 개발
    * 스크롤시 paging 기능을 제공해 연속적으로 검색하는 기능 제공
    * 검색 리스트 결과(메인화면) 및 상세화면으로 구성
        - 상단에 도서명을 입력할 수 있는 입력창을 제공해 도서명이 변경되면 해당 도서명으로 검색 결과 노출
        - 메인 리스트와 상세 화면은 모두 Fragment로 구성
        - 메인 리스트에서 특정 item(도서) 클릭시 상세화면 Fragment로 이동
    * **개발언어는 꼭 Kotlin을 사용**
        
2. 목표 설정
    * 새로운 언어 Kotlin 습득 및 일정준수
        - 적은 가용 시간에 요구사항 구현을 위한 필요한 부분 발췌 및 습득 필요
        - Kotlin 기본 문법
        - Kotlin에서 RecyclerView 사용 방법
        - 비동기 통신 방법
        - 카카오 REST API 사용 방법

3. 개발 및 테스트 반복
    1. 메인 리스트와 상세 화면 Fragment 구현
        - 상세 화면 이동 및 뒤로가기 구현
    2. kakao developers 가입 및 도서 검색 REST-API 테스트
        - 도서 검색 In/Out 포맷 확인
    3. REST-API 비동기 통신 구현
        - 도서 검색 Thread/Handler 비동기 방식으로 HttpURLConnecttion 처리
        - Thumbnail image AsyncTask 비동기 방식으로 URL Stream 처리
        - 오류 처리 추가
    4. Code Refactoring
        - 도서 검색 REST-API Retrofit으로 변경
        - Thumbnail image load Glide로 변경
        
## APK File
```
bin/BookSearch.apk
```
