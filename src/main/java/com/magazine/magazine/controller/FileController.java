package com.magazine.magazine.controller;
import com.magazine.magazine.model.Orders;
import com.magazine.magazine.model.Products;
import com.magazine.magazine.model.Users;
import com.magazine.magazine.service.UserService;
import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:8080/api/files/upload")
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable(value = "id") Integer id) {
        Users user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/users/data")
    public ResponseEntity<List<Users>> getUsersByDateRange(
            @RequestParam ("start_date") Integer start_date,
            @RequestParam ("end_date") Integer end_date) {

        System.out.println("dataInicio:  "+start_date);
        System.out.println("dataFim:  "+end_date);
        List<Users> users = userService.getUsersByDateRange(start_date, end_date);
        return ResponseEntity.ok(users);
    }



    @PostMapping("/upload")
    public ResponseEntity<List<Users>> uploadFile(@RequestParam("file") MultipartFile file) {
        List <Users> users  = getUsers(file);

        if(users!=null && !users.isEmpty() ){
            for(Users user: users){
               userService.saveUser(user);
            }
        }

        return ResponseEntity.ok(users);
    }




    private List<Users> getUsers(@RequestParam("file") MultipartFile file) {
        Map<Integer, Users> userMap = new HashMap<>();
        Map<Integer, Map<Integer, Orders>> orderMap = new HashMap<>();
        Map<Integer, Map<Integer, Products>> productMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Users user = userService.parseLineUser(line, userMap);
                Orders order = userService.parseLineOrders(line, orderMap);
                Products product = userService.parseLineProducts(line, productMap);

                userMap.putIfAbsent(user.getUserId(), user);
                orderMap.computeIfAbsent(user.getUserId(), k -> new HashMap<>()).putIfAbsent(order.getOrderId(), order);
                productMap.computeIfAbsent(order.getOrderId(), k -> new HashMap<>()).putIfAbsent(product.getProductId(), product);

                Users existingUser = userMap.get(user.getUserId());
                Orders existingOrder = orderMap.get(user.getUserId()).get(order.getOrderId());

                if (existingUser.getOrders() == null) {
                    existingUser.setOrders(new ArrayList<>());
                }
                if (!existingUser.getOrders().contains(existingOrder)) {
                    existingUser.getOrders().add(existingOrder);
                }

                if (existingOrder.getProducts() == null) {
                    existingOrder.setProducts(new ArrayList<>());
                }
                if (!existingOrder.getProducts().contains(product)) {
                    existingOrder.getProducts().add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new ArrayList<>(userMap.values());
    }



}

