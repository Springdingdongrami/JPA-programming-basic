package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /**
             * 저장
             */
//            Member member = new Member();
//            member.setId(3L);
//            member.setName("HelloC");
//            em.persist(member);

            /**
             * 조회
             */
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());

            /**
             * 수정
             */
//            Member updateMember = em.find(Member.class, 2L);
//            updateMember.setName("HelloJPA");

            /**
             * JPQL
             */
            List<Member> result = em.createQuery("select m from Member as m", Member.class) // 대상 -> 테이블 X, 객체 O
                            .getResultList();
            for (Member member : result) {
                System.out.println("member.name = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
