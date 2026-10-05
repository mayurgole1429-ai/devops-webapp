package com.devops;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/greet")
public class GreetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("username");
        if (name == null || name.isBlank()) {
            name = "Guest";
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Greeting</title>");
        out.println("<style>body{font-family:Arial,sans-serif;background:#f4f6f8;display:flex;");
        out.println("justify-content:center;align-items:center;height:100vh;margin:0;}");
        out.println(".card{background:#fff;padding:2rem 3rem;border-radius:10px;");
        out.println("box-shadow:0 4px 12px rgba(0,0,0,.15);text-align:center;}</style>");
        out.println("</head><body><div class='card'>");
        out.println("<h1 id='greeting'>Hello, " + escapeHtml(name.trim()) + "!</h1>");
        out.println("<p>Welcome to the DevOps pipeline demo application.</p>");
        out.println("<a href='/'>Back</a>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
