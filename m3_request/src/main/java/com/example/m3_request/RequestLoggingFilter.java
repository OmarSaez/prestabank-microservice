package com.example.m3_request;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Enumeration;

@WebFilter(urlPatterns = "/api/*") // Filtrar solo las rutas necesarias
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            System.out.println("Request Method: " + httpRequest.getMethod());
            System.out.println("Request URI: " + httpRequest.getRequestURI());

            // Registrar los encabezados de la solicitud
            System.out.println("Request Headers:");
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = httpRequest.getHeader(headerName);
                System.out.println(headerName + ": " + headerValue);
            }
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }




    @Override
    public void destroy() {
        // Lógica de limpieza si es necesaria
    }
}
