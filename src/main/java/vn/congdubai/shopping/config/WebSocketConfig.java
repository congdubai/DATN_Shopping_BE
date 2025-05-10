package vn.congdubai.shopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

import vn.congdubai.shopping.service.DataHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getDataHandler(), "/data").setAllowedOrigins("*");
    }

    @Bean
    DataHandler getDataHandler() {
        return new DataHandler();
    }

}