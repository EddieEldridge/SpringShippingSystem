package com.ships.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ships.model.OrderInfo;
import com.ships.model.Ship;
import com.ships.model.ShippingCompany;
import com.ships.services.OrderInfoService;
import com.ships.services.ShipService;
import com.ships.services.ShippingCompanyService;

@Controller    // This means that this class is a Controller
public class OrderController 
{
	@Autowired // This means to get the bean called orderInfoService, Which is auto-generated by Spring, we will use it to handle the data
	private OrderInfoService orderInfoService;
	
	@Autowired // This means to get the bean called shipService, Which is auto-generated by Spring, we will use it to handle the data
	private ShipService shipService;
	
	@Autowired // This means to get the bean called shippingCompanyService, Which is auto-generated by Spring, we will use it to handle the data
	private ShippingCompanyService shippingCompanyService;

	// Setup our method to display all orders in the 'order' table
	@RequestMapping(value = "/showOrders", method = RequestMethod.GET)
	public String getOrders(Model model)
	{
		ArrayList<OrderInfo> orderList = orderInfoService.listAll();
		
		model.addAttribute("orderList", orderList);
		
		return "showOrders";
	}
	
	// Create order method (GET)
	@RequestMapping(value = "/addOrder", method = RequestMethod.GET)
	public String getOrder(@ModelAttribute("orderAdd") OrderInfo o, HttpServletRequest h, Model model) 
	{
		ArrayList<Ship> ships = shipService.listAll();
		
		Map<Long,String> orderShipList = new HashMap<Long,String>();
		
		// For every ship
		for (Ship s : ships)
		{	
			if (s.getShippingCompany() == null)
			{
			orderShipList.put((long) s.getSid(), s.getName() + ", Cost = " + s.getCost());
			}
		}
		
		model.addAttribute("shippingList", orderShipList);
		
		// Create an array list of all our shipping companies from our Shipping Company Service
		ArrayList<ShippingCompany> companies = shippingCompanyService.listAll();
		
		Map<Long,String> companyList = new HashMap<Long,String>();
		
		for (ShippingCompany sc : companies) 
		{	
			companyList.put((long) sc.getScid(), sc.getName() + ", Balance = " + sc.getBalance());
		}
		
		model.addAttribute("orderCompanyList", companyList);
				
	
		return "addOrder";
	}
	
	// Create order method (POST)
	@RequestMapping(value = "/addOrder", method = RequestMethod.POST)
	public String addShip(@Valid @ModelAttribute("orderAdd") OrderInfo o, BindingResult result, HttpServletRequest h, Model model) 
	{
		System.out.println("Ship: " +o.getShip());
		System.out.println("Date: " +o.getDate());
		System.out.println("Oid: " +o.getOid());
		System.out.println("Shipping Company: " +o.getShippingCompany());

		// If there are errors, bring the user to the page so the errors can be displayed
		if (o.getShip()==null)
		{
			System.out.println("Ship is null");
			return "orderErrorPage";
		}
		else if(o.getShippingCompany()==null)
		{
			System.out.println("Shipping company is null");
			return "orderErrorPage";
		}
		else if (o.getShip().getCost().compareTo(o.getShippingCompany().getBalance()) == (0|1))
		{
			System.out.println("Ship/Shipping company are null");
			return "orderErrorPage";
		}
		
		// If there are NO errors, proceeed to add the order
		else 
		{
			System.out.println("In Else statement");
			o.getShippingCompany().setBalance(o.getShippingCompany().getBalance().subtract(o.getShip().getCost()));

			orderInfoService.save(o);
			shipService.addShip(o.getShip());
			shippingCompanyService.addShipCompany(o.getShippingCompany());
			
			ArrayList<OrderInfo> orders = orderInfoService.listAll();
	
			model.addAttribute("orders", orders);
		
			return "redirect:showOrders";
		}
	}
}
