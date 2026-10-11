package main.JavaFrame.web.annotations.controller;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelAttribute {
   
}
