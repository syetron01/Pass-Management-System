package com.example.zoo_passes_management_sys;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class PassesManagementSystem {
    private Queue<Customer> generalCustomers; // FIFO for general customers
    private Stack<Customer> vipCustomers; // LIFO for VIP Customers
    private int totalGeneralServed;
    private int totalVIPServed;

    public PassesManagementSystem() {
        generalCustomers = new LinkedList<>();
        vipCustomers = new Stack<>();
        totalGeneralServed = 0;
        totalVIPServed = 0;
    }

    // Add general customer to queue
    public void addGeneralCustomer(String name) {
        Customer customer = new Customer(name, "General");
        generalCustomers.offer(customer);
    }

    // Add VIP customer to stack
    public void addVIPCustomer(String name) {
        Customer customer = new Customer(name, "VIP");
        vipCustomers.push(customer);
    }

    // Serve VIP customer (LIFO)
    public Customer serveVIPCustomer() {
        if (vipCustomers.isEmpty()) {
            return null;
        }
        Customer served = vipCustomers.pop();
        totalVIPServed++;
        return served;
    }

    // Serve general customer (FIFO)
    public Customer serveGeneralCustomer() {
        if (generalCustomers.isEmpty()) {
            return null;
        }
        Customer served = generalCustomers.poll();
        totalGeneralServed++;
        return served;
    }

    // Check if VIP stack is empty
    public boolean isVIPEmpty() {
        return vipCustomers.isEmpty();
    }

    // Check if general queue is empty
    public boolean isGeneralEmpty() {
        return generalCustomers.isEmpty();
    }

    // Peek at next VIP customer
    public Customer peekVIPCustomer() {
        if (vipCustomers.isEmpty()) {
            return null;
        }
        return vipCustomers.peek();
    }

    // Peek at next general customer
    public Customer peekGeneralCustomer() {
        if (generalCustomers.isEmpty()) {
            return null;
        }
        return generalCustomers.peek();
    }

    // Get all general customers
    public Queue<Customer> getGeneralCustomers() {
        return generalCustomers;
    }

    // Get all VIP customers
    public Stack<Customer> getVIPCustomers() {
        return vipCustomers;
    }

    // Get total general customers served
    public int getTotalGeneralServed() {
        return totalGeneralServed;
    }

    // Get total VIP customers served
    public int getTotalVIPServed() {
        return totalVIPServed;
    }

    // Get total customers remaining
    public int getTotalCustomersRemaining() {
        return generalCustomers.size() + vipCustomers.size();
    }

    // Get total general customers remaining
    public int getGeneralCustomersRemaining() {
        return generalCustomers.size();
    }

    // Get total VIP customers remaining
    public int getVIPCustomersRemaining() {
        return vipCustomers.size();
    }
}