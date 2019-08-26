package com.coindesk.main;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Main {

	private static String UserInput(){
		Scanner myObj = new Scanner(System.in);
	    System.out.println("Please enter currency:");
	    return myObj.nextLine();
	}

	public static void main(String[] args) throws MalformedURLException {
			// TODO: SCANNER INPUT FOR SUPPORTED CURRENCIES
			URL u = new URL("https://api.coindesk.com/v1/bpi/supported-currencies.json");
			URLConnection uc;
			String jsonString = "";
			int c;
			Reader reader;
			
			try {
				uc = u.openConnection();
				try (InputStream raw = uc.getInputStream()) {
					 InputStream buffer = new BufferedInputStream(raw);
					 reader = new InputStreamReader(buffer);
					 
					 //TODO: READ BULK TEXT
					 while ((c = reader.read()) != -1) {
						 jsonString += (char) c;
					 }
					 reader.close();	 
					 
					 CoinDeskCT coinDeskCT = new CoinDeskCT(jsonString);
					 while(!coinDeskCT.isCurrencyAvailable(UserInput())){
						 System.out.println("Currency you have entered is not availbale. Please enter another currency or press ESC to exit.");
					 }
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				 // Open the URLConnection for reading 
				u = new URL("https://api.coindesk.com/v1/bpi/currentprice.json");
				uc = u.openConnection();
				jsonString="";
				 try (InputStream raw = uc.getInputStream()) {
					 InputStream buffer = new BufferedInputStream(raw);
					 // chain the InputStream to a Reader
					 reader = new InputStreamReader(buffer);
					 //TODO: READ BULK TEXT
					 while ((c = reader.read()) != -1) {
						 jsonString += (char) c;
					 }
					 reader.close();	 
					 System.out.print(jsonString);
					 
					 Gson gson = new GsonBuilder().create();
					 CoinDeskData data = gson.fromJson(jsonString, CoinDeskData.class);
					 Iterator<Map.Entry<String, Currency>> it = data.getBpi().entrySet().iterator();
					 while (it.hasNext()) {
					     Map.Entry<String, Currency> pair = it.next();
					     System.out.println(pair.getKey() + pair.getValue());
					 }
					 // TODO: GET CURRENT BITCOIN RATE
					 // TODO: PAST 30d LOWEST BITCOIN RATE
					 // TODO: PAST 30d HIGHEST BITCOIN RATE
					 // TODO: TEST HTTP REQUEST
					 // TODO: IMPLEMENT HANDLING OF HTTP RESPONSE CODES
					 // TODO: SET CUSTOM DESERIALIZATION GSON 
					 // TODO: TEST DESERIALIZATION CASES
				 }
			 } catch (MalformedURLException ex) {
				 System.err.println(args[0] + " is not a parseable URL");
			 } catch (IOException ex) {
				 System.err.println(ex);
			 }
		
	}
}

class CoinDeskCT{
	CoinDeskCT(String json){
		Gson gson = new GsonBuilder().create();
		this.setCurrencyTypes(gson.fromJson(json, CurrencyType[].class));
	}
	
	private CurrencyType[] currencyTypes;

	public boolean isCurrencyAvailable(String currencyToCheck) {
		for(int i=0; i<this.currencyTypes.length; i++){
			if(this.currencyTypes[i].getCurrency().equalsIgnoreCase(currencyToCheck))
				return true;
		}		
		return false;
	}
	public CurrencyType[] getCurrencyTypes() {
		return currencyTypes;
	}

	private void setCurrencyTypes(CurrencyType[] currencyTypes) {
		this.currencyTypes = currencyTypes;
	}
}

class CurrencyType {
	CurrencyType(){}
	private String currency;
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	private String country;
}

class CoinDeskData {
	CoinDeskData(){}
	private TimeUpdated time;
	private String disclaimer;
	private Map<String, Currency> bpi;
	public Map<String, Currency> getBpi() {
		return bpi;
	}
}

class TimeUpdated {
	TimeUpdated(){}
	public String updated;
	public String updatedISO;
}

class Currency {
	Currency (){}
	public String code;
	public String symbol;
	public String rate;
	public String description;
	public BigDecimal rate_float;
}