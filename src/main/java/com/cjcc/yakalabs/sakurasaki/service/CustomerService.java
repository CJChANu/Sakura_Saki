package com.cjcc.yakalabs.sakurasaki.service;

import com.cjcc.yakalabs.sakurasaki.model.Customer;
import com.cjcc.yakalabs.sakurasaki.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;

    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public Customer createCustomer(String firstName, String lastName, String email, String phone) {
        if (customerRepo.existsByEmail(email)) {
            throw new RuntimeException("A customer with this email already exists.");
        }
        Customer customer = new Customer(firstName, lastName, email, phone);
        return customerRepo.save(customer);
    }

    public List<Customer> findAll() {
        return customerRepo.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepo.findById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepo.findByEmail(email);
    }

    public List<Customer> searchByName(String keyword) {
        return customerRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    public Customer updateCustomer(Long id, String firstName, String lastName, String email, String phone) {
        Customer c = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        if (firstName != null && !firstName.isBlank()) c.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) c.setLastName(lastName);
        if (email != null && !email.isBlank()) c.setEmail(email);
        if (phone != null) c.setPhone(phone);
        return customerRepo.save(c);
    }

    public void deleteCustomer(Long id) {
        customerRepo.deleteById(id);
    }
}
