# Fake Data Insert Plugin

IntelliJ IDEA를 위한 데이터베이스 테이블 더미 데이터 생성 플러그인입니다.

## 기능

<!-- Plugin description -->
- 데이터베이스 테이블에 더미 데이터를 손쉽게 생성
- 다양한 데이터 타입 지원 (문자열, 숫자, 날짜 등)
- 다국어 지원 (한국어, 영어)
- 미리보기 기능으로 생성될 데이터 확인 가능
- 직관적인 UI로 손쉬운 사용
- 커스텀 데이터 포맷 지원
<!-- Plugin description end -->

## 설치 방법

### IntelliJ IDEA 마켓플레이스를 통한 설치

1. IntelliJ IDEA에서 `설정(Settings)` > `플러그인(Plugins)` 메뉴로 이동
2. 마켓플레이스 탭에서 "Fake Data Insert" 검색
3. `설치(Install)` 버튼 클릭

### 수동 설치

1. [릴리즈 페이지](https://github.com/timan1802/fakeDataInsert/releases)에서 최신 버전 다운로드
2. IntelliJ IDEA에서 `설정(Settings)` > `플러그인(Plugins)` 메뉴로 이동
3. ⚙️ 아이콘 클릭 후 `디스크에서 플러그인 설치(Install Plugin from Disk...)` 선택
4. 다운로드한 파일 선택

## 사용 방법

1. 데이터베이스 도구 창에서 테이블 선택
2. 마우스 오른쪽 버튼 클릭하여 컨텍스트 메뉴 열기
3. "Generate Fake Data" 메뉴 선택
4. 데이터 생성 옵션 설정
  - 생성할 레코드 수 지정
  - 각 컬럼별 데이터 타입 선택
  - 데이터 형식 커스터마이징
5. 미리보기로 생성될 데이터 확인
6. 생성된 SQL 복사하여 사용

## 주요 기능 스크린샷

![테이블우클릭](https://github.com/user-attachments/assets/526c18b7-92c0-4014-95a3-8ffbb5e8246f)
![최초화면](https://github.com/user-attachments/assets/c15811a1-523e-49f1-b88c-e84f28a43f01)
![옵션](https://github.com/user-attachments/assets/dfc4d073-d1c0-4c9b-97f1-be16116837ce)




## 크레딧

이 플러그인은 다음 라이브러리를 사용합니다:
- [DataFaker](https://github.com/datafaker-net/datafaker) - 더미 데이터 생성 라이브러리
