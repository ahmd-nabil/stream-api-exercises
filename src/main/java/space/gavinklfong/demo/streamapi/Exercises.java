package space.gavinklfong.demo.streamapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Ahmed Nabil
 */
@Service
@RequiredArgsConstructor
public class Exercises {

    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;

    void solve() {
        // Use .peek() for debugging

        // Exercise 1 — Obtain a list of products belongs to category “Books” with price > 100
        productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equals("Books"))
                .filter(product -> product.getPrice() > 100)
                .toList();


        // Exercise 2 — Obtain a list of order with products belong to category “Baby”
        orderRepo.findAll()
                .stream()
                .filter(order -> order.getProducts().stream().anyMatch(product -> product.getCategory().equals("Baby")))
                .toList();


        // Exercise 3 — Obtain a list of product with category = “Toys” and then apply 10% discount
        productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equals("Toys"))
                .peek(product -> product.setPrice(product.getPrice() * .9));

        //Exercise 4 — Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021
        orderRepo.findAll()
                .stream()
                .filter(order -> order.getCustomer().getTier() == 2)
                .filter(order -> !order.getOrderDate().isBefore(LocalDate.of(2021, 2, 1)))
                .filter(order -> !order.getOrderDate().isAfter(LocalDate.of(2021, 4, 1)))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .toList();

        // Exercise 5 — Get the cheapest product of “Books” category
        Product p= productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equals("Books"))
                .min(Comparator.comparing(Product::getPrice))
                .orElse(null);

        // Exercise 6 — Get the 3 most recent placed order
        orderRepo.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate))
                .limit(3)
                .toList();

        // Exercise 7 — Get a list of orders which were ordered on 15-Mar-2021,
        // log the order records to the console and then return its product list
        orderRepo.findAll()
                .stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .peek(System.out::println)
                .toList();

        // Exercise 8 — Calculate total lump sum of all orders placed in Feb 2021
        orderRepo.findAll()
                .stream()
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021,2,1)) >= 0)
                .filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .peek(order -> System.out.println("Orders in feb, 2021 :: " + order.getOrderDate()))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .toList();

        // Exercise 9 — Calculate order average payment placed on 15-Mar-2021
        Function<Order, Double> orderToPriceFun = order -> order.getProducts().stream().map(Product::getPrice).mapToDouble(i->i).sum();
        orderRepo.findAll()
                .stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .map(orderToPriceFun)
                .mapToDouble(x->x)
                .average();

        // Exercise 10 — Obtain a collection of statistic figures (i.e. sum, average, max, min, count) for all products of category “Books”
        productRepo.findAll()
                .stream()
                .filter(product -> product.getCategory().equals("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();

        // Exercise 11 — Obtain a data map with order id and order’s product count
        orderRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Order::getId, order-> order.getProducts().size()));

        // Exercise 12 — Produce a data map with order records grouped by customer
        orderRepo.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(Order::getCustomer));
        System.out.println("Debug");

        // Exercise 13 — Produce a data map with order record and product total sum
        orderRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Function.identity(), order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum()));

        // Exercise 14 — Obtain a data map with list of product name by category
        productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.mapping(Product::getName, Collectors.toList())
                        )
                );

        // Exercise 15 — Get the most expensive product by category
        productRepo.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.maxBy(Comparator.comparing(Product::getPrice))
                        )
                );
    }
}
