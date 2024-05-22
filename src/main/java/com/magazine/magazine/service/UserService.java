package com.magazine.magazine.service;
import com.magazine.magazine.model.Orders;
import com.magazine.magazine.model.Products;
import com.magazine.magazine.model.Users;
import com.magazine.magazine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(Users user) {
        // Verifica se o ID do usuário é nulo antes de prosseguir
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Optional<Users> existingUserOpt = userRepository.findById(user.getUserId());
        if (existingUserOpt.isPresent()) {
            Users existingUser = existingUserOpt.get();
            existingUser.setName(user.getName());
            existingUser.setOrders(user.getOrders());
            userRepository.save(existingUser);
        } else {
            userRepository.save(user);
        }
    }


    @Transactional
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }


    @Transactional
    public Users getUserById(Integer userId) {
         return userRepository.findById(userId).orElse(null);
    }


    @Transactional
    public  List<Users> getUsersByDateRange(Integer startDate, Integer endDate) {
        String startDateString = (String.valueOf(startDate));
        String endDateString = (String.valueOf(endDate));

        return userRepository.findByDateBetween(startDateString, endDateString);
    }


    public Users parseLineUser(String line, Map<Integer, Users> userMap) {
        String userIdStr = line.substring(0, 10).trim();
        String name = line.substring(11, 55).trim();

        int userId = Integer.parseInt(userIdStr);
        Users user = userMap.getOrDefault(userId, new Users());
        user.setUserId(userId);
        user.setName(name);
        return user;
    }

    public Orders parseLineOrders(String line, Map<Integer, Map<Integer, Orders>> orderMap) {
        String userIdStr = line.substring(0, 10).trim();
        String orderIdStr = line.substring(56, 65).trim();
        String totalStr = line.substring(76, 87).trim();
        String dateStr = line.substring(87, 95).trim();

        int userId = Integer.parseInt(userIdStr);
        int orderId = Integer.parseInt(orderIdStr);
        double total = Double.parseDouble(totalStr);
        String date = dateStr; // Assuming date is a string. Convert to Date if necessary.

        Orders order = orderMap
                .computeIfAbsent(userId, k -> new HashMap<>())
                .getOrDefault(orderId, new Orders());
        order.setOrderId(orderId);
        order.setTotal(BigDecimal.valueOf(total));
        order.setDate(date);
        return order;
    }

    public Products parseLineProducts(String line, Map<Integer, Map<Integer, Products>> productMap) {
        String orderIdStr = line.substring(56, 65).trim();
        String productIdStr = line.substring(66, 75).trim();
        String valueStr = line.substring(76, 87).trim();

        int orderId = Integer.parseInt(orderIdStr);
        int productId = Integer.parseInt(productIdStr);
        double value = Double.parseDouble(valueStr);

        Products product = productMap
                .computeIfAbsent(orderId, k -> new HashMap<>())
                .getOrDefault(productId, new Products());
        product.setProductId(productId);
        product.setProductValue(BigDecimal.valueOf(value));
        return product;
    }

    private int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // Log a mensagem de erro e retornar um valor padrão ou lançar uma exceção customizada
            System.err.println("Erro ao converter string para inteiro: " + str);
            return 0; // Ou lançar uma exceção
        }
    }


    private BigDecimal parseBigDecimalSafe(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            // Log a mensagem de erro e retornar um valor padrão ou lançar uma exceção customizada
            System.err.println("Erro ao converter string para BigDecimal: " + str);
            return BigDecimal.ZERO; // Ou lançar uma exceção
        }
    }


    private BigDecimal totalValorListaProdutos (List<Products> product){
        BigDecimal total = BigDecimal.ZERO;
        for (Products productValor : product) {
            total = total.add(productValor.getProductValue());
        }
        return total;
    }



    private String formataData(String data) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(data);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Retorna uma string vazia se a data não puder ser formatada
        }
    }
}