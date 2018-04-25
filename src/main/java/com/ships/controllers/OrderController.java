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
	public String getOrders(Model s)
	{
		ArrayList<OrderInfo> orderList = orderInfoService.listAll();
		
		s.addAttribute("orderList", orderList);
		
		return "showOrders";
	}
	
	// Create order method (GET)
	@RequestMapping(value = "/addOrder", method = RequestMethod.GET)
	public String getOrder(@ModelAttribute("orderAdd") OrderInfo o, HttpServletRequest h, Model m) 
	{
		ArrayList<Ship> ships = shipService.listAll();
		
		Map<Long,String> orderShipList = new HashMap<Long,String>();
		
		// For every ship
		for (Ship s : ships)
		{	
			// Insert into orderList
			orderShipList.put((long) s.getSid(), s.getName() + ", Cost = " + s.getCost());
		}
		
		m.addAttribute("shippingList", orderShipList);
		
		ArrayList<ShippingCompany> companies = shippingCompanyService.listAll();
		
		Map<Long,String> companyList = new HashMap<Long,String>();
		
		for (ShippingCompany sc : companies) 
		{	
			companyList.put((long) sc.getScid(), sc.getName() + ", Balance = " + sc.getBalance());
		}
		
		m.addAttribute("orderCompanyList", companyList);
				
		return "addOrder";
	}
	
	// Create order method (POST)
	@RequestMapping(value = "/addOrder", method = RequestMethod.POST)
	public String addShip(@Valid @ModelAttribute("orderAdd") OrderInfo o, BindingResult result, HttpServletRequest h, Model s) 
	{
		// If there are errors, refresh the page so the errors can be displayed
		if (result.hasErrors())
		{
			System.out.println("Errors have occured");
			return "addOrder";
		}
		
		// If there are NO errors, proceeed to add the order
		else 
		{
			shipService.addShip(o.getShip());
			orderInfoService.addOrder(o);
			
			ArrayList<OrderInfo> orders = orderInfoService.listAll();
	
			s.addAttribute("orders", orders);
		
			return "redirect:showOrders";
		}
	}
}
