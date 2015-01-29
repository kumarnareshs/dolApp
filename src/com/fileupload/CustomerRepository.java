package com.fileupload;

import com.strongloop.android.loopback.UserRepository;


 
public class CustomerRepository extends UserRepository<Customer> {
    public interface LoginCallback extends UserRepository.LoginCallback<Customer> {
    }
 
     public CustomerRepository() {
        super("customer", "Customers", Customer.class);
     }
}