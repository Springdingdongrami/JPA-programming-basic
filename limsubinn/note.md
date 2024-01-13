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
