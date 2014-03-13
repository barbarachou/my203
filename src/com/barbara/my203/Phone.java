package com.barbara.my203;

public class Phone {
	private String name;
	private String num;
	private String city;
	
	public Phone(String name, String num, String city){
		super();
		this.name = name;
		this.num = num;
		this.city = city;	
	}
	public String getName(){
		return name;
	}
	public String getNum(){
		return num;
	}
	public String getCity(){
		return city;
	}

	@Override
	public String toString(){
		return name + "  @" + city;
	}

}
