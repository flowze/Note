package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    private final Integer notFoundStatus = HttpStatus.NOT_FOUND.value();

    @RequestMapping("/error")
    @SneakyThrows
    public String handleError(final HttpServletRequest request) {
        final Object statusCodeObject = request.getAttribute("javax.servlet.error.status_code");
        final Integer statusCode = (statusCodeObject instanceof Integer) ? (Integer) statusCodeObject : 0;

        if (statusCode.equals(this.notFoundStatus)) {
            return "redirect:/";
        }
        return "redirect:/";
    }
}