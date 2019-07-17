## Explanation
Make Template Engine

## Functions
Read Template & Data Files. 
<br>Make 'output.txt' file which is converted Template to Data.

~~~
Ex. 
Data: [
           {
              "info":{
                  "name":{
                      "family":"YANG",
                      ...
                  },

Template: Family name: <?=USER.info.name.family?>\n
Output: Family name: YANG
~~~

## How To Build & Run
프로젝트를 Clone or Download 합니다.
<br>터미널을 실행시켜 해당 폴더로 이동.
~~~
$ gradle build
~~~

build/libs 경로에 가보면 모든 의존성 라이브러리가 포함된 jar 파일을 확인 할 수 있음.

~~~
nohup java -jar TemplateEngine-0.0.1-SNAPSHOT.jar &
tail -f nohup.out
~~~

해당 폴더내 ouput.txt 파일 생성됨.

## Dev Env
JAVA: 1.8

Dependency: JSON-Simple-1.1.1, JUnit-4.12

Build: Gradle

IDE: IntelliJ

Versioning: GitHub

OS: MacOS

## Etc
Template를 DATA로 변환하는 처리는 각각의 Handler에 위임하였다.
<br> TemplateHandler 인터페이스를 구현한 LineTemplateHandler는 Template을 한줄씩 처리한다. 
<br> 이를 확장하여 ForTemplateHandler를 만들었고 다른 Handler를 추가할 수 있다.

## To Do
JAVA 8 문법으로 리팩토링 하기
<br>자바 정규표현식 사용하기


