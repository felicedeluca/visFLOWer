package controllers;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
@RequestMapping("/*")
public class GeoFlowWebController {
	
	@RequestMapping(value = "map", method = RequestMethod.GET)
	public String loginUtente(HttpServletRequest h,HttpServletResponse r){
	
		System.out.println("mappato");
		
		
		return "Ciao";
	}
	
	@RequestMapping("/something")
	public ResponseEntity<String> handle(HttpEntity<byte[]> requestEntity) throws UnsupportedEncodingException {
	    
		System.out.println("test");
	    return null;
	}
}
