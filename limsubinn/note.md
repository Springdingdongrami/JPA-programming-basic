# 1. JPA 소개

### SQL 중심적인 개발의 문제점

- 패러다임의 불일치
    - 객체와 관계형 데이터베이스의 차이
        - 상속 : 객체는 상속 관계가 있지만, 관계형 DB는 상속 관계가 없다.
        - 연관관계 : 객체는 참조를 사용하지만, 테이블은 외래 키를 사용한다.
        - 데이터 타입
        - 데이터 식별 방법

- SQL 중심적인 개발의 문제점
    - 반복적인 코드
    - 객체 그래프 탐색이 어렵다.
        - 처음 실행하는 SQL에 따라 탐색 범위가 결정된다.
    - 모든 객체를 미리 로딩할 수 없다.
    - 진정한 의미의 계층 분할이 어렵다.

### JPA 소개

- JPA (Java Persistence API) - 자바 진영의 ORM 기술 표준
    - ORM : Object-relational mapping  (객체 관계 매핑)
        - 객체는 객체 대로 설계
        - 관계형 데이터베이스는 관계형 데이터베이스 대로 설계
        - ORM 프레임워크가 중간에서 매핑
    - JPA는 애플리케이션과 JDBC 사이에서 동작
        
        ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/fc8f848a-5f8f-4204-a094-957f4c9c41ed)

        
- JPA를 왜 사용해야 하는가?
    - SQL 중심적인 개발 → 객체 중심적인 개발
    - 생산성
        - 저장 : jpa.persist(member)
        - 조회 : Member member = jpa.find(memberId)
        - 수정 : member.setName(”변경할 이름”)
        - 삭제 : jpa.remove(member)
    - 유지보수
        - 기존 : 필드 변경 시 모든 SQL 수정
        - JPA : 필드만 추가하면 된다. SQL은 JPA가 처리한다.
    - 패러다임의 불일치 해결
    - 성능
        - 1차 캐시와 동일성 보장
        - 트랜잭션을 지원하는 쓰기 지연
        - 지연 로딩 - 객체가 실제 사용될 때 로딩
    - 데이터 접근 추상화와 벤더 독립성
    - 표준
