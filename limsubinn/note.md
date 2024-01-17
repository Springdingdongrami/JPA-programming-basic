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

# 2. JPA 시작하기

### Hello JPA - 프로젝트 생성

- 프로젝트 생성
    
    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/7c627379-b925-4fb3-9fb8-66b35d6ee9e1)


- 라이브러리 추가 - `pom.xml`
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>jpa-basic</groupId>
        <artifactId>ex1-hello-jpa</artifactId>
        <version>1.0.0</version>
    
        <dependencies>
            <!-- JPA 하이버네이트 -->
            <dependency>
                <groupId>org.hibernate</groupId>
                <artifactId>hibernate-entitymanager</artifactId>
                <version>5.6.15.Final</version>
            </dependency>
            <!-- H2 데이터베이스 -->
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
                <version>2.2.220</version>
            </dependency>
        </dependencies>
    
        <properties>
            <maven.compiler.source>17</maven.compiler.source>
            <maven.compiler.target>17</maven.compiler.target>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        </properties>
    
    </project>
    ```
    

- JPA 설정 파일 - `resources/META-INF/persistence.xml`
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <persistence version="2.2"
                 xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
        <persistence-unit name="hello">
            <properties>
                <!-- 필수 속성 -->
                <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
                <property name="javax.persistence.jdbc.user" value="sa"/>
                <property name="javax.persistence.jdbc.password" value=""/>
                <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
                <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
                <!-- 옵션 -->
                <property name="hibernate.show_sql" value="true"/>
                <property name="hibernate.format_sql" value="true"/>
                <property name="hibernate.use_sql_comments" value="true"/>
                <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
            </properties>
        </persistence-unit>
    </persistence>
    ```
    
    - 데이터베이스 방언
        
        ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/c8e529c3-ee74-4e18-95e5-56e183d656d9)

        - JPA는 특정 데이터베이스에 종속 X
        - 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다르다.
        - 방언: SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능
        - `hibernate.dialect` 속성에 지정
        ****

### Hello JPA - 애플리케이션 개발

- JPA 구동 방식
    
    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/7dd9bc40-af6e-4150-bc80-caacf4aaad79)

- 객체와 테이블을 생성하고 매핑하기
    
    ```java
    package hellojpa;
    
    import javax.persistence.Entity;
    import javax.persistence.Id;
    
    @Entity
    public class Member {
    
        @Id
        private Long id;
        private String name;
        
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
        
    }
    ```
    
    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/aa4b3bd8-de19-4f6e-80a0-bdf87aa12025)

    - `@Entity` : JPA가 관리할 객체
    - `@Id` : 데이터베이스 PK와 매핑

** 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유

** 엔티티 매니저는 스레드 간 공유 X (사용하고 버려야 한다.)

** JPA의 모든 데이터 변경은 트랜잭션 안에서 실행

- JPQL
    - JPA가 제공하는 SQL을 추상화한 객체 지향 쿼리 언어
    - select, from, where, group by, having, join 지원
    - 엔티티 객체를 대상으로 쿼리

# 3. 영속성 관리 - 내부 동작 방식

### 영속성 컨텍스트 1

- JPA에서 가장 중요한 2가지
    - 객체와 관계형 데이터베이스 매핑하기
    - 영속성 컨텍스트

- 영속성 컨텍스트
    - 엔티티를 영구 저장하는 환경
    - 논리적인 개념 - 눈에 보이지 않는다.
    - 엔티티 매니저를 통해서 영속성 컨텍스트에 접근
    - J2SE 환경 - 엔티티 매니저와 영속성 컨텍스트가 1:1

- 엔티티의 생명주기
    
    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/64d48508-a756-404a-85a3-ca660d54a8cc)

    - 비영속(new/transient)
        
        ```java
        // 비영속 - 객체를 생성한 상태
        Member member = new Member();
        member.setId(100L);
        member.setName("HelloJPA");
        ```
        
        - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
    - 영속 (managed)
        
        ```java
        // 영속 - 객체를 저장한 상태
        em.persist(member);
        ```
        
        - 영속성 컨텍스트에 관리되는 상태
    - 준영속 (detached)
        
        ```java
        // 준영속 - 엔티티를 영속성 컨텍스트에서 분리
        em.detach(member);
        ```
        
        - 영속성 컨텍스트에 저장되었다가 분리된 상태
    - 삭제 (removed)
        
        ```java
        // 삭제 - 객체를 삭제한 상태
        em.remove(member);
        ```
        
        - 삭제된 상태

- 영속성 컨텍스트의 이점
    - 1차 캐시
    - 동일성 보장
    - 트랜잭션을 지원하는 쓰기 지연
    - 변경 감지
    - 지연 로딩

### 영속성 컨텍스트 2

- 1차 캐시
    
    ```java
    Member member = new Member();
    member.setId("member1");
    member.setUsername("회원1");
    
    // 1차 캐시에 저장됨
    em.persist(member);
    
    // 1차 캐시에서 조회
    Member findMember = em.find(Member.class, "member1");
    
    // 1차 캐시에 없음 -> 데이터베이스에서 조회 -> 1차 캐시에 저장
    Member findMember2 = em.find(Member.class, "member2");
    ```
    

- 영속 엔티티의 동일성 보장
    
    ```java
    Member a = em.find(Member.class, "member1");
    Member b = em.find(Member.class, "member2");
    
    System.out.println(a == b); // true
    ```
    

- 트랜잭션을 지원하는 쓰기 지연
    
    ```java
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    
    EntityTransaction tx = em.getTransaction();
    tx.begin(); // 트랜잭션 시작
    
    em.persist(memberA);
    em.persist(memberB);
    
    System.out.println("result = " + (findMember1 == findMember2)); // 동일성
    
    tx.commit(); // 커밋하는 순간 데이터베이스에 SQL을 보낸다.
    ```
    
    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/f908bd44-1548-4427-b0c0-9cefcfd24e5d)

    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/a8c759ec-e4d8-46dd-ad5b-e8405acf7695)


- 엔티티 변경 감지 (Dirty Checking)
    
    ```java
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    
    EntityTransaction tx = em.getTransaction();
    tx.begin(); // 트랜잭션 시작
    
    // 영속 엔티티 조회
    Member memberA = em.find(Member.class, "memberA");
    
    // 영속 엔티티 데이터 수정
    memberA.setUsername("hi");
    memberA.setAge(10);
    
    tx.commit(); // 커밋하는 순간 데이터베이스에 SQL을 보낸다.
    ```
    
    ![image](https://github.com/Springdingdongrami/JPA-programming-basic/assets/66028419/25bad3c1-51c8-4df9-a521-177cdd389d0b)

    - `em.update(member)` 와 같은 코드가 필요 없다.

### 플러시

- 플러시
    - 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영

- 플러시 발생
    - 변경 감지
    - 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
    - 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송 (등록, 수정, 삭제 쿼리)

- 영속성 컨텍스트를 플러시하는 방법
    - 직접 호출
        - `em.flush()`
    - 자동 호출
        - 트랜잭션 커밋
        - JPQL 쿼리 실행

- 플러시 모드 옵션
    - `em.setFlushMode(FlushModeType.COMMIT)`
        - `FlushModeType.AUTO` : 커밋이나 쿼리를 실행할 때 플러시 (기본값)
        - `FlushModeType.COMMIT` : 커밋할 때만 플러시

### 준영속 상태

- 준영속 상태
    - 영속 상태의 엔티티가 영속성 컨텍스트에서 분리
    - 영속성 컨텍스트가 제공하는 기능을 사용하지 못한다.

- 준영속 상태로 만드는 방법
    - `em.detach(entity)` : 특정 엔티티만 준영속 상태로 전환
    - `em.clear()` : 영속성 컨텍스트를 완전히 초기화
    - `em.close()` : 영속성 컨텍스트를 종료
