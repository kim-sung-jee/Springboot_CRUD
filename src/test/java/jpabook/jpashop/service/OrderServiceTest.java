package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Test
    public void 상품주문() throws Exception{
        Member member = createMember();
        Item book = createBook();

        int orderCount=2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);


        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER,getOrder.getStatus());
        assertEquals(1,getOrder.getOrderItems().size());
        assertEquals(10000*orderCount,getOrder.getTotalPrice());
        assertEquals(8,book.getStockQuantity());


    }


    @Test
    public void 상품주문_재고수량초과() throws Exception{
        Member member = createMember();
        Item book = createBook();


        int orderCount= 11;
        try {
            orderService.order(member.getId(), book.getId(), orderCount);
        }catch (NotEnoughStockException e){
            return;
        }

        fail("재고 수량 부족 예외가 발생해야 한다.");

    }

    @Test
    public void 주문취소() throws Exception{

        Member member=createMember();
        Item book=createBook();
        int orderCount=2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        orderService.cancelOrder(orderId);


        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals(10,book.getStockQuantity());

    }



    private Item createBook() {
        Item book=new Book();
        book.setName("시골 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member=new Member();
        member.setName("회원");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }
}