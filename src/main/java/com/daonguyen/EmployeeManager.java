package com.daonguyen;

import com.daonguyen.entity.Employee;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.util.Iterator;
import java.util.List;

public class EmployeeManager {

    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }

        EmployeeManager employeeManager = new EmployeeManager();

        // Add few employees into database
        Integer empID1 = employeeManager.addEmployee("Ty", "Le", 1000);
        Integer empID2 = employeeManager.addEmployee("Teo", "Tran", 3000);
        Integer empID3 = employeeManager.addEmployee("Tin", "Nguyen", 2000);
        Integer empID4 = employeeManager.addEmployee("To", "Dinh", 4000);

        // List of all employees
        employeeManager.listEmployees();

        // Update employee's records
        employeeManager.updateEmployee(empID1, 5000);

        // Delete an employee from the database
        employeeManager.deleteEmployee(empID2);

        // List down new list of the employees
        employeeManager.listEmployees();
    }

    public Integer addEmployee(String fname, String lname, int salary) {
        Session session = factory.openSession();
        Transaction trans = null;
        Integer empID = null;

        try {
            trans = session.beginTransaction();
            Employee employee = new Employee(fname, lname, salary);
            empID = (Integer) session.save(employee);
            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return empID;
    }

    public void listEmployees() {
        Session session = factory.openSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            Query query = session.createQuery("FROM Employee");
            List data = query.list();

            for (Iterator iterator = data.iterator(); iterator.hasNext();) {
                Employee employee = (Employee) iterator.next();
                System.out.print("First name: " + employee.getFirstName());
                System.out.print(" | Last name: " + employee.getLastName());
                System.out.println(" | Salary: " + employee.getSalary());
            }

            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void updateEmployee(Integer EmployeeID, int salary) {
        Session session = factory.openSession(new MyInterceptor());
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            Employee employee = (Employee) session.get(Employee.class, EmployeeID);
            employee.setSalary(salary);
            session.update(employee);
            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteEmployee(Integer EmployeeID) {
        Session session = factory.openSession(new MyInterceptor());
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            Employee employee = (Employee) session.get(Employee.class, EmployeeID);
            session.delete(employee);
            trans.commit();
        } catch (HibernateException e) {
            if (trans != null)
                trans.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
