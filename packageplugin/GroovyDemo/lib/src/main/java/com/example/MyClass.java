package com.example;

public class MyClass {

    @Override
    public String toString() {
        return "MyClass toString";
    }

    public void print(){
        System.out.print("MyClass to :"+toString());
    }
}
